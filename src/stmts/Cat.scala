package knitkit

import ir._
import internal.Builder.{pushOp, error}

object Cat {
  def apply[T <: Bits](a: T, r: T*): Bits = apply(a :: r.toList)

  def apply[T <: Bits](r: Seq[T]): Bits = r match {
    case b: Seq[Bits] =>
      val w = b.foldLeft(IntWidth(0)) {  (res, d) => res + d.width }
      val args = b map { _.ref }
      pushOp(UInt(w), PrimOps.CatOp, args: _*)
    case _ =>
      error("Not support")
  }
}
