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
        val clk_info = ClkInfo(Some(clock), None)
        cur_module.pushRegInfo(e, clk_info, RegInfo(clk_info, None))
        e.bind(RegBinding(cur_module))
      case a: Aggregate =>
        a.getElements foreach { d => bindData(d) }
        a.bind(RegBinding(Builder.forcedUserModule))
      case v: Vec =>
        v.getElements foreach { d => bindData(d) }
        v.bind(RegBinding(Builder.forcedUserModule))
    }
  }

  def apply[T <: Data](t: T): T = {
    requireIsknitkitType(t, "reg type")
    bindData(t)
    t
  }
}

object RegNext {
  def apply(next: Bits): Bits = {
    requireIsHardware(next, "reg next")
    val model = next.cloneType
    val reg = Reg(model)
    reg := next
    reg
  }

  def apply(next: Bits, init: Bits): Bits = {
    requireIsHardware(next, "reg next")
    val model = next.cloneType
    val reg = RegInit(model, init)
    reg := next
    reg
  }
}

object RegInit {
  def apply[T <: Bits](reg: T, init: Bits): T = {
    requireIsknitkitType(reg, "reg type")
    requireIsHardware(init, "reg initializer")

    val clock = Node(Builder.forcedClock)
    val reset = Node(Builder.forcedReset)

    val cur_module = Builder.forcedUserModule

    val clk_info = ClkInfo(Some(clock), Some(reset))
    cur_module.pushRegInfo(reg, clk_info, RegInfo(clk_info, Some(init.ref)))
    reg.bind(RegBinding(cur_module))
    reg
  }

  def apply(init: Bits): Bits = {
    val model = init.cloneType
    RegInit(model, init)
  }

  def apply(init: Arr): Arr = {
    val init_ele = init.element
    val model = init.cloneType
    RegInit(model, init_ele)
  }

  def apply(init: Vec): Vec = {
    val eles = init.elements map { e => e match {
      case b: Bits => apply(b)
      case v: Vec => apply(v)
      case _ => Builder.error("Not support yet")
    }}

    Vec(eles)
  }
}

object RegEnable {

  /** Returns a register with the specified next, update enable gate, and no reset initialization.
    *
    * @example {{{
    * val regWithEnable = RegEnable(nextVal, ena)
    * }}}
    */
  def apply[T <: Data](next: T, enable: Bits): T = {
    val res = next match {
	    case b: Bits =>
        val r = Reg(b.clone())
        when(enable) { r := b }
        r
      case v: Vec =>
        val eles = v.elements map { e =>
          apply(e, enable)
        }
        Vec(eles)
      case other =>
        Builder.error(s"TODO: $other")
    }

    res.asInstanceOf[T]
  }

  /** Returns a register with the specified next, update enable gate, and reset initialization.
    *
    * @example {{{
    * val regWithEnableAndReset = RegEnable(nextVal, 0.U, ena)
    * }}}
    */
  def apply(next: Bits, init: Bits, enable: Bits): Bits = {
    val r = RegInit(init)
    when(enable) { r := next }
    r
  }
}
