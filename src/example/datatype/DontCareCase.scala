package example

import knitkit._

class DontCareCase extends RawModule {
  val out1 = IO(Output(Clock()))
  val out2 = IO(Output(Reset()))
  val out3 = IO(Output(AsyncPosReset()))
  val out4 = IO(Output(AsyncNegReset()))
  val out5 = IO(Output(UInt(8.W)))
  val out6 = IO(Output(SInt(6.W)))
  val out7 = IO(Output(Bool()))
  //val out8 = IO(Analog())

  out1 <> DontCare
  out2 <> DontCare
  out3 <> DontCare
  out4 <> DontCare
  out5 <> DontCare
  out6 <> DontCare
  out7 <> DontCare
  //out8 := DontCare
}
