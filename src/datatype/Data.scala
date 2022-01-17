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
    case v: Vec =>
      v.getElements foreach { x => specifiedDirection(x, dir) }
    case a: Aggregate =>
      a.getElements foreach { x => specifiedDirection(x, dir) }
    case b: Bits =>
      b.direction = dir
  }
}

object Input {
  def apply[T<:Data](source: T, rename: String = ""): T = {
    SpecifiedDirection.specifiedDirection(source, SpecifiedDirection.Input)
    if (rename != "")
      source.suggestName(rename)
    source
  }
}

object Output {
  def apply[T<:Data](source: T, rename: String = ""): T = {
    SpecifiedDirection.specifiedDirection(source, SpecifiedDirection.Output)
    if (rename != "")
      source.suggestName(rename)
    source
  }
}

object InOut {
  def apply[T<:Data](source: T, rename: String = ""): T = {
    SpecifiedDirection.specifiedDirection(source, SpecifiedDirection.InOut)
    if (rename != "")
      source.suggestName(rename)
    source
  }
}

abstract class Data extends HasId with DataOps {
  def prefix(s: String): this.type
  def suffix(s: String): this.type
  def flip: this.type

  def apply(name: String): Data
  def apply(idx: Int*): Data

  def getPair: Seq[(String, Data)]
  def getElements: Seq[Data]

  def getDir: SpecifiedDirection

  var bypass: Boolean = false

  var used: Boolean = false

  var _binding: Option[Binding] = None

  def bindingOpt: Option[Binding] = _binding

  def binding: Binding = _binding.get
  def binding_=(target: Binding): Unit = {
    if (_binding.isDefined) {
      throw RebindingException(s"Attempted reassignment of binding to ${this.computeName(None, "")}")
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
      case Some(_) => _ref match {
        case Some(r) => Utils.bypass_cvt_type(Node(this), r)
        case None    => Node(this)
      }
      case None => throwException(s"internal error: unbinding in generating RHS ref")
    }
  }

  final def :-= (that: Data): Unit = :=(that, concise = true)
  final def := (that: Data, concise: Boolean = false): Unit = (this, that) match {
    case (l: Bits, r: Bits) => l.connect(r, concise)
    case (l: Aggregate, r: Bits) => l.getElements foreach { _.:=(r, concise) }
    case (l: Vec, r: Bits) => l.getElements foreach { _.:=(r, concise) }
    case (l: Bits, r@DontCare) =>
      MonoConnect.connect(l, r, Builder.forcedUserModule, concise)
    case _ => Builder.error(":= only use between Bits")
  }
  final def <-> (that: Data): Unit = <>(that, concise = true)
  final def <> (that: Data, concise: Boolean = false): Unit = {
    (this.binding, that.binding) match {
      case (_: ReadOnlyBinding, _: ReadOnlyBinding) => throwException(s"Both $this and $that are read-only")
      case _ =>
    }
    val cur_module = Builder.forcedUserModule
    if (cur_module.whenScopeBegin) {
      require(!cur_module.currentInWhenScope.contains(this), s"Can't connect $this twice")
      binding match {
        case PortBinding(_) =>
          cur_module._port_as_reg += this
        case WireBinding(_) =>
          // TODO
          // cur_module.addWireAsReg(this)
        case _ =>
      }
    } else if (cur_module.switch_id.nonEmpty) {
      binding match {
        case RegBinding(_) =>
        case PortBinding(_) => cur_module._port_as_reg += this
        // TODO
        // case WireBinding(_) => cur_module.addWireAsReg(this)
        case _ =>
      }
    }

    try {
      BiConnect.connect(this, that, Builder.forcedUserModule, concise)
    } catch {
      case BiConnectException(message) =>
        throwException(
          s"Connection between left ($this) and source ($that) failed @$message"
        )
    }
  }

  def asBits: Bits = this match {
    case b: Bits => b
    case _ => error(s"$this can't be Bits")
  }

  def asAgg: Aggregate = this match {
	  case a: Aggregate => a
    case _ => error(s"$this can't be Aggregate")
  }

  def asVec: Vec = this match {
	  case v: Vec => v
    case _ => error(s"$this can't be Vec")
  }

  def asUInt: Bits
  def asUIntGroup(group_num: Int, prefix: String): Bits
}
