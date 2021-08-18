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
      case b: Aggregate =>
        b.getElements foreach { d => bindData(d) }
        b.bind(WireBinding(Builder.forcedUserModule))
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

  private def applyImpl[T <: Bits](t: T, init: Bits): T = {
    val x = Wire(t)
    requireIsHardware(init, "wire initializer")
    x := init
    x
  }

  /** Construct a [[Wire]] with a type template and a default connection
    * @param t The type template used to construct this [[Wire]]
    * @param init The hardware value that will serve as the default value
    */
  def apply[T <: Bits](t: T, init: T): T = {
    applyImpl(t, init)
  }

  /** Construct a [[Wire]] with a default connection
    * @param init The hardware value that will serve as a type template and default value
    */
  def apply[T <: Bits](init: T): T = {
    val model = init.cloneType
    apply(model, init)
  }
}
