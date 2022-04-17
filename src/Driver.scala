package knitkit

import java.nio.file.{Files, Paths}
import java.io.{File, FileWriter}

import internal._

object Driver {
  def genVerilog(dut: () => RawModule): String = {
    val (circuit, _) = Builder.build(Module(dut()))
    Emitter.emit(circuit) map { case (_, v) => v } reduce { _ + _ }
  }
  def execute(dut: () => RawModule, dump_dir: String, one_file: Boolean = true): Unit = {
    val (circuit, _) = Builder.build(Module(dut()))

    val result = Emitter.emit(circuit)

    if (one_file) {
      val contents = result map { case (_, v) => v } reduce { _ + _ }
      Files.createDirectories(Paths.get(dump_dir));
      val file = new File(s"${dump_dir}/${circuit.name}.v")
      val w = new FileWriter(file)
      w.write(contents)
      w.close()

    } else {
      result foreach { case (m_name, v) =>
        Files.createDirectories(Paths.get(dump_dir));
        val file = new File(s"${dump_dir}/${m_name}.v")
        val w = new FileWriter(file)
        w.write(v)
        w.close()
      }
    }
  }
}
