package example

import knitkit._

class Bitwise_Reductions extends RawModule {
  val x      = IO(Input(UInt(32.W)))
  val allSet = IO(Output(Bool()))
  val anySet = IO(Output(Bool()))
  val parity = IO(Output(Bool()))

  allSet := x.andR
  anySet := x.orR
  parity := x.xorR
}
