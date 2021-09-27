package knitkit

import example._

object Main extends App {
  def genVerilog(modules: Seq[() => RawModule], dest: String): Unit = {
    modules foreach { m =>
      Driver.execute(m, dest)
    }
  }

  genVerilog(DataType.modules, args(0)+"/datatype")
  genVerilog(Operators.modules, args(0)+"/operators")
  genVerilog(Modules.modules, args(0)+"/modules")
  genVerilog(Statements.modules, args(0)+"/statements")
  genVerilog(Bundles.modules, args(0)+"/bundles")
  genVerilog(Connects.modules, args(0)+"/connects")
}
