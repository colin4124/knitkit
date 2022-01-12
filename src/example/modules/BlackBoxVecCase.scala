package example

import knitkit._

class IBUFVec extends ExtModule {
  val io = IO(Vec(2, Agg(
    "O"  -> Output(Clock()),
    "I"  -> Input(Clock()),
    "IB" -> Input(Clock()),
  )), "")
}

class BlackBoxVecCase extends RawModule {
  val io = IO(Vec(2, Agg(
    "O"  -> Output(Clock()),
    "I"  -> Input(Clock()),
    "IB" -> Input(Clock()),
  )), "")

  val ibuf   = Module(new IBUFVec)

  val u_ibuf   = ibuf()

  u_ibuf("io") <> io
}
