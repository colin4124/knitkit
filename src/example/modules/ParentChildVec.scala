package example

import knitkit._

class AddVec extends RawModule {
  val in0 = IO(Vec(10, Input(UInt(32.W))))
  val in1 = IO(Vec(10, Input(UInt(32.W))))
  val out = IO(Vec(10, Output(UInt(32.W))))

  (0 until 10) foreach { idx =>
    out(idx) := in0(idx) + in1(idx)
  }
}

class ParentChildVec extends RawModule {
  val in0 = IO(Vec(10, Input(UInt(32.W))))
  val in1 = IO(Vec(10, Input(UInt(32.W))))
  val out = IO(Vec(10, Output(UInt(32.W))))

  val u_add = Module(new AddVec)()

  u_add("in0") <> in0
  u_add("in1") <> in1

  val foo = u_add("out")(0)

  out <> u_add("out")
}

