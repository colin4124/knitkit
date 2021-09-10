package example

import knitkit._

class Logical_Operations extends RawModule {
  val sel   = IO(Input(Bool()))
  val a     = IO(Input(Bool()))
  val b     = IO(Input(Bool()))
  val sleep = IO(Output(Bool()))
  val hit   = IO(Output(Bool()))
  val stall = IO(Output(Bool()))
  val out   = IO(Output(Bool()))

  sleep := !a
  hit   := a && b
  stall := a || b
  out   := Mux(sel, a, b)
}
