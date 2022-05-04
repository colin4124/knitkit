package example

import knitkit._

class SwitchBundleCase extends RawModule {
  val clk = IO(Input(Clock()))
  val rst = IO(Input(Reset()))

  setClockAndReset(clk, rst)
  val in  = IO(Input(UInt(1.W)))
  val out = IO(Vec(3, Output(UInt(3.W))))

  val reg3 = Vec(3, RegInit(3.U(3.W)))
  val reg4 = Vec(3, RegInit(4.U(3.W)))

  val state = RegInit(0.U)

  state := in

  switch (state) {
    is (0.U) {
      out <> reg3
    }
    is (1.U) {
      out <> reg4
    }
    default {
      out <> Vec(3, 0.U)
    }
  }
}

