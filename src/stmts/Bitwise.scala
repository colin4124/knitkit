package knitkit

import Utils._
import internal.Builder

object Fill {
  def apply(n: Int, x_raw: Data): Bits = {
    val x = x_raw.asBits
    require(isUInt(x))
    n match {
      case _ if n < 0 => Builder.error(s"n (=$n) must be nonnegative integer.")
      case 0 => UInt(0.W)
      case 1 => x
      case _ if x.width.value == 1 =>
        Mux(x.asBool, ((BigInt(1) << n) - 1).asUInt(n.W), 0.U(n.W))
      case _ =>
        Cat(Seq.fill(n)(x))
    }
  }
}
