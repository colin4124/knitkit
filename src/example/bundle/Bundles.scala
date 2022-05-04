package example

import knitkit._

object Bundles {
  val modules = Seq(
    () => new ChildChildBundle,
    () => new BundleCase,
    () => new BundleCase2,
    () => new BundleDontCareCase,
    () => new PacketCase,
    () => new PacketBundleCase,
    () => new BundlePrefixSufixCase,
    () => new BundleConnBits,
    () => new VecConnBits,
    () => new VecCase,
    () => new VecIndexCase,
    () => new VecBundleCase,
    () => new AsUIntBundleCase,
    () => new AsUIntVecCase,
    () => new AsUIntGroupBundleCase,
    () => new AsUIntGroupVecCase,
    () => new DecoupledIOCase,
  )
}
