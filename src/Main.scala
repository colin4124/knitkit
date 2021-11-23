package knitkit

import example._

object Main extends App {
  def genVerilogFile(modules: Seq[() => RawModule], dest: String): Unit = {
    modules foreach { m =>
      Driver.execute(m, dest)
    }
  }

  // genVerilogFile(DataType.modules, args(0)+"/datatype")
  genVerilogFile(Operators.modules, args(0)+"/operators")
  // genVerilogFile(Modules.modules, args(0)+"/modules")
  // genVerilogFile(Statements.modules, args(0)+"/statements")
  // genVerilogFile(Bundles.modules, args(0)+"/bundles")
  // genVerilogFile(Connects.modules, args(0)+"/connects")

  // println(Driver.genVerilog(() => new Mux2))
}
