package example

import knitkit._

class ArraySub extends ExtModule {
  val y_mem = IO(Input((Arr(UInt(8.W), 4))))
}

class ArrayCase extends RawModule {
  val clk  = IO(Input(Clock()))
  val rstn = IO(Input(AsyncNegReset()))
  setClockAndReset(clk, rstn)

  val y_mem = IO(Input((Arr(UInt(8.W), 4))))

  // val sub = Module(new ArraySub)()

  val y1 = RegInit(ArrInit(0.B, 12))
  // val y2 = Wire(Arr(UInt(8.W), 4))
  // val y3 = Reg(Arr(UInt(6.W), 2, 4))

  // y2 <> y_mem
  // y2 <> sub("y_mem")

  y1(0) := 1.B
  // y2(3) := 41.U
  // y3(1, 3) := 59.U
}
