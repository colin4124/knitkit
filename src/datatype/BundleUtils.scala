package knitkit

class Valid(orig: Data) extends Bundle {
  IO(
    "valid" -> Output(Bool()),
    "bits"  -> Output(orig),
  )

  def fire: Bits = apply("valid").asBits
}

object Valid {
  def apply(orig: Data): Valid = new Valid(orig)
}

class Decoupled(orig: Data) extends Bundle {
  IO(
    "valid" -> Output(Bool()),
    "ready" -> Input(Bool()),
    "bits"  -> Output(orig),
  )

  def fire: Bits = apply("valid").asBits && apply("ready").asBits
}

object Decoupled {
  def apply(orig: Bundle): Decoupled = new Decoupled(orig)
}

