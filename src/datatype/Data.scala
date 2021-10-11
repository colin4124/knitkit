package knitkit

import ir._
import internal._
import internal.Builder._

sealed abstract class SpecifiedDirection
object SpecifiedDirection {
  case object Unspecified extends SpecifiedDirection
  case object Output      extends SpecifiedDirection
  case object Input       extends SpecifiedDirection
  case object InOut       extends SpecifiedDirection
  case object Internal    extends SpecifiedDirection

  def flip(dir: SpecifiedDirection): SpecifiedDirection = dir match {
    case Output      => Input
    case Input       => Output
    case InOut       => InOut
    case _  => error(s"use Input/Output before Flip!")
  }

  def specifiedDirection[T<:Data](source: T, dir: SpecifiedDirection): Unit = source match {
    case a: Aggregate =>
      a.getElements foreach { x => specifiedDirection(x, dir) }
    case b: Bits =>
      b.direction = dir
  }
}

object Input {
  def apply[T<:Data](source: T): T = {
    SpecifiedDirection.specifiedDirection(source, SpecifiedDirection.Input)
    source
  }
}

object Output {
  def apply[T<:Data](source: T): T = {
    SpecifiedDirection.specifiedDirection(source, SpecifiedDirection.Output)
    source
  }
}

object InOut {
  def apply[T<:Data](source: T): T = {
    SpecifiedDirection.specifiedDirection(source, SpecifiedDirection.InOut)
    source
  }
}

abstract class Data extends HasId with DataOps {
  def prefix(s: String): this.type
  def suffix(s: String): this.type
  def flip: this.type

  def apply(name: String): Data

  var bypass: Boolean = false

  var used: Boolean = false

  var _binding: Option[Binding] = None

  def bindingOpt: Option[Binding] = _binding

  def binding: Binding = _binding.get
  def binding_=(target: Binding): Unit = {
    if (_binding.isDefined) {
      throw RebindingException(s"Attempted reassignment of binding to $this")
    }
    _binding = Some(target)
  }

  def bind(target: Binding): Unit

  def isSynthesizable: Boolean = _binding match {
    case Some(_) => true
    case None => false
  }

  def lref: Node = {
    requireIsHardware(this)
    bindingOpt match {
      case Some(binding: ReadOnlyBinding) => throwException(s"internal error: attempted to generate LHS ref to ReadOnlyBinding $binding")
      case Some(binding: Binding) => Node(this)
      case None => throwException(s"internal error: unbinding in generating LHS ref")
    }
  }

  def ref: Expression = {
    requireIsHardware(this)
    bindingOpt match {
      case Some(_) => Node(this)
      case None => throwException(s"internal error: unbinding in generating RHS ref")
    }
  }

  final def := (that: Data): Unit = (this, that) match {
    case (l: Bits, r: Bits) => l.connect(r)
    case _ => Builder.error(":= only use between Bits")
  }
  final def <> (that: Data): Unit = {
    (this.binding, that.binding) match {
      case (_: ReadOnlyBinding, _: ReadOnlyBinding) => throwException(s"Both $this and $that are read-only")
      case _ =>
    }
    try {
      BiConnect.connect(this, that, Builder.forcedUserModule)
    } catch {
      case BiConnectException(message) =>
        throwException(
          s"Connection between left ($this) and source ($that) failed @$message"
        )
    }
  }
}
