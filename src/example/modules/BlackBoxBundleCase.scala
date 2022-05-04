package example

import knitkit._

class IBUFBundle extends ExtModule {
  val io = IO(Bundle(
    "O"  -> Output(Clock()),
    "I"  -> Input(Clock()),
    "IB" -> Input(Clock()),
  ), "")
}

class BlackBoxBundleCase extends RawModule {
  val clk_125M = IO(Input(Clock()))
  val clk_25M  = IO(Input(Clock()))
  val sel      = IO(Input(Bool()))
  val clk_out  = IO(Output(Clock()))

  val ibufds = Module(new IBUFDS    )
  val ibuf   = Module(new IBUFBundle)

  val u_ibufds = ibufds()
  val u_ibuf   = ibuf()

  u_ibufds("I" ) <> clk_125M
  u_ibufds("IB") <> clk_25M

  u_ibuf("io")("I" ) <> clk_125M
  u_ibuf("io")("IB") <> clk_25M

  clk_out := Mux(sel, u_ibufds("O"), u_ibuf("io")("O"))
}
