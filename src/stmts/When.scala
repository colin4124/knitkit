package knitkit

import collection.mutable.{HashMap, ArrayBuffer, Set}

import ir._
import internal._
import Utils.wrapClkInfo

trait HasConditional { this: BaseModule =>

  var whenDepth: Int = 0 // Depth of when nesting
  var whenScopeBegin: Boolean = false // Depth of when nesting
  var currentWhenStmt: ArrayBuffer[Statement] = ArrayBuffer()
  var currentInWhenScope: ArrayBuffer[Bits] = ArrayBuffer()

  var switch_id: Option[Bits] = None
  var switch_case: Option[SwitchCondition] = None
  var switch_case_idx: Int = 0
  val switchScope = HashMap[Bits, HashMap[ClkInfoIdx, HashMap[SwitchCondition, ArrayBuffer[Statement]]]]()
  val switchRegs  = HashMap[Bits, HashMap[ClkInfo, Set[Bits]]]()
  val switchWires = HashMap[Bits, HashMap[ClkInfo, Set[Bits]]]()
  var switch_defalut = HashMap[Bits, HashMap[ClkInfo, ArrayBuffer[Bits]]]()

  val whenScope = HashMap[Bits, ArrayBuffer[Statement]]()
  val whenScopeInSwitch = HashMap[Bits, ArrayBuffer[Statement]]()
  def pushWhenScope(e: Bits, stmt: Statement): Unit = {
    val scope = if (switch_id.nonEmpty) whenScopeInSwitch else whenScope
    if (scope contains e) {
      scope(e) += stmt
    } else {
      scope(e) = ArrayBuffer(stmt)
    }
  }

  def setSwitchId(id: Bits): Unit = {
    if (switchScope contains id) {
      Builder.error(s"Can't switch $id twice!")
    } else {
      switchScope(id) = HashMap[ClkInfoIdx, HashMap[SwitchCondition, ArrayBuffer[Statement]]]()
      switchRegs (id) = HashMap[ClkInfo, Set[Bits]]()
      switchWires(id) = HashMap[ClkInfo, Set[Bits]]()
      switch_defalut(id) = HashMap[ClkInfo, ArrayBuffer[Bits]]()
    }
  }
  def pushSwitchScope(info: ClkInfo, stmt: Statement): Unit = {
    val clk_info = wrapClkInfo(info)
    val id = switch_id.get
    val cond = switch_case.get
    if (!switchScope(id).contains(clk_info)) {
      switchScope(id)(clk_info) = HashMap()
    }
    if (switchScope(id)(clk_info) contains cond) {
      switchScope(id)(clk_info)(cond) += stmt
    } else {
      switchScope(id)(clk_info)(cond) = ArrayBuffer(stmt)
    }
  }

  def when(cond: => Bits)(block: => Unit): WhenContext = {
    new WhenContext(Some(() => cond), block, true)
  }

  final class WhenContext(cond: Option[() => Bits], block: => Unit, isFirstWhen: Boolean = false) {

    def elsewhen (elseCond: => Bits)(block: => Unit): WhenContext = {
      new WhenContext(Some(() => elseCond), block)
    }

    def otherwise(block: => Unit): Unit =
      new WhenContext(None, block)

    if (whenDepth == 0) whenScopeBegin = true

    val parentWhenStmt = currentWhenStmt
    val parentInWhenScope = currentInWhenScope
    currentWhenStmt.clear()
    currentInWhenScope.clear()

    cond match {
      case Some(c) =>
        currentWhenStmt += WhenBegin(c().ref, isFirstWhen)
      case None =>
        currentWhenStmt += OtherwiseBegin()
    }

    whenDepth += 1

    try {
      block
    } catch {
      case ret: scala.runtime.NonLocalReturnControl[_] =>
        throwException("Cannot exit from a when() block with a \"return\"!" +
                         " Perhaps you meant to use Mux or a Wire as a return value?"
        )
    }

    whenDepth -= 1
    if (whenDepth == 0) whenScopeBegin = false

    cond match {
      case Some(c) =>
        currentInWhenScope foreach { bits =>
          pushWhenScope(bits, WhenEnd())
        }
      case None =>
        currentInWhenScope foreach { bits =>
        pushWhenScope(bits, OtherwiseEnd())
      }
    }

    currentWhenStmt    = parentWhenStmt
    currentInWhenScope = parentInWhenScope
  }

  def switch(cond: => Bits)(block: => Unit): Unit = {
    switch_id = Some(cond)
    setSwitchId(cond)
    try {
      block
    } catch {
      case ret: scala.runtime.NonLocalReturnControl[_] =>
        throwException("Cannot exit from a switch() block with a \"return\"!" +
                       " Perhaps you meant to use Mux or a Wire as a return value?"
        )
    }
    switch_id = None
    switch_case_idx = 0
  }


  object is {
    def apply(cond: => Bits)(block: => Unit): Unit = {
      val cur_module = Builder.forcedUserModule
      val idx = switch_case_idx
      cond._ref match {
        case Some(l: Literal) =>
          switch_case = Some(SwitchCondition(idx, Some(l), None))
        case _ =>
          cond._binding match {
            case Some(EnumBinding(_, lit)) =>
              switch_case = Some(SwitchCondition(idx, Some(lit), Some(cond)))
            case other => Builder.error(s"$other Must be Enum")
          }
      }
      try {
        block
      } catch {
        case ret: scala.runtime.NonLocalReturnControl[_] =>
          throwException("Cannot exit from a is() block with a \"return\"!" +
                           " Perhaps you meant to use Mux or a Wire as a return value?"
          )
      }
      if (whenScopeInSwitch.nonEmpty) {
        whenScopeInSwitch.toSeq.sortWith {case ((a, _), (b, _)) => a._id < b._id } foreach { case (ele, stmts) =>
          ele.binding match {
            case RegBinding(_) =>
              val clk_info = cur_module._regs_info(ele).clk_info
              pushSwitchScope(clk_info, ir.WhenScope(ele.ref, stmts.toSeq))
            case _ =>
              pushSwitchScope(ClkInfo(None, None), ir.WhenScope(ele.ref, stmts.toSeq))
          }
        }
        whenScopeInSwitch.clear()
      }
      switch_case_idx += 1
      switch_case = None
    }
  }

  def default(block: => Unit): Unit = {
    val cur_module = Builder.forcedUserModule
    switch_case = Some(SwitchCondition(-1, None, None))

    try {
      block
    } catch {
      case ret: scala.runtime.NonLocalReturnControl[_] =>
        throwException("Cannot exit from a is() block with a \"return\"!" +
                       " Perhaps you meant to use Mux or a Wire as a return value?"
        )
    }
    switch_case = None
  }
}
