package example

import knitkit._

class VecMuxCaseTestCase extends RawModule {
  val total = 4

  val clk  = IO(Input(Clock()))
  val rstn = IO(Input(AsyncNegReset()))
  setClockAndReset(clk, rstn)

  val sel = IO(Input(UInt(3.W)))

  val out = IO(Output(Vec(3, UInt(3.W))))

  val inner  = Wire(Vec(total, Vec(3, UInt(3.W))))
  val outter = RegInit(Vec(3, 0.U(3.W)))

  (0 until total) foreach { idx =>
    inner(idx) <> idx.U
  }

  val cases = (1 until total) map { idx =>
    (sel === idx.U) -> inner(idx)
  }

  outter <> WireInit(MuxCase(inner(0), cases)).suggestName("outter_mux")
  out <> outter

}
