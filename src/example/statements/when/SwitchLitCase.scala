package example

import knitkit._

class SwitchLitCase extends RawModule {
  val clk = IO(Input(Clock()))
  val rst = IO(Input(Reset()))

  setClockAndReset(clk, rst)
  val in  = IO(Input(UInt(1.W)))
  val out = IO(Output(UInt(2.W)))
  val out_num = IO(Output(UInt(3.W)))

  val myreg = Reg(UInt(3.W))

  val state = RegInit(0.U)

  state   := in
  out_num := myreg

  switch (state) {
    is (0.U) {
      out := 1.U
      myreg := 2.U
    }
    is (1.U) {
      out := 3.U
      myreg := 4.U
    }
    default {
      out := 0.U
      myreg := 0.U
    }
  }
}
