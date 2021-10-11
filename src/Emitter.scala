package knitkit

import scala.collection.mutable.{ArrayBuffer, ListMap, HashMap}

import internal.Builder.error
import ir._
import ir.PrimOps._
import Utils._
import knitkit.internal.Builder
import scala.annotation.meta.param

object Emitter {
  def emit(circuit: Circuit): Map[String, String] = {
    (circuit.modules.flatMap {
      case m: DefModule =>
        val renderer = new VerilogRender()
        val result = renderer.emit_verilog(m)
        Some(m.name -> result)
      case m: DefBlackBox => None
      case m => error(s"Unknown modules: $m")
    }).toMap
  }
}

class VerilogRender() {
  import VerilogRender._

  val result = ArrayBuffer[String]()

  def build_ports(ports: Seq[Port], tab: Int): Seq[String]= {
    val portdefs = ArrayBuffer[String]()
    // Turn directions into strings (and AnalogType into inout)
    val dirs = ports map { case Port(_, dir) =>
      dir match {
        case ir.Input  => "input "
        case ir.Output => "output"
        case ir.InOut  => "inout"
      }
    }
    // Turn types into strings, all ports must be GroundTypes
    val tpes = ports map { p  => str_of_type(p.tpe) }

    // dirs are already padded
    dirs.lazyZip(padToMax(tpes)).lazyZip(ports).toSeq.zipWithIndex.foreach {
      case ((dir, tpe, Port(id, _)), i) =>
        val name = str_of_expr(id.getRef)
        if (i != ports.size - 1) {
          portdefs += indent(s"$dir $tpe$name,", tab)
        } else {
          portdefs += indent(s"$dir $tpe$name" , tab)
        }
    }
    portdefs.toSeq
  }

  def str_of_if_block(cond: Seq[(String, String)], default: Option[String], tab: Int): Seq[String] = {
    val result = ArrayBuffer[String]()
    if (cond.size > 0) {
      result += indent(s"if (${cond(0)._1}) begin", tab)
      result += indent(s"${cond(0)._2}", tab+1)
      result ++ (cond.drop(1) flatMap { case (c, v) =>
        Seq(
          indent(s"end else if ($c) begin", tab),
          indent(s"$v", tab*2),
        )
      })
      default match {
        case Some(v) =>
          result ++= Seq(
            indent(s"end else begin", tab),
            indent(s"${v}", tab+1),
          )
        case None =>
      }
      result += indent("end", tab)
    } else {
      require(default.nonEmpty)
      result += indent(default.get, tab)
    }
    result.toSeq
  }

  def wrap_always_block(clk_info: ClkInfo, block: Seq[String], tab: Int): Seq[String ] = {
    val head = clk_info match {
      case ClkInfo(Some(clk), Some(rst)) =>
        type_of_expr(rst) match {
          case AsyncPosResetType =>
            s"always @(posedge ${str_of_expr(clk)} or posedge ${str_of_expr(rst)}) begin"
          case AsyncNegResetType =>
            s"always @(posedge ${str_of_expr(clk)} or negedge ${str_of_expr(rst)}) begin"
          case SyncResetType =>
            s"always @(posedge ${str_of_expr(clk)}) begin"
          case t =>
            error(s"Not support reset type: $rst ${t} ${str_of_expr(rst)}")
        }
      case ClkInfo(Some(clk), None) =>
        s"always @(posedge ${str_of_expr(clk)}) begin"
      case _ =>
        s"always @* begin"
    }
    Seq(indent(head, tab)) ++ block ++ Seq(indent("end", tab))
  }

