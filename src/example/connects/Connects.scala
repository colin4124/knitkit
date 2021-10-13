package example

import knitkit._

object Connects {
  val modules = Seq(
    () => new TopCloneCase,
    () => new TopAutoCase,
  )
}
