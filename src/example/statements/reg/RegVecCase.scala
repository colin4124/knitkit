package example

import knitkit._

class RegVecCase extends RawModule {
  val clk = IO(Input(Clock()))
  val in  = IO(Vec(3, Input(UInt(3.W))))
  val out = IO(Vec(3, Output(UInt(3.W))))

  val delayReg = withClock(clk) { Reg(Vec(Seq(UInt(3.W), UInt(3.W), UInt(3.W)))) }
  // val delayReg = withClock(clk) { Reg(Vec(3, UInt(3.W))) }

  delayReg <> in
  out <> delayReg
}


