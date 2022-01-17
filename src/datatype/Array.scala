package knitkit

import ir._
import Utils._
import knitkit.internal._

// Array
class Arr(
  specifiedType: Type,
  val dimension: Int*
) extends Bits(specifiedType) {

  override def cloneType: this.type = new Arr(tpe, dimension:_*).asInstanceOf[this.type]

  override def apply(idx: Int*): Bits = {
    val ele = clone_fn_base(new Bits(specifiedType), this).asBits
    ele.setRef(NodeArray(this, idx))
    _binding match {
	    case Some(WireBinding(_)) =>
        Wire(ele)
      case Some(RegBinding(_)) =>
        Reg(ele)
      case ohter =>
        Builder.error(s"TODO")
    }
    ele
  }
}

object Arr {
  def apply(b: Bits, dimension: Int*): Arr = {
    new Arr(b.tpe, dimension:_*)
  }
}

object ArrInit {
  def apply(b: Bits, dimension: Int*): Arr = {
    requireIsHardware(b, "reg initializer")

    val arr = new Arr(b.tpe, dimension:_*)
    arr._binding = b._binding
    arr
  }
}
