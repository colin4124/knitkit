package knitkit

class Valid(orig: Data) extends Aggregate {
  val eles: Seq[(String, Data)] = Seq(
    "valid" -> Output(Bool()),
    "bits"  -> Output(orig),
  )

  def fire: Bits = apply("valid").asBits
}

object Valid {
  def apply(orig: Data): Valid = new Valid(orig)
}

class Decoupled(orig: Data) extends Aggregate {
  val eles: Seq[(String, Data)] = Seq(
    "valid" -> Output(Bool()),
    "ready" -> Input(Bool()),
    "bits"  -> Output(orig),
  )

  def fire: Bits = apply("valid").asBits && apply("ready").asBits
}

object Decoupled {
  def apply(orig: Aggregate): Decoupled = new Decoupled(orig)
}