  //def gen_always_block(reg: Expression, info: RegInfo, default: Option[String], tab: Int): Seq[String] = {
  //  val init_cond = (info.reset, info.init) match {
  //    case (Some(rst_val), Some(init_val)) =>
  //      val rst_cond = type_of_expr(rst_val) match {
  //        case AsyncNegResetType =>
  //          s"!${str_of_expr(rst_val)}"
  //        case _ =>
  //          s"${str_of_expr(rst_val)}"
  //      }
  //      val rst_conn = s"${str_of_expr(reg)} <= ${str_of_expr(init_val)};"
  //      Seq((rst_cond, rst_conn))
  //    case (Some(rst_val), _) =>
  //      Builder.error(s"Reset Without init value!")
  //    case (None, Some(init_val)) =>
  //      Builder.error(s"Init Without Reset value!")
  //    case (_, None) =>
  //      if (default.nonEmpty) {
  //        Seq()
  //      } else {
  //        Builder.error(s"Default value not exist!")
  //      }
  //  }
  //  str_of_if_block(init_cond, default, tab*2)
  //}

  def check_bits_dir(p: Bits) = p.direction match {
    case SpecifiedDirection.Output =>
      require(p._conn.nonEmpty | p.used, s"${str_of_expr(p.getRef)} Output shoud be connected")
    case SpecifiedDirection.Input =>
      require(p._conn.nonEmpty, s"${str_of_expr(p.getRef)} Input shoud be connected")
    case SpecifiedDirection.InOut =>
      require(p._conn.nonEmpty, s"${str_of_expr(p.getRef)} InOut shoud be connected")
    case _ =>
  }

  def check_port_conn(d: Data): Unit = d match {
    case a: Aggregate =>
      a.getElements foreach check_port_conn
    case b: Bits =>
      check_bits_dir(b)
  }

  def getInstConn(d: Data): Seq[(String, String)] = {
    d match {
      case a: Aggregate =>
        a.getElements map { x => getInstConn(x) } reduce { _ ++ _ }
      case b: Bits =>
        val port_name = b.computeName(None, "ERR")
        val port_ref  = b._conn match {
          case Some(e) => str_of_expr(e)
          case None    => str_of_expr(b.getRef)
        }
        Seq((port_name, port_ref))
    }
  }

  def str_of_param(param: Param): String = {
    param match {
      case IntParam(value) =>
        if (value.isValidInt) {
          s"$value"
        } else {
          val blen = value.bitLength
          if (value > 0) s"$blen'd$value" else s"-${blen+1}'sd${value.abs}"
        }
      case DoubleParam(value) => s"$value"
      case StringParam(value) => s"${value.verilogEscape}"
      case RawParam(value)    => s"$value"
    }
  }
  def str_of_stmt(s: Statement, tab: Int): Seq[String] = s match {
    case DefWire(e, reg) =>
      val tpe = str_of_type(type_of_expr(e))
      val decl = if (reg) "reg " else "wire"
      Seq(indent(s"$decl ${tpe}${str_of_expr(e)};", tab))
    case DefInstance(inst, module, params) =>
      val instdeclares = ArrayBuffer[String]()
      val inst_name = str_of_expr(inst.getRef)
      if (params.nonEmpty) {
        val ps_names  = params map { case (name,  _) => name  }
        val ps_params = params map { case (_, param) => str_of_param(param) }

        val ps = (padToMax(ps_names.toSeq) zip padToMax(ps_params.toSeq)) map { case (name, param) =>
          s"    .$name ($param)"
        } mkString(",\n")
        instdeclares += indent(s"$module #(\n$ps\n  ) ${inst_name} (", tab)
      } else {
        instdeclares += indent(s"$module ${inst_name} (", tab)
      }
      inst.ports foreach { case (_, p) => check_port_conn(p) }
      val portCons = inst.ports flatMap { case (_, port) => getInstConn(port) }
      val (p_list, r_list) = portCons.unzip
      val padPortCons = padToMax(p_list.toSeq) zip padToMax(r_list.toSeq)
        for (((port, ref), i) <- padPortCons.zipWithIndex) {
          val line = indent(s".${port} ( $ref )", tab + 1)
          if (i != portCons.size - 1) instdeclares += s"${line},"
          else instdeclares += line
        }
        instdeclares += indent(");", tab)
      instdeclares.toSeq
    case Assign(loc, e) =>
      Seq(indent(s"assign ${str_of_expr(loc)} = ${str_of_expr(e)};", tab))
    case Always(info, stmts) =>
      wrap_always_block(info, stmts flatMap { s => str_of_stmt(s, tab + 1) }, tab)
    case SwitchScope(expr, conds) =>
      val result = ArrayBuffer[String]()
      result += s"case (${str_of_expr(expr)})"
      conds foreach { case (cond, stmts) =>
        cond match {
          case SwitchCondition(_, Some(lit), Some(id)) =>
            result += indent(s"${str_of_expr(lit)}: begin //${str_of_expr(id.ref)}", 1)
          case SwitchCondition(_, Some(lit), None) =>
            result += indent(s"${str_of_expr(lit)}: begin", 1)
          case _ =>
            result += indent(s"default: begin", 1)
        }
        stmts foreach { stmt =>
          stmt match {
            case Connect(l, r) =>
              result += indent(s"${str_of_expr(l)} => ${str_of_expr(r)};", 2)
            case WhenScope(_, stmts) =>
              result ++= stmts flatMap { s => str_of_stmt(s, 2) }
            case _ =>
              error(s"Not support\n $stmt")
          }
        }
        result += indent(s"end", 1)
      }
      result += s"endcase //${str_of_expr(expr)}"
      result.toSeq map { x => indent(x, tab) }
    case WhenBegin(pred, isFirstWhen) =>
      val cond = type_of_expr(pred) match {
	      case AsyncNegResetType => s"!${str_of_expr(pred)}"
        case _ => s"${str_of_expr(pred)}"
      }
      if (isFirstWhen) {
        Seq(indent(s"if (${cond}) begin", tab))
      } else {
        Seq(indent(s"else if (${cond}) begin", tab))
      }
    case WhenEnd() =>
      Seq(indent(s"end", tab))
    case OtherwiseBegin() =>
      Seq(indent(s"else begin", tab))
    case OtherwiseEnd() =>
      Seq(indent(s"end", tab))
    case Connect(loc, expr) =>
      Seq(indent(s"${str_of_expr(loc)} <= ${str_of_expr(expr)};", tab + 1))
  }
  def build_streams(stmts: Seq[Statement]): Seq[String]= {
    stmts flatMap { s => str_of_stmt(s, 1) }
  }

