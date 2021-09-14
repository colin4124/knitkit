package example

import knitkit._

class StringLiteral extends RawModule {
  val out1 = IO(Output(UInt(4.W)))
  val out2 = IO(Output(UInt(4.W)))
  val out3 = IO(Output(UInt(4.W)))
  val out4 = IO(Output(UInt(32.W)))
  val out5 = IO(Output(UInt(8.W)))
  val out6 = IO(Output(UInt(6.W)))
  val out7 = IO(Output(UInt(12.W)))

  /* 字符串常量 */
  out1 := "ha".U       // ４位16进制
  out2 := "o12".U      // 4位八进制
  out3 := "b1010".U    // 4位二进制
  out4 := "h_dead_beef".U   // 32-bit lit of type UInt
  out5 := "ha".asUInt(8.W)     // hexadecimal 8-bit lit of type UInt
  out6 := "o12".asUInt(6.W)    // octal 6-bit lit of type UInt
  out7 := "b1010".asUInt(12.W) // binary 12-bit lit of type UInt
}
