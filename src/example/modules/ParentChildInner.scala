package example

import knitkit._

class Layer extends ExtModule {
  // val out = IO(Vec(1, Vec(2, Output(UInt(32.W)))))
  val out = IO( Output(UInt(32.W)))
}

class ParentChildInner extends RawModule {
  val clk  = IO(Input(Clock()))
  val rstn = IO(Input(AsyncNegReset()))
  setClockAndReset(clk, rstn)

  val foo = IO( Output(UInt(32.W)))
  // val out = IO(Vec(1, Vec(2, Output(UInt(32.W)))))

  // val inner_a = Wire(Vec(1, Vec(2, UInt(32.W))))
  // val inner_b = Wire(Vec(1, Vec(2, UInt(32.W))))
  val inner_a = Wire(UInt(32.W))
  val inner_b = Wire(UInt(32.W))
  val inner_c = Reg(UInt(32.W))

  val u_layer = Module(new Layer)()

  // u_layer("out") <> inner_a
  u_layer("out") <> inner_a
  foo            <> inner_a
  u_layer("out") <> inner_b
  u_layer("out") <> inner_c
}
