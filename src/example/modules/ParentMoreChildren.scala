package example

import knitkit._
import scala.collection.mutable.{ArrayBuffer, HashMap}

class AddConst(const: Int) extends RawModule {
  // override def desiredName: String = s"Add_$const"
  val in  = IO(Input(UInt(32.W)))
  val out = IO(Output(UInt(32.W)))
  out := in + const.U
}

class ParentMoreChildren extends RawModule {
  val in  = IO(Input(UInt(4.W)))
  val out = IO(Output(UInt(32.W)))

  val u_add_1 = Module(new AddConst(1))()
  val u_add_2 = Module(new AddConst(2))()

  u_add_1("in") := in
  u_add_2("in") := in

  out := u_add_1("out") & u_add_2("out")
}


