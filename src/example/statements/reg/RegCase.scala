package example

import knitkit._

class RegCase extends RawModule {
  val clk = IO(Input(Clock()))
  val clk_neg = IO(Input(ClockNeg()))
  val in  = IO(Input(UInt(3.W)))
  val out = IO(Output(UInt(3.W)))
  val out_neg = IO(Output(UInt(3.W)))

  val delayReg = withClock(clk) { Reg(UInt(3.W)) }
  val negReg = withClock(clk_neg) { Reg(UInt(3.W)) }

  delayReg := in
  negReg := in
  out := delayReg
  out_neg := negReg
}
