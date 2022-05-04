package example

import knitkit._

class WhenCaseExample extends RawModule {
  val clk = IO(Input(Clock()))
  setClock(clk)
  val sel1 = IO(Input(Bool()))
  val sel2 = IO(Input(Bool()))
  val out1 = IO(Output(UInt(4.W)))
  val out2 = IO(Output(UInt(4.W)))

  val foo = Wire(UInt(4.W))
  val bar = Reg(UInt(4.W))

  foo := WhenCase(2.U, Seq(
    sel1 -> 5.U,
    sel2 -> 15.U,
  ))

  bar := WhenCase(4.U, Seq(
    sel1 -> 3.U,
    sel2 -> 13.U,
  ))

  out1 := foo
  out2 := bar
}


