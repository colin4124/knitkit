package example

import knitkit._

class VecMuxTestCase extends RawModule {
  val sel = IO(Input(Bool()))

  val out = IO(Output(Vec(3, UInt(8.W))))

  val inner = Wire(Vec(2, Vec(3, UInt(8.W))))

  (0 until 2) foreach { idx =>
    inner(idx) <> (idx+10).U
  }

  out <> Mux(sel, inner(1), inner(0))
}
