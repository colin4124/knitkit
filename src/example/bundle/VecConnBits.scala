package example

import knitkit._

class VecConnBits extends RawModule {
  def info = Bundle(
    "header" -> UInt(16.W),
    "addr"   -> UInt(16.W),
    "data"   -> UInt(32.W),
  )


  val io = IO(Output(Vec(2, info)))

  val inner = Wire(Vec(2, info))

  inner := 0.U

  io <> inner
}
