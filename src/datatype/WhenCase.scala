package knitkit

import internal._
import internal.Builder.error
import ir._

class WhenCase(
  val default: Option[Data],
  val mapping: Seq[(Data, Data)],
) extends Bits(UIntType(Width())) {

  bind(WireBinding(Builder.forcedUserModule))

  def genWhenCase(dest: Data): Unit = {
    if (mapping.isEmpty) {
      default match {
        case Some(d) =>
          dest := d
        case None =>
          error(s"default can't be empty!")
      }
    } else {
      val init = mapping(0)
      val tail = mapping.tail

      val init_when = when (init._1.asBits) { dest := init._2 }
      tail foreach { case (cond, res) => init_when.elsewhen(cond.asBits) { dest := res } }

      default match {
        case Some(d) =>
          init_when otherwise { dest := d }
        case None =>
      }
    }
  }

}

object WhenCase {
  def apply(mapping: Seq[(Data, Data)]): WhenCase = {
    new WhenCase(None, mapping)
  }

  def apply(default: Data, mapping: Seq[(Data, Data)]): WhenCase = {
    new WhenCase(Some(default), mapping)
  }

  def apply(default: Option[Data], mapping: Seq[(Data, Data)]): WhenCase = {
    new WhenCase(default, mapping)
  }

  // def apply(dest: Data, mapping: Seq[(Data, Data)]): Unit = {
  //   apply(dest, None, mapping)
  // }

  def apply(dest: Data, default: Data, mapping: Seq[(Data, Data)]): Unit = {
    apply(dest, Some(default), mapping)
  }

  def apply(dest: Data, default: Option[Data], mapping: Seq[(Data, Data)]): Unit = {
    val init = mapping(0)
    val tail = mapping.tail

    val init_when = when (init._1.asBits) { dest := init._2 }
    tail foreach { case (cond, res) => init_when.elsewhen(cond.asBits) { dest := res } }

    default match {
      case Some(d) =>
        init_when otherwise { dest := d }
      case None =>
    }
  }
}
