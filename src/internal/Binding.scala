package knitkit.internal

import knitkit._
import ir._

object requireIsHardware {
  def apply(node: Data, msg: String = ""): Unit = {
    if (!node.isSynthesizable) {
      val prefix = if (msg.nonEmpty) s"$msg " else ""
      throw ExpectedHardwareException(s"$prefix'$node' must be hardware, " +
                                        "not a bare knitkit type. Perhaps you forgot to wrap it in Wire(_) or IO(_)?")
    }
  }
}

object requireIsknitkitType {
  def apply(node: Data, msg: String = ""): Unit = if (node.isSynthesizable) {
    val prefix = if (msg.nonEmpty) s"$msg " else ""
    throw ExpectedknitkitTypeException(s"$prefix'$node' must be a knitkit type, not hardware")
  }
}

sealed trait Binding {
  def location: Option[BaseModule]
}

sealed trait UnconstrainedBinding extends Binding {
  def location: Option[BaseModule] = None
}

sealed trait ConstrainedBinding extends Binding {
  def module: BaseModule
  def location: Option[BaseModule] = Some(module)
}

sealed trait ReadOnlyBinding extends Binding

case class OpBinding  (module: RawModule ) extends ConstrainedBinding with ReadOnlyBinding
case class PortBinding(module: BaseModule) extends ConstrainedBinding
case class RegBinding (module: RawModule ) extends ConstrainedBinding
case class WireBinding(module: RawModule ) extends ConstrainedBinding
case class EnumBinding(module: RawModule, lit: Literal) extends ConstrainedBinding

case object LitBinding extends UnconstrainedBinding with ReadOnlyBinding
