package example

import knitkit._

class LitValue1 extends RawModule {
  val out1 = IO(Output(UInt(5.W)))
  val out2 = IO(Output(UInt(4.W)))
  val out3 = IO(Output(UInt(3.W)))
  val out4 = IO(Output(SInt(4.W)))
  val out5 = IO(Output(SInt(4.W)))
  val out6 = IO(Output(SInt(32.W)))
  val out7 = IO(Output(UInt(4.W)))
  val out8 = IO(Output(UInt(4.W)))
  val out9 = IO(Output(UInt(4.W)))
  val outB0 = IO(Output(Bool()))
  val outB1 = IO(Output(Bool()))
  val outB2 = IO(Output(Bool()))
  val outB3 = IO(Output(Bool()))

  /* 根据左边的值自动推导宽度 */
  out1 := 1.U
  out3 := 5.U          // 无符号 3位整型
  out4 := 5.S          // 有符号 4位整型
  out5 := -8.S         // ４位十进制负数
  /* 也可以指定宽度 */
  out2 := 8.U(4.W)
  out6 := -152.S(32.W) // 32位有符号十进制数
  /* 字符串常量 */
  out7 := "ha".U       // ４位16进制
  out8 := "o12".U      // 4位八进制
  out9 := "b1010".U    // 4位二进制
  /* 布尔类型 */
  outB0 := true.B
  outB1 := false.B
  outB2 := 1.B
  outB3 := 0.B
}
