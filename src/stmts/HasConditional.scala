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
  var bitsInWhenScope: HashMap[Bits, Int] = HashMap()

  var whenScopeCond: ArrayBuffer[Statement] = ArrayBuffer() // Layer of each WhenBegin
  var whenPredDepth: HashMap[Int, ArrayBuffer[Expression]] = HashMap() // Layer of each WhenBegin

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

    if (bitsInWhenScope.contains(e)) {
      bitsInWhenScope(e) = Seq(bitsInWhenScope(e), whenDepth).max
    } else {
      bitsInWhenScope(e) = whenDepth
    }

    val scope = if (switch_id.nonEmpty) whenScopeInSwitch else whenScope
    if (!scope.contains(e)) {
      scope(e) = ArrayBuffer()
    }

    stmt match {
	    case _: WhenBegin =>
        appendMissWhenBegin(scope(e))
        if (!scope(e).contains(stmt)) {
          scope(e) += stmt
        }
      case _ =>
        scope(e) += stmt
    }

  }

  def appendMissWhenBegin(a: ArrayBuffer[Statement]): Unit = {
    whenScopeCond foreach { cond =>
      if (!a.contains(cond)) {
        a += cond
      }
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
}
