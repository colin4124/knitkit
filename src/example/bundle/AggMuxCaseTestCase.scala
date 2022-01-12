package example

import knitkit._

class AggMuxCaseTestCase extends RawModule {
  def info = Agg(
    "a" -> UInt(4.W),
    "b" -> UInt(4.W),
    "c" -> UInt(4.W),
  )

  val sel = IO(Input(UInt(3.W)))

  val out = IO(Output(info))

  val inner = Wire(Vec(8, info))

  (0 until 8) foreach { idx =>
    inner(idx) <> idx.U
  }

  val cases = (1 until 8) map { idx =>
    (sel === idx.U) -> inner(idx)
  }

  out <> MuxCase(inner(0), cases)
}


