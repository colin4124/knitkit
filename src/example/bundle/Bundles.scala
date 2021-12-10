package example

import knitkit._

object Bundles {
  val modules = Seq(
    () => new ChildChildAgg,
    () => new BundleCase,
    () => new BundleDontCareCase,
    () => new PacketCase,
    () => new PacketAggCase,
    () => new AggPrefixSufixCase,
    () => new AggConnBits,
    () => new VecConnBits,
    () => new VecCase,
    () => new VecAggCase,
    () => new AsUIntAggCase,
    () => new AsUIntVecCase,
    () => new AsUIntGroupAggCase,
    () => new AsUIntGroupVecCase,
  )
}
