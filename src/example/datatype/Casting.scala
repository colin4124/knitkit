package example

import knitkit._

class Casting extends RawModule {
  val out0 = IO(Output(SInt(3.W)))
  val out1 = IO(Output(UInt(3.W)))
  val out2 = IO(Output(UInt(3.W)))

  out0 := 7.U.asSInt.asUInt.asSInt.asSInt
  out1 := -1.S(3.W).asUInt
  out2 := -1.S.asUInt
}
