package knitkit

import collection.mutable.{HashMap, ArrayBuffer, Set}

import internal._
import internal.Builder._
import ir._
import VerilogRender._

case class ClkInfo(clock: Option[Expression], reset: Option[Expression])
case class RegInfo(clk_info: ClkInfo, init : Option[Expression])

abstract class RawModule extends BaseModule with HasConditional {
  // Module Local Variables
  val _inst_stmts = ArrayBuffer[Statement]()
  val _wire_eles = Set[Bits]()
  val _wire_as_reg_eles = Set[Bits]()

  val _clks_info = HashMap[ClkInfo, ArrayBuffer[Bits]]()
  val _regs_info = HashMap[Bits, RegInfo]()

  def pushRegInfo(reg: Bits, clk: ClkInfo, reg_info: RegInfo): Unit = {
    if (!_clks_info.contains(clk)) {
      _clks_info(clk) = ArrayBuffer[Bits](reg)
    } else {
      _clks_info(clk) += reg
    }
    if (_regs_info.contains(reg)) {
      Builder.error("Define register twice")
    } else {
      _regs_info(reg) = reg_info
    }
  }

  val _wire_connects = HashMap[Bits, Bits]()
  val _reg_connects  = HashMap[Bits, Bits]()
  def pushConn(lhs: Bits, rhs: Bits, is_reg: Boolean = false): Unit = {
    if (is_reg) {
      _reg_connects += (lhs -> rhs)
    } else {
      _wire_connects += (lhs -> rhs)
    }
  }

  def addWire(e: Bits): Unit =  {
    require(!_closed, "Can't write to module after module close")
    e.binding match {
      case WireBinding(_) =>
        _wire_eles += e
      case _ =>
        error(s"$e is not Wire!")
    }
  }
  def addWireAsReg(e: Bits): Unit =  {
    require(!_closed, "Can't write to module after module close")
    e.binding match {
      case WireBinding(_) =>
        _wire_as_reg_eles += e
      case _ =>
        error(s"$e is not Wire!")
    }
  }

  def pushInst[T <: Instance](inst: T): Unit = {
    require(_closed, "Can't push instance before module close")
    val cur_module = Builder.forcedUserModule
    cur_module._inst_stmts += DefInstance(inst, name, Map())
  }

  def getStatements = {
    require(_closed, "Can't get commands before module close")
    require(_wire_as_reg_eles.subsetOf(_wire_eles), s"${_wire_as_reg_eles} not in ${_wire_eles}" )
    val wire_decl        = _wire_eles.diff(_wire_as_reg_eles).toSeq.sortBy(_._id) map { x =>  DefWire(x.ref) }
    val wire_as_reg_decl = _wire_as_reg_eles.toSeq.sortBy(_._id) map { x => DefWire(x.ref, true) }
    val reg_decl         = _regs_info map { case (e, _) => DefWire(e.ref, true) }

    val wire_assigns = _wire_connects map { case(l, r) => Assign(l.lref, r.ref) }

    val always_blocks = _reg_connects map { case (lhs, rhs) =>
      Always(_regs_info(lhs).clk_info, Seq(Connect(lhs.lref, rhs.ref)))
    }

    wire_decl ++ wire_as_reg_decl ++ reg_decl ++ wire_assigns ++ _inst_stmts.toSeq
  }

