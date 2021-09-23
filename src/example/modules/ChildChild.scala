package example

import knitkit._

class Slave extends RawModule {
  val valid = IO(Input(Bool()))
  val addr  = IO(Input(UInt(4.W)))
  val wdata = IO(Input(UInt(4.W)))
  val rdata = IO(Output(UInt(4.W)))
  val ready = IO(Output(Bool()))

  val bus_out = IO(Output(UInt(5.W)))

  bus_out := valid | addr | wdata | rdata

  rdata := wdata
  ready := valid
}

class Master extends RawModule {
  val valid = IO(Output(Bool()))
  val addr  = IO(Output(UInt(4.W)))
  val wdata = IO(Output(UInt(4.W)))
  val rdata = IO(Input(UInt(4.W)))
  val ready = IO(Input(Bool()))

  val bus_out = IO(Output(UInt(5.W)))

  valid := 1.B
  addr  := 12.U
  wdata := 12.U

  bus_out := ready & rdata
}

class ChildChild extends RawModule {
  val out = IO(Output(UInt(5.W)))

  val u_slave  = Module(new Slave)()
  val u_master = Module(new Master)()

  u_slave ("valid") := u_master("valid")
  u_slave ("addr" ) := u_master("addr" )
  u_slave ("wdata") := u_master("wdata")
  u_master("rdata") := u_slave ("rdata")
  u_master("ready") := u_slave ("ready")

  out := u_slave("bus_out") | u_master("bus_out")
}
