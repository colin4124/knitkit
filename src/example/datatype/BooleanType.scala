package example

import knitkit._

class BooleanType extends RawModule {
  val out0 = IO(Output(Bool()))
  val out1 = IO(Output(Bool()))
  val out2 = IO(Output(Bool()))
  val out3 = IO(Output(Bool()))
  /* 布尔类型 */
  out0 := true.B
  out1 := false.B
  out2 := 1.B
  out3 := 0.B
}
