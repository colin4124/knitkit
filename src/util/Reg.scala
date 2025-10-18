package knitkit.util

import knitkit._

object ShiftRegister {

  /** Returns the n-cycle delayed version of the input signal.
    *
    * @param in input to delay
    * @param n number of cycles to delay
    * @param en enable the shift
    *
    * @example {{{
    * val regDelayTwo = ShiftRegister(nextVal, 2, ena)
    * }}}
    */
  def apply(
    in: Bits,
    n:  Int,
    en: Bits = true.B
  ): Bits =
    _apply_impl(in, n, en)

  /** Returns the n-cycle delayed version of the input signal.
    *
    * Enable is assumed to be true.
    *
    * @param in input to delay
    * @param n number of cycles to delay
    *
    * @example {{{
    * val regDelayTwo = ShiftRegister(nextVal, 2)
    * }}}
    */
  def apply(in: Bits, n: Int): Bits =
    _apply_impl(in, n)

  private def _apply_impl(
    in: Bits,
    n:  Int,
    en: Bits = true.B
  ): Bits =
    ShiftRegisters(in, n, en).lastOption.getOrElse(in)

  /** Returns the n-cycle delayed version of the input signal with reset initialization.
    *
    * @param in input to delay
    * @param n number of cycles to delay
    * @param resetData reset value for each register in the shift
    * @param en enable the shift
    *
    * @example {{{
    * val regDelayTwoReset = ShiftRegister(nextVal, 2, 0.U, ena)
    * }}}
    */
  def apply(
    in:        Bits,
    n:         Int,
    resetData: Bits,
    en:        Bits
  ): Bits =
    ShiftRegisters(in, n, resetData, en).lastOption.getOrElse(in)
}

object ShiftRegisters {

  /** Returns a sequence of delayed input signal registers from 1 to n.
    *
    * @param in input to delay
    * @param n  number of cycles to delay
    * @param en enable the shift
    */
  def apply(
    in: Bits,
    n:  Int,
    en: Bits
  ): Seq[Bits] = _apply_impl(in, n, en)

  private def _apply_impl(
    in: Bits,
    n:  Int,
    en: Bits = true.B
  ): Seq[Bits] =
    Seq.iterate(in, n + 1)(RegEnable(_, en)).drop(1)

  /** Returns a sequence of delayed input signal registers from 1 to n.
    *
    * Enable is assumed to be true.
    *
    * @param in input to delay
    * @param n  number of cycles to delay
    */
  def apply(in: Bits, n: Int): Seq[Bits] =
    _apply_impl(in, n)

  /** Returns delayed input signal registers with reset initialization from 1 to n.
    *
    * @param in        input to delay
    * @param n         number of cycles to delay
    * @param resetData reset value for each register in the shift
    * @param en        enable the shift
    */
  def apply(
    in:        Bits,
    n:         Int,
    resetData: Bits,
    en:        Bits
  ): Seq[Bits] =
    Seq.iterate(in, n + 1)(RegEnable(_, resetData, en)).drop(1)
}
