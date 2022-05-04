package example

import knitkit._

class Arithmetic_Operations extends RawModule {
  val a     = IO(Input(UInt(8.W)))
  val b     = IO(Input(UInt(8.W)))
  val sum_0 = IO(Output(UInt(8.W)))
  val sum_1 = IO(Output(UInt(9.W)))
  val diff  = IO(Output(UInt(9.W)))
  val prod  = IO(Output(UInt(16.W)))
  val div   = IO(Output(UInt(8.W)))
  val mod   = IO(Output(UInt(8.W)))

  sum_0 := a + b
  sum_1 := a +& b
  diff  := a - b
  prod  := a * b
  div   := a / b
  mod   := a % b
}
