package example

import knitkit._

class WhenCase extends RawModule {
  val clk = IO(Input(Clock()))
  setClock(clk)
  val sel1 = IO(Input(Bool()))
  val sel2 = IO(Input(Bool()))
  val out1 = IO(Output(UInt(3.W)))
  val out2 = IO(Output(UInt(3.W)))

  val foo = Wire(UInt(3.W))
  val bar = Reg(UInt(3.W))

  foo := 2.U
  when (sel1) {
    foo := 5.U
  }
  bar := 4.U
  when (sel2) {
    bar := 3.U
  }
  out1 := foo
  out2 := bar
}
