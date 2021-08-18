package knitkit

import internal._
import internal.Builder._
import ir._

object Reg {
  def bindData[T <: Data](t: T): Unit = {
    val cur_module = Builder.forcedUserModule
    t match {
      case e: Bits =>
        val clock = Node(Builder.forcedClock)
        e.bind(RegBinding(cur_module))
        val clk_info = ClkInfo(Some(clock), None)
        cur_module.pushRegInfo(e, clk_info, RegInfo(clk_info, None))
      case a: Aggregate =>
        a.getElements foreach { d => bindData(d) }
        a.bind(RegBinding(Builder.forcedUserModule))
    }
  }

  def apply[T <: Bits](t: T): T = {
    requireIsknitkitType(t, "reg type")
    bindData(t)
    t
  }
}

object RegNext {
  def apply[T <: Bits](next: T): T = {
    requireIsHardware(next, "reg next")
    val model = next.cloneType
    val reg = Reg(model)
    reg := next
    reg
  }

  def apply[T <: Bits](next: T, init: T): T = {
    requireIsHardware(next, "reg next")
    val model = next.cloneType
    val reg = RegInit(model, init)
    reg := next
    reg
  }
}

object RegInit {
  def apply[T <: Bits](reg: T, init: T): T = {
    requireIsknitkitType(reg, "reg type")
    requireIsHardware(init, "reg initializer")

    val clock = Node(Builder.forcedClock)
    val reset = Node(Builder.forcedReset)

    val cur_module = Builder.forcedUserModule

    reg.bind(RegBinding(cur_module))
    val clk_info = ClkInfo(Some(clock), Some(reset))
    cur_module.pushRegInfo(reg, clk_info, RegInfo(clk_info, Some(init.ref)))
    reg
  }

  def apply[T <: Bits](init: T): T = {
    val model = init.cloneType
    RegInit(model, init)
  }
}
