package example

import knitkit._

class BusBundle extends Bundle {
  IO(
    "valid" -> Input(Bool()),
    "addr"  -> Input(UInt(4.W)),
    "wdata" -> Input(UInt(4.W)),
    "rdata" -> Output(UInt(4.W)),
    "ready" -> Output(Bool()),
  )
}

class SlaveBundle extends RawModule {
  val bus = IO((new BusBundle).prefix("slv"), "")

  val bus_out = IO(Output(UInt(5.W)))

  bus_out := bus("valid") | bus("addr") | bus("wdata")

  bus("rdata") := bus("wdata")
  bus("ready") := bus("valid")
}

class MasterBundle extends RawModule {
  val bus = IO((new BusBundle).prefix("mst").flip, "")

  val bus_out = IO(Output(UInt(5.W)))

  bus("valid") := 1.B
  bus("addr" ) := 12.U
  bus("wdata") := 12.U

  bus_out := bus("ready") & bus("rdata")
}

class ChildChildBundle extends RawModule {
  val out = IO(Output(UInt(5.W)))

  val u_slave  = Module(new SlaveBundle)()
  val u_master = Module(new MasterBundle)()

  u_slave("bus") <> u_master("bus")

  out := u_slave("bus_out") | u_master("bus_out")
}
