package example

import knitkit._

class Piece(const: Int) extends RawModule {
  val in0 = IO(Input(UInt(4.W)))
  val out = IO(Output(UInt(4.W)))
  out := in0 & const.U
}

class AbundantModules(num: Int) extends RawModule {
  val in0 = IO(Vec(num, Input(UInt(4.W))))
  val out = IO(Output(UInt(4.W)))

  val out_array = (0 until num) map { idx =>
    val vec_m = Module(new Piece(idx))().suggestName(s"vec_m_$idx")
    vec_m("in0") := in0(idx)
    vec_m("out").asBits
  }

  val out_res = WireInit(CatGroup(out_array, 8, "out_res"))

  out := out_res.xorR
}
