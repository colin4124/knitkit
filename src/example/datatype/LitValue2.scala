package example

import knitkit._

class LitValue2 extends RawModule {
  val out1 = IO(Output(UInt(32.W)))
  val out2 = IO(Output(UInt(8.W)))
  val out3 = IO(Output(UInt(6.W)))
  val out4 = IO(Output(UInt(12.W)))
  val out5 = IO(Output(SInt(7.W)))
  val out6 = IO(Output(UInt(8.W)))

  out1 := "h_dead_beef".U   // 32-bit lit of type UInt

  out2 := "ha".asUInt(8.W)     // hexadecimal 8-bit lit of type UInt
  out3 := "o12".asUInt(6.W)    // octal 6-bit lit of type UInt
  out4 := "b1010".asUInt(12.W) // binary 12-bit lit of type UInt

  out5 := 5.asSInt(7.W) // signed decimal 7-bit lit of type SInt
  out6 := 5.asUInt(8.W) // unsigned decimal 8-bit lit of type UInt
}
