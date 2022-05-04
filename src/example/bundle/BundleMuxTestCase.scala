package example

import knitkit._

class BundleMuxTestCase extends RawModule {
  def info = Bundle(
    "a" -> UInt(4.W),
    "b" -> UInt(4.W),
    "c" -> UInt(4.W),
  )
  val sel = IO(Input(Bool()))

  val out = IO(Output(info))

  val inner = Wire(Vec(2, info))

  (0 until 2) foreach { idx =>
    inner(idx) <> (idx+10).U
  }

  out <> Mux(sel, inner(1), inner(0))
}
