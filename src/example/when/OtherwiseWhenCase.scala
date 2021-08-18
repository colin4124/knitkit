package example

import knitkit._

class OtherwiseCase extends RawModule {
  val clk = IO(Input(Clock()))
  val rst = IO(Input(AsyncNegReset()))
  setClockAndReset(clk, rst)
  val sel1 = IO(Input(Bool()))
  val sel2 = IO(Input(Bool()))
  val out1 = IO(Output(UInt(3.W)))
  val out2 = IO(Output(UInt(3.W)))
  val out3 = IO(Output(UInt(4.W)))

  val foo = Wire(UInt(3.W))
  val bar = Reg(UInt(3.W))
  val car = RegInit(14.U(4.W))

  // foo := 1.U
  when (sel1) {
    foo := 5.U
    when (sel2) {
      foo := 7.U
    }
  } otherwise {
    foo := 2.U
  }

  when (sel2) {
    bar := 3.U
  } otherwise {
    bar := 4.U
  }

  car := 1.U
  when (sel2) {
    car := 2.U
  } .elsewhen(sel1) {
    car := 3.U
  }

  out1 := foo
  out2 := bar
  out3 := car
}

