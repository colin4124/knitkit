package example

import knitkit._

class Shifts extends RawModule {
  val x         = IO(Input(UInt(3.W)))
  val y         = IO(Input(UInt(32.W)))
  val twoToTheX = IO(Output(SInt(33.W)))
  val hiBits    = IO(Output(UInt(16.W)))
  twoToTheX := (1.S << x)
  hiBits    := y >> 16.U
}
