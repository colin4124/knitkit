package example

import knitkit._

class Bitfield_Manipulation extends RawModule {
  val sel        = IO(Input(UInt(4.W)))
  val x          = IO(Input(UInt(16.W)))
  val dynamicSel = IO(Output(Bool()))
  val xLSB       = IO(Output(Bool()))
  val xTopNibble = IO(Output(UInt(4.W)))
  val usDebt     = IO(Output(UInt(12.W)))
  val float      = IO(Output(UInt(17.W)))

  dynamicSel := x(sel, "foo")
  xLSB       := x(0)
  xTopNibble := x(15, 12)
  usDebt     := Fill(3, "hA".U)
  float      := Cat(xLSB, xTopNibble, usDebt)
}
