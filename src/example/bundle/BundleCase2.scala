package example

import knitkit._

class BarBundle(bits: Bundle) extends Bundle {
  IO(
    "valid" -> Input(Bool()),
    "ready" -> Output(Bool()),
    "bits"  -> Input(bits),
  )

  def fire = this("valid") && this("ready")
}

class FooBundle extends Bundle {
  IO(
    "addr"  -> UInt(3.W),
    "wdata" -> UInt(3.W),
  )
}

class BundleCase2 extends RawModule {
  val io = IO(new BarBundle(new FooBundle))

  val data_out = IO(Output(new FooBundle))

  when (io.fire) {
    data_out <> io("bits")
  } .otherwise {
    data_out <> 0.U
  }

}
