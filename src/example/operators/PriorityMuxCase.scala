package example

import knitkit._

class PriorityMuxCase extends RawModule {
  val sel = IO(Vec(3, Input(Bool())))
  val in  = IO(Vec(3, Input(UInt(2.W))))
  val out = IO(Output(UInt(2.W)))

  out := PriorityMux(sel.elements.map(_.asBits) zip in.elements)
}