package example

import knitkit._

class ArraySub extends ExtModule {
  // val y_mem = IO(Input((Arr(UInt(8.W), 4))))
  val y_out = IO(Output((Arr(UInt(8.W), 4))))
  // val y_out = IO(Output((UInt(8.W))))
}

class ArrayCase extends RawModule {
  val clk  = IO(Input(Clock()))
  val rstn = IO(Input(AsyncNegReset()))
  setClockAndReset(clk, rstn)

  // val xxxx_out = IO(Output((Arr(UInt(8.W), 4))))
  val y_out = IO(Output((Arr(UInt(8.W), 2, 4))))

  // val R_old = Wire(Arr(UInt(6.W), 4, 6))

  val sub = Module(new ArraySub)()

  // val y1 = RegInit(ArrInit(0.U(1.W), 3, 4, 6))

  // val y2 = Wire(Arr(UInt(8.W), 4))
  // val y3 = RegInit(ArrInit(0.U(6.W), 2, 4))
  // val y4 = RegInit(ArrInit(0.U(6.W), 2, 4))

  // val y3_x = RegInit(0.U)

  // val x = WireInit(ArrInit(0.U(8.W), 2))
  // val x = Wire(Arr(UInt(8.W), 4))
  // val x = Wire(Arr(UInt(8.W), 2, 4))
  // val y = Wire((Arr(UInt(8.W), 4)))

  // val x = Wire(UInt(8.W))

  // val x_x = WireInit(0.B)


  // y2 := y_mem

  // y2(0) := y_mem(3)
  // y2(1) := y_mem(2)
  // y2(2) := y_mem(1)
  // y2(3) := y_mem(0)

  // y_mem <> sub("y_mem")

  // x := sub("y_out")

  // xxxx_out <> sub("y_out")
  y_out(0) <> sub("y_out")

  // x(0) := sub("y_out")
  // x(1) := y

  // x := sub("y_out")

  // x_x := sub("x_out")

  // println(sub("x_out").asBits._conn)
  // println(x_x._conn)

  // y1 := 1.B
  // y3(1) := 59.U

  // y4(1, 3) := 24.U

  // WhenCase(R_old, y1(0), Seq(
  //   sel_1 -> y1(1),
  //   sel_2 -> y1(2),
  // ))
}
