package example

import knitkit._

class Shifts extends RawModule {
  val x         = IO(Input(UInt(3.W)))
  val y         = IO(Input(UInt(32.W)))
  val z         = IO(Input(SInt(32.W)))
  val twoToTheX = IO(Output(SInt(33.W)))
  val hiBits    = IO(Output(UInt(16.W)))
  val sigHiBits = IO(Output(SInt(16.W)))
  val usigHiBits = IO(Output(UInt(16.W)))

  //val imm = WireInit(0.S) // TODO
  val s_imm = WireInit(0.S(16.W))
  val u_imm = WireInit(0.U(16.W))

  s_imm := y.asSInt >> 16.U
  u_imm := z.asUInt >> 16.U

  twoToTheX := (1.S << x)
  hiBits    := y >> 16.U

  usigHiBits := u_imm
}
