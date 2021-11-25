package example

import knitkit._

class Add extends RawModule {
  val in0 = IO(Input(UInt(32.W)))
  val in1 = IO(Input(UInt(32.W)))
  val out = IO(Output(UInt(32.W)))
  out := in0 + in1
}

class ParentChild extends RawModule {
  val in0 = IO(Input(UInt(4.W)))
  val in1 = IO(Input(UInt(4.W)))
  val out = IO(Output(UInt(32.W)))

  val u_add = Module(new Add)()

  val add10 = Cat(in0, 10.U(28.W))
  u_add("in0") := add10
  u_add("in1") := Cat(Seq(5.U(31.W), 1.U))

  out := u_add("out") + 9.U
}
