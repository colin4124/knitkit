package example

import knitkit._

class ElseWhenCase extends RawModule {
  val clk = IO(Input(Clock()))
  val rst = IO(Input(Reset()))
  setClockAndReset(clk, rst)
  val foo_sel1 = IO(Input(Bool()))
  val foo_sel2 = IO(Input(Bool()))
  val bar_sel1 = IO(Input(Bool()))
  val bar_sel2 = IO(Input(Bool()))
  val out1     = IO(Output(UInt(3.W)))
  val out2     = IO(Output(UInt(3.W)))

  val foo = Wire(UInt(3.W))
  val bar = RegInit(0.U(3.W))

  foo := 0.U
  when (foo_sel1) {
    foo := 5.U
  } .elsewhen (foo_sel2) {
    foo := 4.U
  }
  bar := 0.U
  when (bar_sel1) {
    bar := 3.U
  } .elsewhen (bar_sel2) {
    bar := 2.U
  }
  out1 := foo
  out2 := bar
}
