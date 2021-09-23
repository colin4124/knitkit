package example

import knitkit._

object Modules {
  val modules = Seq(
    () => new Mux2,
    () => new WhichFruit,
    () => new ParentChild,
    () => new ParentChildBeta,
    () => new ChildChild,
    () => new BlackBoxCase,
    () => new BlackBoxAggCase,
    () => new InOutCase,
  )
}
