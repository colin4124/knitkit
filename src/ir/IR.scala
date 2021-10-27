package knitkit.ir

import knitkit._

case class Circuit(name: String, modules: Seq[Component])

case class Port(
  data : Bits,
  dir  : Direction,
) {
  def tpe: Type = data.tpe
}

abstract class Component {
  val name  : String
  val ports : Seq[Port]
}

case class DefModule(name: String, ports: Seq[Port], stmts: Seq[Statement]) extends Component
case class DefBlackBox(name: String, ports: Seq[Port], params: Map[String, Param]) extends Component

/** Primitive Operation
  */
abstract class PrimOp

abstract class Expression

trait HasType { this: Expression =>
  val tpe: Type
}

case class InstanceIO(inst: Instance, name: String) extends Expression

case class PairInstIO(l_inst_io: InstanceIO, r_inst_io: InstanceIO, concise: Boolean = false) extends Expression

case class Reference(serialize: String, tpe: Type = UnknownType) extends Expression with HasType

case class SubField(expr: Expression, name: String) extends Expression

case class Node(id: HasId) extends Expression

case class Mux(cond: Expression, tval: Expression, fval: Expression) extends Expression

case class DoPrim(op: PrimOp, args: Seq[Expression], consts: Seq[BigInt], tpe: Type) extends Expression with HasType
case class ILit(n: BigInt) extends Expression
case class CatArgs(args: Expression*) extends Expression

abstract class Literal extends Expression {
  val value: BigInt
  var width: Width
  def minWidth: BigInt
  def getWidth: BigInt = width match {
    case IntWidth(w)  => w
    case UnknownWidth => minWidth
  }
}

case class UIntLiteral(value: BigInt, specifiedWidth: Width) extends Literal {
  def minWidth: BigInt = 1 max value.bitLength
  def tpe = width match {
    case w: IntWidth => UIntType(w)
    case UnknownWidth => UIntType(IntWidth(minWidth))
  }

  var width: Width = specifiedWidth
}
object UIntLiteral {
  def minWidth(value: BigInt): Width = IntWidth(math.max(value.bitLength, 1))
  def apply(value: BigInt): UIntLiteral = new UIntLiteral(value, minWidth(value))
}
case class SIntLiteral(value: BigInt, w: Width) extends Literal {
  def tpe = SIntType(width)
  def minWidth: BigInt = 1 + value.bitLength
  var width: Width = w
}
object SIntLiteral {
  def minWidth(value: BigInt): Width = IntWidth(value.bitLength + 1)
  def apply(value: BigInt): SIntLiteral = new SIntLiteral(value, minWidth(value))
}

case class SwitchCondition(idx: Int, lit: Option[Literal], id: Option[Bits]) extends Expression {
  def tpe = UnknownType
}


/** Statement
  */
abstract class Statement

case class DefWire(e: Expression, reg: Boolean = false) extends Statement

case class DefInstance(inst: Instance, module: String, params: Map[String, Param]) extends Statement
//case class DefInstance(inst: Instance, module: String, params: Seq[Param]) extends Statement
case class Assign(loc: Expression, expr: Expression) extends Statement
case class Connect(loc: Expression, expr: Expression) extends Statement

case class Always(info: ClkInfo, stmts: Seq[Statement]) extends Statement

case class WhenScope(e: Expression, stmts: Seq[Statement]) extends Statement
case class WhenBegin(pred: Expression, isFirstWhen: Boolean) extends Statement
case class WhenEnd() extends Statement
case class OtherwiseBegin() extends Statement
case class OtherwiseEnd() extends Statement
case class WhenConnect(loc: Expression, expr: Expression) extends Statement

case class SwitchScope (expr: Expression, stmts: Seq[(SwitchCondition, Seq[Statement])]) extends Statement

/** Width
  */
abstract class Width {
  val value: BigInt
  def known: Boolean

  def op(that: Width, f: (BigInt, BigInt) => BigInt) = {
    IntWidth(f(this.value, that.value))
  }

  def max(that: Width ) = op(that, _ max _)
  def +  (that: Width ) = op(that, _  +  _)
  def +  (that: BigInt) = op(this, (a, b) => a + that)
  def shiftRight(that: BigInt) = IntWidth(BigInt(0) max (value - that))
  def dynamicShiftLeft(that: Width) =
    this.op(that, (a, b) => a + (1 << b.toInt) - 1)
}

object IntWidth {
  private val maxCached = 1024
  private val cache = new Array[IntWidth](maxCached + 1)
  def apply(width: BigInt): IntWidth = {
    if (0 <= width && width <= maxCached) {
      val i = width.toInt
      var w = cache(i)
      if (w eq null) {
        w = new IntWidth(width)
        cache(i) = w
      }
      w
    } else new IntWidth(width)
  }
  def unapply(w: IntWidth): Option[BigInt] = Some(w.value)
}
class IntWidth(val value: BigInt) extends Width {
  def known: Boolean = true
}
object UnknownWidth extends Width {
  val value: BigInt = 0
  def known: Boolean = false
}

object Width {
  def apply(x: BigInt) = IntWidth(x)
  def apply() = UnknownWidth
}

/** Type
  */
abstract class Type {
  var width: Width
}


case object UnknownType extends Type {
  var width: Width = UnknownWidth
}

case class UIntType(var width: Width) extends Type
case class SIntType(var width: Width) extends Type
case class AnalogType(var width: Width) extends Type

case object ClockType extends Type {
  var width: Width = IntWidth(1)
}

case object ClockNegType extends Type {
  var width: Width = IntWidth(1)
}

trait ResetType extends Type {
  var width: Width = IntWidth(1)
}

case object SyncResetType     extends ResetType
case object AsyncNegResetType extends ResetType
case object AsyncPosResetType extends ResetType

/** Direction
  */
sealed abstract class Direction

case object Input  extends Direction
case object Output extends Direction
case object InOut  extends Direction
