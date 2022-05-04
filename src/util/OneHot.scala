package knitkit

/** Returns the bit position of the sole high bit of the input bitvector.
  *
  * Inverse operation of [[UIntToOH]].
  *
  * @example {{{
  * OHToUInt("b0100".U) // results in 2.U
  * }}}
  *
  * @note assumes exactly one high bit, results undefined otherwise
  */
object OHToUInt {
  def apply(in: Seq[Bits]): Bits = apply(Cat(in.reverse), in.size)
  def apply(in: Bits):      Bits = apply(in, in.getWidth)

  def apply(in: Bits, width: BigInt): Bits = {
    if (width <= 2) {
      Log2(in, width)
    } else {
      val mid = 1 << (log2Ceil(width) - 1)
      val hi = in(width - 1, mid)
      val lo = in(mid - 1, 0)
      Cat(hi.orR, apply(hi | lo, mid)).asBits
    }
  }
}

/** Returns the bit position of the least-significant high bit of the input bitvector.
  *
  * @example {{{
  * PriorityEncoder("b0110".U) // results in 1.U
  * }}}
  *
  * Multiple bits may be high in the input.
  */
object PriorityEncoder {
  def apply(in: Seq[Bits]): Bits = PriorityMux(in, (0 until in.size).map(_.asUInt)).asBits
  def apply(in: Bits):      Bits = apply(in.asBools)
}

/** Returns the one hot encoding of the input UInt.
  *
  * @example {{{
  * UIntToOH(2.U) // results in "b0100".U
  * }}}
  */
object UIntToOH {
  def apply(in: Bits): Bits = 1.U << in
  def apply(in: Bits, width: Int): Bits = width match {
    case 0 => 0.U(0.W)
    case 1 => 1.U(1.W)
    case _ =>
      val shiftAmountWidth = log2Ceil(width)
      val shiftAmount = in.pad(shiftAmountWidth)(shiftAmountWidth - 1, 0)
      (1.U << shiftAmount)(width - 1, 0)
  }
}

/** Returns a bit vector in which only the least-significant 1 bit in the input vector, if any,
  * is set.
  *
  * @example {{{
  * PriorityEncoderOH(Seq(false.B, true.B, true.B, false.B)) // results in Seq(false.B, true.B, false.B, false.B)
  * }}}
  */
object PriorityEncoderOH {
  private def encode(in: Seq[Bits]): Bits = {
    val outs = Seq.tabulate(in.size)(i => (BigInt(1) << i).asUInt(in.size.W))
    PriorityMux(in :+ true.B, outs :+ 0.U(in.size.W)).asBits
  }
  def apply(in: Seq[Bits]): Seq[Bits] = {
    val enc = encode(in)
    Seq.tabulate(in.size)(enc(_))
  }
  def apply(in: Bits): Bits = encode((0 until in.getWidth.toInt).map(i => in(i)))
}
