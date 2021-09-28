package example

import knitkit._

class A extends RawModule {
  val valid = IO(Input(Bool()))
  val rdata = IO(Output(UInt(4.W)))
  val ready = IO(Output(Bool()))

  rdata := 5.U
  ready := 1.B
}

class B extends RawModule {
  val addr  = IO(Output(UInt(4.W)))
  val wdata = IO(Output(UInt(4.W)))

  val u_a  = Module(new A)()

  addr := 10.U
  wdata := 4.U
}

class TopAutoCase extends RawModule {
  val u_b = Module(new B)()
}