  def emit_verilog(m: DefModule): String = {
    val result = ArrayBuffer[String]()
    result += s"module ${m.name} ("
    result ++= build_ports(m.ports, 1)
    result += ");"
    result ++= build_streams(m.stmts)
    result += s"endmodule\n"
    result.mkString("\n")
  }
}

object VerilogRender {
  val cfg_tab = "  "
  def indent(s: String, i: Int) = cfg_tab * i + s

    def str_of_type(tpe: Type): String = {
    val wx = tpe.width.value - 1
    if (wx > 0) s"[$wx:0] " else ""
  }

  def type_of_expr(e: Expression): Type = e match {
    case Reference(_, t) => t
    case DoPrim(_, _, _, t) => t
    case Node(id) => type_of_expr(id.getRef)
    case _ => UnknownType
  }

  def str_of_expr(e: Expression): String = e match {
    case Reference(s, _) => s
    case SubField(e, name) =>
      val parent_name = str_of_expr(e)
      if (parent_name == "") name else s"${parent_name}_${name}"
    case InstanceIO(inst, name) =>
      if (name == "") {
        s"${str_of_expr(inst.getRef)}"
      } else {
        s"${str_of_expr(inst.getRef)}_${name}"
      }
    case PairInstIO(l, r) => s"${str_of_expr(l)}_to_${str_of_expr(r)}"
    case Node(id) =>
      str_of_expr(id.getRef)
    case Mux(cond, tval, fval) =>
        def cast(e: Expression): String = e match {
          case m: Mux => "(" + str_of_expr(m) + ")"
          case _      => str_of_expr(e)
        }
      str_of_expr(cond) + " ? " + cast(tval) + " : " + cast(fval)
    case d: DoPrim => str_of_op(d)
    case ILit(n) => n.toString()
    case u: UIntLiteral =>
      s"${u.getWidth}'h${u.value.toString(16)}"
    case s: SIntLiteral =>
      val stringLiteral = s.value.toString(16)
      stringLiteral.head match {
        case '-' => s"-${s.getWidth}'sh${stringLiteral.tail}"
        case _   => s"${s.getWidth}'sh${stringLiteral}"
      }
  }

