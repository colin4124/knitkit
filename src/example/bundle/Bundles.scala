package example

import knitkit._

object Bundles {
  val modules = Seq(
    () => new ChildChildAgg,
    () => new BundleCase,
    () => new PacketCase,
    () => new PacketAggCase,
    () => new VecCase,
  )
}
