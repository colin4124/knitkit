package knitkit

import collection.mutable.{HashMap, ArrayBuffer, Set}

import ir._
import internal._

object switch {
  def apply(cond: => Bits)(block: => Unit): Unit = {

    val cur_module = Builder.forcedUserModule

    cur_module.switch_id = Some(cond)
    cur_module.setSwitchId(cond)
    try {
      block
    } catch {
      case ret: scala.runtime.NonLocalReturnControl[_] =>
        throwException("Cannot exit from a switch() block with a \"return\"!" +
                       " Perhaps you meant to use Mux or a Wire as a return value?"
        )
    }
    cur_module.switch_id = None
    cur_module.switch_case_idx = 0
  }
}


object is {
  def apply(cond: => Bits)(block: => Unit): Unit = {
    val cur_module = Builder.forcedUserModule
    val idx = cur_module.switch_case_idx
    cond._ref match {
      case Some(l: Literal) =>
        cur_module.switch_case = Some(SwitchCondition(idx, Some(l), None))
      case _ =>
        cond._binding match {
          case Some(EnumBinding(_, lit)) =>
            cur_module.switch_case = Some(SwitchCondition(idx, Some(lit), Some(cond)))
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
    if (cur_module.whenScopeInSwitch.nonEmpty) {
      cur_module.whenScopeInSwitch.toSeq.sortWith {case ((a, _), (b, _)) => a._id < b._id } foreach { case (ele, stmts) =>
        ele.binding match {
          case RegBinding(_) =>
            val clk_info = cur_module._regs_info(ele).clk_info
            cur_module.pushSwitchScope(clk_info, ir.WhenScope(ele.ref, stmts.toSeq))
          case _ =>
            cur_module.pushSwitchScope(ClkInfo(None, None), ir.WhenScope(ele.ref, stmts.toSeq))
        }
      }
      cur_module.whenScopeInSwitch.clear()
    }
    cur_module.switch_case_idx += 1
    cur_module.switch_case = None
  }
}

object default {
  def apply(block: => Unit): Unit = {
    val cur_module = Builder.forcedUserModule

    cur_module.switch_case = Some(SwitchCondition(-1, None, None))

    try {
      block
    } catch {
      case ret: scala.runtime.NonLocalReturnControl[_] =>
        throwException("Cannot exit from a is() block with a \"return\"!" +
                       " Perhaps you meant to use Mux or a Wire as a return value?"
        )
    }
    cur_module.switch_case = None
  }
}