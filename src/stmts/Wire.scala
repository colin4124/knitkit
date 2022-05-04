package knitkit

import internal._
import internal.Builder._
import ir._

trait WireFactory {
  def bindData[T <: Data](t: T): Unit = {
    t match {
      case e: Bits =>
        e.bind(WireBinding(Builder.forcedUserModule))
        Builder.forcedUserModule.addWire(e)
      case b: Bundle =>
        b.getElements foreach { d => bindData(d) }
        b.bind(WireBinding(Builder.forcedUserModule))
      case v: Vec =>
        v.getElements foreach { d => bindData(d) }
        v.bind(WireBinding(Builder.forcedUserModule))
    }
  }

  def apply[T <: Data](t: T): T = {
    requireIsknitkitType(t, "wire type")
    bindData(t)
    t
  }
}

object Wire extends WireFactory

object WireInit {

  private def applyImpl(t: Bits, init: Bits): Bits = {
    val x = Wire(t)
    requireIsHardware(init, "wire initializer")
    x := init
    x
  }

  /** Construct a [[Wire]] with a type template and a default connection
    * @param t The type template used to construct this [[Wire]]
    * @param init The hardware value that will serve as the default value
    */
  def apply(t: Bits, init: Bits): Bits = {
    applyImpl(t, init)
  }

  /** Construct a [[Wire]] with a default connection
    * @param init The hardware value that will serve as a type template and default value
    */
  def apply(init: Bits): Bits = {
    val model = init.cloneType
    apply(model, init)
  }

  def apply(init: Vec): Vec = {
    val eles = init.elements map { e => e match {
      case b: Bits => apply(b)
      case v: Vec => apply(v)
      case _ => Builder.error("Not support yet")
    }}

    Vec(eles)
  }
  def apply(init: Data): Data = {
    init match {
      case b: Bits => apply(b)
      case v: Vec => apply(v)
      case _ => Builder.error("Not support yet")
    }
  }
}
