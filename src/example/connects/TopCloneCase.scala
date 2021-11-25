package example

import knitkit._

class Inner extends RawModule {
  val valid = IO(Input(Bool()))
  val rdata = IO(Output(UInt(4.W)))
  val ready = IO(Output(Bool()))

  rdata := 5.U
  ready := valid
}

class Middle extends RawModule {
  val ready = IO(Output(Bool()))
  val rdata = IO(Output(UInt(4.W)))

  val u_inner  = Module(new Inner)()

  cloneIO(u_inner("valid")).suggestName("ABC").prefix("tx").suffix("todo")

  ready  := u_inner("ready")
  rdata := u_inner("rdata")
}

class TopCloneCase extends RawModule {

  val u_middle = Module(new Middle)()
  cloneIO(u_middle("valid")).clearWithName("BAR")
  cloneIO(u_middle("ready")).clearWithName("FOO")
  cloneIO(u_middle("rdata")).clearWithName("CAR")
}
