package knitkit

import Utils._
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

  def setArrDirection(a: Arr, dir: SpecifiedDirection): Unit = {
    if (a.elements.isEmpty) {
      a.direction = dir
    } else {
      a.direction = dir
      a.elements foreach { case (_, ele) => setArrDirection(ele, dir) }
    }
  }

  def specifiedDirection[T<:Data](source: T, dir: SpecifiedDirection): Unit = source match {
    case v: Vec =>
      v.getElements foreach { x => specifiedDirection(x, dir) }
    case a: Aggregate =>
      a.getElements foreach { x => specifiedDirection(x, dir) }
    case arr: Arr =>
      setArrDirection(arr, dir)
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
  final def := (that: WhenCase): Unit = {
    that.genWhenCase(this)
  }
  final def := (that: Data, concise: Boolean = false): Unit = (this, that) match {
    case (l: Arr , r: Arr ) =>
      if (is_port_io(l) && is_port_io(r)) {
        l.connect(r, concise)
      } else {
        l.arr_connect(r, concise)
      }
    case (l: Arr , r: Bits) =>
      if (l.elements.isEmpty) {
        l.connect(r, concise)
      } else {
        l.elements foreach { case (_, ele) =>
          ele.connect(r, concise)
        }
      }
    case (l: Bits , r: Arr) =>
      require(r.elements.isEmpty)
      l.connect(r, concise)
    case (l: Vec, r: Arr ) =>
      r.vec_connect(l, concise)
    case (l: Arr, r: Vec ) =>
      if (l.elements.isEmpty) {
        l.connect(r(0).asBits, concise)
      } else {
        l.elements foreach { case (name, ele) =>
          val idx = name.split("_").toList map { _.toInt }
          ele.connect(r.get_ele(idx: _*).asBits, concise)
        }
      }
    case (l: Bits, r: Bits) => l.connect(r, concise)
    case (l: Aggregate, r: Bits) => l.getElements foreach { _.:=(r, concise) }
    case (l: Vec, r: Bits) => l.getElements foreach { _.:=(r, concise) }
    case (l: Bits, r@DontCare) =>
      MonoConnect.connect(l, r, Builder.forcedUserModule, concise)
    case other => Builder.error(s":= only use between Bits, not $other")
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


  /**
    * If this is a literal that is representable as bits, returns the value as a BigInt.
    * If not a literal, or not representable as bits (for example, is or contains Analog), returns None.
    */
  def litOption: Option[BigInt]

  def isLit: Boolean = litOption.isDefined

  def litValue: BigInt = litOption.get

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

  def asArr: Arr = this match {
	  case a: Arr => a
    case _ => error(s"$this can't be Arr")
  }

  def asUInt: Bits
  def asUIntGroup(group_num: Int, prefix: String): Bits
}

/** Creates a clone of the super-type of the input elements. Super-type is defined as:
  * - for Bits type of the same class: the cloned type of the largest width
  * - Bools are treated as UInts
  * - For other types of the same class are are the same: clone of any of the elements
  * - Otherwise: fail
  */
object cloneSupertype {
  def apply[T <: Data](
    elts:        Seq[T],
    createdType: String
  ): T = {
    require(!elts.isEmpty, s"can't create $createdType with no inputs")

    val filteredElts = elts.filter(_ != DontCare)
    require(!filteredElts.isEmpty, s"can't create $createdType with only DontCare inputs")

    if (filteredElts.head.isInstanceOf[Bits]) {
      val model: T = filteredElts.reduce { (elt1: T, elt2: T) =>
        ((elt1, elt2) match {
          case (elt1: Bits, elt2: Bits) =>
            // TODO: perhaps redefine Widths to allow >= op?
            val e = if (elt1.width == (elt1.width.max(elt2.width))) elt1 else elt2
            e.clone()
          case (elt1, elt2) =>
            Builder.error(
              s"can't create $createdType with heterogeneous types ${elt1.getClass} and ${elt2.getClass}"
            )
        }).asInstanceOf[T]
      }
      model
    } else {
      Builder.error(s"TODO")
    }
  }
}
