package example

import knitkit._

class InferredWidth extends RawModule {
  val out1 = IO(Output(UInt(5.W)))
  val out2 = IO(Output(UInt(3.W)))
  val out3 = IO(Output(SInt(4.W)))
  val out4 = IO(Output(SInt(4.W)))

  /* 根据左边的值自动推导宽度 */
  out1 := 1.U
  out2 := 5.U          // 无符号 3位整型
  out3 := 5.S          // 有符号 4位整型
  out4 := -8.S         // ４位十进制负数
}