  def str_of_op(doprim: DoPrim): String = {
    def is_same_op(a: Expression) = a match {
      case d: DoPrim if (d.op != doprim.op) => false
      case _ => true
    }

    def cast_as(e: Expression): String = str_of_expr(e)

    def checkArgumentLegality(e: Expression): Expression = e match {
      case (Node(id))    => checkArgumentLegality(id.getRef)
      case r: Reference  => r
      case s: SubField   => s
      case d: DoPrim     => d
      case d: InstanceIO => d
      case _: UIntLiteral | _: SIntLiteral | _: CatArgs => e
      case _ => error(s"Can't emit ${e.getClass.getName} as PrimOp argument")
    }

    val args = doprim.args map checkArgumentLegality

    def a0: Expression = args.head
    def a1: Expression = args(1)
    def a0_seq = if (is_same_op(a0)) cast_as(a0) else s"(${cast_as(a0)})"
    def a1_seq = if (is_same_op(a1)) cast_as(a1) else s"(${cast_as(a1)})"

    def c0: Int = doprim.consts.head.toInt
    def c1: Int = doprim.consts(1).toInt

    doprim.op match {
      case Add => a0_seq + " + "  + a1_seq
      case Sub => a0_seq + " - "  + a1_seq
      case Mul => a0_seq + " * "  + a1_seq
      case Div => a0_seq + " / "  + a1_seq
      case Rem => a0_seq + " % "  + a1_seq
      case Lt  => a0_seq + " < "  + a1_seq
      case Leq => a0_seq + " <= " + a1_seq
      case Gt  => a0_seq + " > "  + a1_seq
      case Geq => a0_seq + " >= " + a1_seq
      case Eq  => a0_seq + " == " + a1_seq
      case Neq => a0_seq + " != " + a1_seq
      case Cast => cast_as(a0)
      case Dshl => a0_seq + " << " + a1_seq
      case Dshr => doprim.tpe match {
        case (_: SIntType) => a0_seq + " >>> " + a1_seq
        case (_) => a0_seq + " >> " + a1_seq
      }
      case Shl => a0_seq + " << " + s"$c0"
      case Shr if c0 >= bitWidth(type_of_expr(a0)) =>
        error("Verilog emitter does not support SHIFT_RIGHT >= arg width")
      case Shr => doprim.tpe match {
        case (_: SIntType) => a0_seq + " >>> " + c0
        case (_) => a0_seq + " >> " + c0
      }
      case Not => "~ " + a0_seq
      case And => a0_seq + " & " + a1_seq
      case Or  => a0_seq + " | " + a1_seq
      case Xor => a0_seq + " ^ " + a1_seq
      case Andr => "&" + a0_seq
      case Orr  => "|" + a0_seq
      case Xorr => "^" + a0_seq
      case CatOp => "{" + args.map(str_of_expr(_)).mkString(", ") + "}"
      case Bits if c0 == 0 && c1 == 0 && bitWidth(type_of_expr(a0)) == BigInt(1) => a0_seq
      case Bits if c0 == c1 => a0_seq + "[" + c0 + "]"
      case Bits => a0_seq + "[" + c0 + ":" + c1 + "]"
      case Tail =>
        val w = bitWidth(type_of_expr(a0))
        val low = w - c0 - 1
        a0_seq + "[" + low + ":" + 0 + "]"
    }
  }

  def unsigned2signed(uint: BigInt, width: BigInt): String = {
    require(width >= uint.bitLength)
    val sign_value = 1.toLong << (width.toLong - 1)
    val sign = if ((uint & sign_value) == 0) 0 else -sign_value
    val value = uint & (sign_value - 1)
    (sign + value).toString(16)
  }
}

object bitWidth {
  def apply(dt: Type): BigInt = widthOf(dt)
  private def widthOf(dt: Type): BigInt = dt.width match {
    case IntWidth(width) => width
    case t => Builder.error(s"Unknown type encountered in bitWidth: $dt")
  }
}
