package example

import knitkit._

class Bitwise_Ops extends RawModule {
  val x         = IO(Input(UInt(32.W)))
  val invertedX = IO(Output(UInt(32.W)))
  val hiBits    = IO(Output(UInt(32.W)))
  val OROut     = IO(Output(UInt(32.W)))
  val XOROut    = IO(Output(UInt(32.W)))

  invertedX := ~x                   // Bitwise NOT
  hiBits    :=  x & "h_ffff_0000".U // Bitwise AND
  OROut     := invertedX | hiBits   // Bitwise OR
  XOROut    := invertedX ^ hiBits   // Bitwise XOR
}
