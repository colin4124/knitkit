package example

import knitkit._

class RegInitVecCase extends RawModule {
  val clk     = IO(Input(Clock()))
  val rst     = IO(Input(Reset()))
  val foo     = IO(Vec(3, Input(UInt(3.W))))
  val foo_out = IO(Vec(3, Output(UInt(3.W))))

  val reg_foo = withClockAndReset(clk, rst) { Vec(3, RegInit(0.U(3.W))) }

  reg_foo <> foo
  foo_out <> reg_foo
}
