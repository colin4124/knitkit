package example

import knitkit._

class AggConnBits extends RawModule {
  def info = Agg(
    "header" -> UInt(16.W),
    "addr"   -> UInt(16.W),
    "data"   -> UInt(32.W),
  )

  val io = IO(Output(info))

  val inner = Wire(info)

  inner := 0.U

  io <> inner
}
