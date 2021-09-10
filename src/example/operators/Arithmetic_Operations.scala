package example

import knitkit._

class Arithmetic_Operations extends RawModule {
  val a = IO(Input(UInt(8.W)))
  val b = IO(Input(UInt(8.W)))
  val sum  = IO(Output(UInt(8.W)))
  val diff0 = IO(Output(UInt(9.W)))
  val diff1 = IO(Output(UInt(9.W)))
  val diff2 = IO(Output(UInt(9.W)))
  val prod  = IO(Output(UInt(16.W)))
  val div   = IO(Output(UInt(8.W)))
  val mod   = IO(Output(UInt(8.W)))

  sum   := a + b
  diff0 := a - b
  prod  := a * b
  div   := a / b
  mod   := a % b
}
