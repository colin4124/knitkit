package example

import knitkit._

class ByPassPiece extends RawModule {
  val in  = IO(Input(Bool()))
  val out = IO(Output(Bool()))

  out := in
}

class ParentChildCase extends RawModule {
  val clk = IO(Input(Clock()))
  val rstn = IO(Input(AsyncNegReset()))
  setClockAndReset(clk, rstn)

  val in  = IO(Input(Bool()))
  val out = IO(Output(Bool()))
  // val out2 = IO(Output(Bool()))

  val bypass = Module(new ByPassPiece)
  val u_1 = bypass()
  val u_2 = bypass()

  val foo_reg = RegInit(0.B)
  // val foo_wire = Wire(Bool())

  foo_reg  <> u_1("out")

  u_1("in") := in
  // u_2("in") := in
  u_2("in") := u_1("out")

  // foo_wire <> u_2("out")

  out := foo_reg
  // out2 := foo_wire
}


