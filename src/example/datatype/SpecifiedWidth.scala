package example

import knitkit._

class SpecifiedWidth extends RawModule {
  val out1 = IO(Output(UInt(4.W)))
  val out2 = IO(Output(SInt(32.W)))
  val out3 = IO(Output(SInt(7.W)))
  val out4 = IO(Output(UInt(8.W)))

  /* 也可以指定宽度 */
  out1 := 8.U(4.W)
  out2 := -152.S(32.W) // 32位有符号十进制数
  out3 := 5.asSInt(7.W) // signed decimal 7-bit lit of type SInt
  out4 := 5.asUInt(8.W) // unsigned decimal 8-bit lit of type UInt
}
