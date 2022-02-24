package example

import knitkit._

class ArrayDontCareCase extends RawModule {
  val x_in  = IO(Input(Arr(UInt(7.W), 4, 8)))
  val x_out = IO(Output(Arr(UInt(7.W), 4, 8)))

  val y_in  = IO(Input(Arr(UInt(6.W), 2, 4)))
  val y_out = IO(Output(Arr(UInt(6.W), 4, 4)))

  x_out := DontCare
  y_out := DontCare
}
