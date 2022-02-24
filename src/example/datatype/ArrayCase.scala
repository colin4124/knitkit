package example

import knitkit._

class ArraySub extends ExtModule {
  val y_mem = IO(Input((Arr(UInt(8.W), 4))))
}

class ArrayCase extends RawModule {
  val clk  = IO(Input(Clock()))
  val rstn = IO(Input(AsyncNegReset()))
  setClockAndReset(clk, rstn)

  val sel_1 = IO(Input(Bool()))
  val sel_2 = IO(Input(Bool()))

  val y_mem = IO(Input((Arr(UInt(8.W), 4))))

  val R_old = Wire(Arr(UInt(6.W), 4, 6))

  val sub = Module(new ArraySub)()

  val y1 = RegInit(ArrInit(0.U(1.W), 3, 4, 6))

  val y2 = Wire(Arr(UInt(8.W), 4))
  val y3 = RegInit(ArrInit(0.U(6.W), 2, 4))
  val y4 = RegInit(ArrInit(0.U(6.W), 2, 4))

  y2 := y_mem
  y_mem <> sub("y_mem")

  y1 := 1.B
  y3(1) := 59.U

  y4(1, 3) := 24.U

  WhenCase(R_old, y1(0), Seq(
    sel_1 -> y1(1),
    sel_2 -> y1(2),
  ))
}
