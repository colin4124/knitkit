package example

import knitkit._

class ArrayVecSub extends ExtModule {
  val x_in  = IO(Vec(4, Vec(8, Input(UInt(7.W)))))
  val x_out = IO(Vec(4, Vec(8, Output(UInt(7.W)))))

  val y_in  = IO(Vec(2, Vec(4, Input(UInt(6.W)))))
  val y_out = IO(Vec(2, Vec(4, Output(UInt(6.W)))))
}

class ArrayVecCase extends RawModule {
  val x_in  = IO(Input(Arr(UInt(7.W), 4, 8)))
  val x_out = IO(Output(Arr(UInt(7.W), 4, 8)))

  val y_in  = IO(Input(Arr(UInt(6.W), 2, 4)))
  val y_out = IO(Output(Arr(UInt(6.W), 2, 4)))

  val u_sub = Module(new ArrayVecSub)()

  u_sub("x_in" ) <> x_in
  u_sub("x_out") <> x_out

  u_sub("y_in" ) <> y_in
  u_sub("y_out") <> y_out
}