  def generateComponent(): Component = {
    require(!_closed, "Can't generate module more than once")
    _closed = true

    val names = nameIds(classOf[RawModule])

    namePorts(names)

    for ((node, name) <- names) {
      node.decl_name = name
      node.suggestName(name)
    }

    // All suggestions are in, force names to every node.
    for (id <- getIds) {
      id match {
        case inst: Instance =>
          inst.forceName(None, default="INST", _namespace)
        case agg: Aggregate =>
          agg.forceName(None, default="AGG", _namespace)
          agg._onModuleClose
        case id: Bits  =>
          if (id.isSynthesizable) {
            id.bindingOpt match {
              case Some(PortBinding(_)) =>
                id.forceName(None, default="PORT", _namespace)
              case Some(RegBinding(_)) =>
                id.forceName(None, default="REG", _namespace)
              case Some(WireBinding(_)) =>
                id.forceName(None, default="WIRE", _namespace)
              case Some(_) => // don't name literals
                id.forceName(Some(""), default="T", _namespace)
              case _ =>  // don't name literals
            }
          } // else, don't name unbound types
      }
    }

    def genPortIR(port: Data): Seq[Port] = port match {
      case a: Aggregate =>
        a.getElements map { x => genPortIR(x) } reduce { _ ++ _ }
      case b: Bits =>
        val dir = b.direction match {
          case SpecifiedDirection.Output => ir.Output
          case SpecifiedDirection.Input  => ir.Input
          case SpecifiedDirection.InOut  => ir.InOut
          case _ => Builder.error(s"Port Dir Error: ${b.direction}")
        }
        Seq(Port(b, dir))
    }

    val modulePorts = getModulePorts flatMap { p => genPortIR(p) }

    def toConn(info: Map[Bits, Bits]): Map[Expression, Expression] = {
      info map { case (lhs, rhs) => lhs.lref -> rhs.ref }
    }

    def add_when_default(connects: HashMap[Bits, Bits], ele: Bits): Seq[Statement] = {
      if (connects.contains(ele)) {
        Seq(OtherwiseBegin(),
            Connect(ele.lref, connects(ele).ref),
            OtherwiseEnd(),
        )
      } else {
        Seq()
      }
    }

    def wrap_when_init(e: Bits): Seq[Statement] = {
      _regs_info(e) match {
        case RegInfo(ClkInfo(_, Some(rst_val)), Some(init_val)) =>
          Seq(WhenBegin(rst_val, true), Connect(e.lref, init_val), WhenEnd())
        case RegInfo(_, _) =>
          Seq()
      }
    }

    val switch_scope_regs  = ArrayBuffer[Statement]()
    val switch_scope_wires = ArrayBuffer[Statement]()

    def sorted_cases(cases: Seq[(SwitchCondition, ArrayBuffer[Statement])] ): Seq[(SwitchCondition, Seq[Statement])] = {
      val (default, other) = (cases map { case (cond, stmts) => cond -> stmts.toSeq }).sortBy(_._1.idx).partition(_._1.idx == -1)
      other ++ default
    }

    val switch_stmts = switchScope flatMap { case (id, scope) =>
      scope map { case (info, cases) =>
        // Check Defalut
        switch_defalut(id)(info) foreach { x =>
          if (_reg_connects.contains(x)) {
            error(s"${str_of_expr(x.ref)} has two default value, outside switch case default is: ${str_of_expr(_reg_connects(x).ref)} ")
          }
          if (_wire_connects.contains(x)) {
            error(s"${str_of_expr(x.ref)} has two default value, outside switch case default is: ${str_of_expr(_wire_connects(x).ref)} ")
          }

        }

        val switch_body = info match {
          case ClkInfo(Some(_), Some(rst_val)) =>
            // Check Defalut
            val no_default_reg  = switchRegs(id)(info)  filter { x => !switch_defalut(id)(info).contains(x) }
            val default_reg  = _reg_connects  filter { case (x, _) => no_default_reg.contains(x)  }
            if (cases.contains(SwitchCondition(-1, None, None))) {
              cases(SwitchCondition(-1, None, None)) ++= (default_reg map { case (l, r) => Connect(l.lref, r.ref) })
            } else {
              cases(SwitchCondition(-1, None, None)) = ArrayBuffer((default_reg map { case (l, r) => Connect(l.lref, r.ref) }).toSeq: _*)
            }
            val reg_inits = switchRegs(id)(info) map { bit => bit -> _regs_info(bit).init }
            val reg_conns = (reg_inits map { case (bit, init) => init match {
              case Some(init_val) => Some(Connect(bit.lref, init_val))
              case None => None
            }}).flatten
            val init_when = Seq(WhenBegin(rst_val, true)) ++ reg_conns ++ Seq(WhenEnd())
            val else_when = Seq(OtherwiseBegin(), SwitchScope(id.lref, sorted_cases(cases.toSeq)), OtherwiseEnd())
            init_when ++ else_when
          case ClkInfo(Some(_), None) =>
            // Check Defalut
            val no_default_reg  = switchRegs(id)(info)  filter { x => !switch_defalut(id)(info).contains(x) }
            val default_reg  = _reg_connects  filter { case (x, _) => no_default_reg.contains(x)  }
            if (cases.contains(SwitchCondition(-1, None, None))) {
              cases(SwitchCondition(-1, None, None)) ++= (default_reg map { case (l, r) => Connect(l.lref, r.ref) })
            } else if (default_reg.nonEmpty){
              cases(SwitchCondition(-1, None, None)) = ArrayBuffer((default_reg map { case (l, r) => Connect(l.lref, r.ref) }).toSeq: _*)
            }

            Seq(SwitchScope(id.lref, sorted_cases(cases.toSeq)))
          case _ =>
            // Check Defalut
            val no_default_wire = switchWires(id)(info) filter { x => !switch_defalut(id)(info).contains(x) }
            val default_wire = _wire_connects filter { case (x, _) => no_default_wire.contains(x) }
            if (cases.contains(SwitchCondition(-1, None, None))) {
              cases(SwitchCondition(-1, None, None)) ++= (default_wire map { case (l, r) => Connect(l.lref, r.ref) })
            } else if (default_wire.nonEmpty){
              cases(SwitchCondition(-1, None, None)) = ArrayBuffer((default_wire map { case (l, r) => Connect(l.lref, r.ref) }).toSeq: _*)
            }

            Seq(SwitchScope(id.lref, sorted_cases(cases.toSeq)))
        }
        Always(info, switch_body)
      }
    }

    val whenScopeStatements = {
      val scope = whenScope map { case (ele, stmts) =>
        val init_stmt = if (_regs_info.contains(ele)) wrap_when_init(ele) else Seq()
        val wrap_stmts =
          if (init_stmt.nonEmpty) {
            val first = stmts(0) match {
	            case WhenBegin(pred, _) => Seq(WhenBegin(pred, false))
              case other => error(s"First of When statements must be WhenBegin: $other")
            }
            init_stmt ++ first ++ stmts.tail
          } else {
            stmts
          }
        if (_wire_connects.contains(ele) || _reg_connects.contains(ele)) {
          stmts.last match {
            case OtherwiseEnd() => error(s"${str_of_expr(ele.ref)} in $desiredName only one default!")
            case _ =>
          }
        }
        ele -> (wrap_stmts ++ add_when_default(_wire_connects, ele) ++ add_when_default(_reg_connects, ele))
      }
      scope.toSeq.sortWith { case ((a, _), (b, _)) => a._id < b._id } map { case (e, stmts) =>
        val info = if (_regs_info.contains(e)) _regs_info(e).clk_info else ClkInfo(None, None)
        Always(info, stmts.toSeq)
      }
    }

    DefModule(name, modulePorts, getStatements ++ whenScopeStatements ++ switch_stmts)
  }
}
