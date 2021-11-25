package example

import knitkit._

class ChildChildBeta extends RawModule {
  val out = IO(Output(UInt(5.W)))

  val u_slave  = Module(new Slave)()
  val u_master = Module(new Master)()

  u_slave ("valid") := u_master("valid")
  u_slave ("addr" ) := u_master("addr" )
  u_slave ("wdata") := u_master("wdata")
  u_master("rdata") := u_slave ("rdata")
  u_master("ready") := u_slave ("ready")

  out := u_slave("bus_out") | u_master("bus_out") | u_master("valid")
}
