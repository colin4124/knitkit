package example

import knitkit._

class AddBeta extends RawModule {
  val in0 = IO(Input(UInt(32.W)))
  val in1 = IO(Input(UInt(32.W)))
  val out = IO(Output(UInt(32.W)))
  val out1 = IO(Output(UInt(32.W)))
  out := in0 + in1
  out1 := in0 + in1
}

class ParentChildBeta extends RawModule {
  val in0 = IO(Input(UInt(4.W)))
  val in1 = IO(Input(UInt(4.W)))
  val out = IO(Output(UInt(32.W)))

  val u_add = Module(new AddBeta)()

  val foo = Wire(UInt(32.W))

  val add10 = Cat(in0, 10.U)
  u_add("in0") := add10
  u_add("in1") := Cat(Seq(5.U, 1.U))

  foo := u_add("out1")
  out := u_add("out") + 9.U
}
