package knitkit

import example._

object Main extends App {
  val modules = List(
    () => new Mux2,
    () => new WhichFruit,
    () => new WireCase,
    () => new RegCase,
    () => new RegInferredCase,
    () => new RegInitCase,
    () => new RegInitDoubleArgCase,
    () => new RegInitInferredLitCase,
    () => new RegInitInferredNonLitCase,
    () => new RegNextCase,
    () => new RegNextInitCase,
    () => new ParentChild,
    () => new ChildChild,
    () => new BlackBoxCase,
    () => new BundleCase,
    () => new PacketCase,
    () => new WhenCase,
    () => new ElseWhenCase,
    () => new OtherwiseCase,
    () => new SwitchCase,
    () => new SwitchLitCase,
    () => new SwitchWhenCase,
  )

  modules foreach { m =>
    Driver.execute(m, args(0))
  }
}
