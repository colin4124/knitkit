package example

import knitkit._

class GreaterThan extends RawModule {
  val in0 = IO(Input(SInt(3.W)))
  val in1 = IO(Input(SInt(3.W)))
  val out = IO(Output(Bool()))
  out := in0 > in1
}
