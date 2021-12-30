package example

import knitkit._

object whenAlone {
  def apply(cond: Bits, sink: Bits, source: Bits): Unit = {
    when (cond) {
      sink := source
    }
  }
}
class WhenAloneCase extends RawModule {
  val clk = IO(Input(Clock()))
  setClock(clk)
  val sel1 = IO(Input(Bool()))
  val sel2 = IO(Input(Bool()))
  val out1 = IO(Output(UInt(3.W)))
  val out2 = IO(Output(UInt(3.W)))

  val foo = Wire(UInt(3.W))
  val bar = Reg(UInt(3.W))

  foo := 2.U
  whenAlone(sel1, foo, 5.U)

  bar := 4.U
  whenAlone(sel2, bar, 3.U)

  out1 := foo
  out2 := bar
}

