package example

import knitkit._

class IBUFDS extends ExtModule (
  Map(
    "DIFF_TERM" -> "TRUE",
    "IOSTANDARD" -> "DEFAULT"
  )
) {
  val O  = IO(Output(Clock()))
  val I  = IO(Input(Clock()))
  val IB = IO(Input(Clock()))
}

class IBUF extends ExtModule {
  val O  = IO(Output(Clock()))
  val I  = IO(Input(Clock()))
  val IB = IO(Input(Clock()))
}

class BlackBoxCase extends RawModule {
  val clk_125M = IO(Input(Clock()))
  val clk_25M  = IO(Input(Clock()))
  val sel      = IO(Input(Bool()))
  val clk_out  = IO(Output(Clock()))

  val ibufds = Module(new IBUFDS)
  val ibuf   = Module(new IBUF  )

  val u_ibufds = ibufds()
  val u_ibuf   = ibuf()

  u_ibufds("I" ) <> clk_125M
  u_ibufds("IB") <> clk_25M

  u_ibuf("I" ) <> clk_125M
  u_ibuf("IB") <> clk_25M

  clk_out := Mux(sel, u_ibufds("O"), u_ibuf("O"))
}
