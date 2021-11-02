package example

import knitkit._

object DataType {
  val modules = Seq(
    () => new InferredWidth,
    () => new SpecifiedWidth,
    () => new StringLiteral,
    () => new BooleanType,
    () => new Casting,
    () => new AnalogCase,
    () => new DontCareCase,
  )
}
