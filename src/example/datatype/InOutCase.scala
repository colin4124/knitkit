package example

import knitkit._

class IOPadInOut extends ExtModule {
  val I   = IO(Input(Bool()))
  val OEN = IO(Input(Bool()))
  val O   = IO(Output(Bool()))
  val Pad = IO(InOut(Bool()))
}

class InOutCase extends RawModule {
  val I   = IO(Input(Bool()))
  val OEN = IO(Input(Bool()))
  val O   = IO(Output(Bool()))
  val Pad = IO(InOut(Bool()))

  val pad = Module(new IOPadInOut())()

  pad("I"  ) <> I
  pad("OEN") <> OEN
  pad("O"  ) <> O
  pad("Pad") <> Pad
}
