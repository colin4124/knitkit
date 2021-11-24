package example

import knitkit._

class CatCase extends RawModule {
  val out        = IO(Output(UInt(74.W)))

  // out := CatGroup((1 to 7).reverse map { _.U }, 2, "out") // Pass
  out := CatGroup((1 to 20).reverse map { _.U }, 2, "out")
  // out := CatGroup((1 to 8).reverse map { _.U }, 2, "out") // Pass
}
