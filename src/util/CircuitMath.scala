package knitkit

/** Returns the base-2 integer logarithm of an UInt.
  *
  * @note The result is truncated, so e.g. Log2(13.U) === 3.U
  *
  * @example {{{
  * Log2(8.U)  // evaluates to 3.U
  * Log2(13.U)  // evaluates to 3.U (truncation)
  * Log2(myUIntWire)
  * }}}
  */
object Log2 {

  /** Returns the base-2 integer logarithm of the least-significant `width` bits of an UInt.
    */
  def apply(x: Bits, width: BigInt): Bits = {
    if (width < 2) {
      0.U
    } else if (width == 2) {
      x(1)
    } else if (width <= divideAndConquerThreshold) {
      Mux(x(width - 1), (width - 1).asUInt, apply(x, width - 1))
    } else {
      val mid = 1 << (log2Ceil(width) - 1)
      val hi = x(width - 1, mid)
      val lo = x(mid - 1, 0)
      val useHi = hi.orR
      Cat(useHi, Mux(useHi, Log2(hi, width - mid), Log2(lo, mid))).asBits
    }
  }

  def apply(x: Bits): Bits = apply(x, x.getWidth)

  private def divideAndConquerThreshold = 4
}
