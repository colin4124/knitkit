package example

import knitkit._

object Statements {
  val modules = Seq(
    () => new WireCase,
    () => new RegCase,
    () => new RegVecCase,
    () => new RegInferredCase,
    () => new RegInitCase,
    () => new RegInitVecCase,
    () => new RegInitDoubleArgCase,
    () => new RegInitInferredLitCase,
    () => new RegInitInferredNonLitCase,
    () => new RegNextCase,
    () => new RegNextInitCase,
    () => new WhenTestCase,
    () => new WhenCaseExample,
    () => new WhenAloneCase,
    () => new ElseWhenCase,
    () => new OtherwiseCase,
    () => new SwitchCase,
    () => new SwitchLitCase,
    () => new SwitchWhenCase,
    () => new SwitchBundleCase,
  )
}
