package example

import knitkit._

class RegInitInferredLitCase extends RawModule {
  val clk   = IO(Input(Clock()))
  val rst   = IO(Input(Reset()))
  val x_in  = IO(Input(UInt(3.W)))
  val y_in  = IO(Input(UInt(8.W)))
  val x_out = IO(Output(UInt(3.W)))
  val y_out = IO(Output(UInt(8.W)))
  setClockAndReset(clk, rst)

  // width will be inferred to be 3
  val x = RegInit(5.U)
  // width is set to 8
  val y = RegInit(5.U(8.W))

  x := x_in
  y := y_in
  x_out := x
  y_out := y
}
