package example

import knitkit._

class PieceA extends RawModule {
  val out = IO(Output(Bool()))
  out := 1.B
}

class TwoChildrenCase extends RawModule {
  val out = IO(Output(Bool()))

  val piece = Module(new PieceA)

  val u_1 = piece()
  val u_2 = piece()
  val u_3 = piece()

  out := u_1("out").asBits &
    u_2("out").asBits &
    u_3("out").asBits
}



