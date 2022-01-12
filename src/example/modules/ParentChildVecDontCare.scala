package example

import knitkit._

class ParentChildVecDontCare extends RawModule {
  val in0 = IO(Vec(10, Input(UInt(32.W))))
  val in1 = IO(Vec(10, Input(UInt(32.W))))
  val out = IO(Output(UInt(32.W)))

  val u_add = Module(new AddVec)()

  u_add("in0") <> in0
  u_add("in1") <> in1

  val foo = u_add("out")(0)

  out <> foo

  (1 until 32) foreach { idx =>
    u_add("out")(idx) <> DontCare
  }
}
