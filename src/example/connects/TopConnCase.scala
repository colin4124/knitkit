package example

import knitkit._

class Inner extends RawModule {
  val valid = IO(Input(Bool()))
  val rdata = IO(Output(UInt(4.W)))
  val ready = IO(Output(Bool()))

  rdata := 5.U
  ready := 1.B
}

class Middle extends RawModule {
  val addr  = IO(Output(UInt(4.W)))
  val wdata = IO(Output(UInt(4.W)))

  val u_inner  = Module(new Inner)()

  val tmp = cloneIO(u_inner("valid")).suggestName("ABC").prefix("tx").suffix("todo")

  addr  := u_inner("ready")
  wdata := u_inner("rdata")
}

class TopCloneCase extends RawModule {

  val u_middle = Module(new Middle)()
  cloneIO(u_middle("valid")).clearWithName("BAR")
  cloneIO(u_middle("addr")).clearWithName("FOO")
  cloneIO(u_middle("wdata")).clearWithName("CAR")
}
