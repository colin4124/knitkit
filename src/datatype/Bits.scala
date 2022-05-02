package knitkit

import internal._
import internal.Builder._
import ir._
import Utils._
import ir.PrimOps._
import scala.collection.mutable.ArrayBuffer

class Bits(specifiedType: Type) extends Data with BitsOps {
  def apply(name: String): Data = error(s"Bits Not Support string extract")

  def getPair: Seq[(String, Data)] = error(s"Bits Not Support get pair")
  def getElements: Seq[Data] = error(s"Bits Not Support get elements")
  def asUIntGroup(group_num: Int, prefix: String): Bits  = error(s"Bits Not Support asUIntGroup")

  def getDir: SpecifiedDirection = direction

  def apply(x: BigInt): Bits =  { // Bool
    if (x < 0) {
      error(s"Negative bit indices are illegal (got $x)")
    }
    requireIsHardware(this, "bits to be indexed")
    pushOp(Bool(), Bits, this.ref, ILit(x), ILit(x))
  }
  def apply(idx_seq: Int*): Bits = {
    idx_seq.size match {
      case 1 =>
        apply(BigInt(idx_seq(0))) // Bool
      case 2 =>
        val x = idx_seq(0)
        val y = idx_seq(1)
        if (x < y || y < 0) {
          error(s"Invalid bit range ($x,$y)")
        }
        val w = x - y + 1
        requireIsHardware(this, "bits to be sliced")

        val dest_type = tpe match {
          case _: UIntType   => UIntType  (Width(w))
          case _: SIntType   => SIntType  (Width(w))
          case _: AnalogType => AnalogType(Width(w))
        }
        val dest = new Bits(dest_type)
        pushOp(dest, Bits, this.ref, ILit(x), ILit(y))
    }
  }

  def apply(x: Bits, name: String = ""): Bits = { // Bool
    require(isUInt(x))
    val theBits = if (name == "") {
      WireInit(this >> x)
    } else {
      WireInit(this >> x).suggestName(name)
    }
    theBits(0)
  }

  def apply(x: BigInt, y: BigInt): Bits = // UInt
    apply(castToInt(x, "High index"), castToInt(y, "Low index"))

  def prefix(s: String): this.type = {
    _prefix += s
    this
  }

  def suffix(s: String): this.type = {
    _suffix += s
    this
  }

  def flip: this.type = {
    direction = SpecifiedDirection.flip(direction)
    this
  }

  var tpe = specifiedType
  var direction: SpecifiedDirection = SpecifiedDirection.Internal

  var width = tpe.width
  def getWidth: BigInt = width.value
  def setWidth(w: Width ) = tpe.width = w
  def setWidth(w: BigInt) = tpe.width = IntWidth(w)

  var _conn = ArrayBuffer[Bits]()
  def setConn(d: Bits): Unit = {
    if (!_conn.contains(d)) {
      _conn += d
    }
    // _ref  = Some(d)
  }

  def cloneType: this.type = new Bits(tpe).asInstanceOf[this.type]

  def clone(fn: (Data, Data) => Data = (x, y) => x): Data = {
    fn(this.cloneType, this) match {
      case b: Bits => b
      case _ => Builder.error("Bits clone should be Bits")
    }
  }

  def copy(cvt_type: CvtType): Bits = {
    val bits = new Bits(tpe)
    bits._prefix ++= _prefix
    bits._suffix ++= _suffix
    bits.direction = direction
    bits.decl_name = decl_name
    bits.bypass    = bypass
    bits.suggested_name = suggested_name
    bits.bind(binding)
    bits.setRef(Node(this, cvt_type))
    bits
  }

  override def bind(target: Binding): Unit = {
    binding = target
  }

  def connect(that: Bits, concise: Boolean): Unit = {
    requireIsHardware(this, "data to be connected")
    requireIsHardware(that, "data to be connected")
    val (lhs_width, rhs_width) = (this.width, that.width) match {
      case (IntWidth(l_w), IntWidth(r_w)) =>
        (l_w, r_w)
      case (IntWidth(l_w), UnknownWidth) =>
        that.setWidth(l_w)
        (l_w, l_w)
      case (UnknownWidth, IntWidth(r_w)) =>
        setWidth(r_w)
        (r_w, r_w)
      case _ => error(s"Can't infer width")
    }

    require(lhs_width >= rhs_width, s"LHS's width ${lhs_width} < RHS's width ${rhs_width}")
    that._ref match {
      case Some(r) => r match {
        case ref: Literal =>
          ref.width = IntWidth(lhs_width)
          that.setRef(ref)
        case _ =>
      }
      case None =>
    }

    this.binding match {
      case _: ReadOnlyBinding => throwException(s"Cannot reassign to read-only $this")
      case _ =>  // fine
    }
    try {
      MonoConnect.connect(this, that, Builder.forcedUserModule, concise)
    } catch {
      case MonoConnectException(message) =>
        throwException(
          s"Connection between sink ($this) and source ($that) failed @$message"
        )
    }
  }


  /*
   * Operations
   */
  def unop(dest: Bits, op: PrimOp): Bits = {
    requireIsHardware(this, "bits operated on")
    pushOp(dest, op, this.ref)
  }

  def binop(dest: Bits, op: PrimOp, other: BigInt): Bits = {
   requireIsHardware(this, "bits operated on")
   pushOp(dest, op, this.ref, ILit(other))
  }

  def binop(dest: Bits, op: PrimOp, other: Bits): Bits = {
    requireIsHardware(this, "bits operated on")
    requireIsHardware(other, "bits operated on")
    pushOp(dest, op, this.ref, other.ref)
  }

  def compop(op: PrimOp, other: Bits): Bits = {
    binop(Bool(), op, other)
  }

  def redop(op: PrimOp): Bits = {
    unop(Bool(), op)
  }

  def tail(n: BigInt): Bits = { // UInt
   val w = width match {
     case IntWidth(x) =>
       require(x >= n, s"Can't tail($n) for width $x < $n")
       Width(x - n)
     case UnknownWidth => Width()
   }
   binop(UInt(width = w), Tail, n)
  }

  /*
   * PrimOps
   */
  def same_type_binop (that: BigInt, width: Width, op: PrimOp): Bits = {
   tpe match {
     case UIntType(_) =>
       binop(UInt(width), op, that)
     case SIntType(_) =>
       binop(SInt(width), op, that)
   }
  }

  def same_type_binop (that: Bits, width: Width, op: PrimOp, type_check: Boolean = true): Bits = {
    if (type_check) {
      def tpe_err_str(b: Bits): String =
        s"type: ${this.tpe}"
      require(sameType(this, that),
              s"${tpe_err_str(this)} and ${tpe_err_str(that)} not the same!"
      )
    }
    tpe match {
      case UIntType(_) =>
        binop(UInt(width), op, that)
      case SIntType(_) =>
        binop(SInt(width), op, that)
    }
  }
  def same_type_compop (that: Bits, op: PrimOp): Bits = {
    require(sameType(this, that))
    compop(op , that)
  }


  def + (that: Bits): Bits =
    same_type_binop(that, (this.width max that.width), Add)
  def +& (that: Bits): Bits =
    same_type_binop(that, (this.width max that.width) + 1, Add)
  def - (that: Bits): Bits =
    same_type_binop(that, this.width max that.width, Sub)
  def & (that: Bits): Bits =
    same_type_binop(that, this.width max that.width, And)
  def | (that: Bits): Bits =
    same_type_binop(that, this.width max that.width, Or)
  def ^ (that: Bits): Bits =
    same_type_binop(that, this.width max that.width, Xor)

  def / (that: Bits): Bits =
    same_type_binop(that, this.width, Div)
  def % (that: Bits): Bits =
    same_type_binop(that, this.width, Rem)

  def * (that: Bits): Bits = {
    (tpe, that.tpe) match {
      case (UIntType(_), UIntType(_)) =>
        binop(UInt(this.width +   that.width), Mul, that)
      case (SIntType(_), SIntType(_)) =>
        binop(SInt(this.width + that.width), Mul, that)
      case (UIntType(_), SIntType(_)) =>
        that * this
      case (SIntType(_), UIntType(_)) =>
        val thatToSInt = that.zext()
        val result = binop(SInt(this.width + thatToSInt.width), Mul, thatToSInt)
        result.tail(1).asSInt
    }
  }

  def <   (that: Bits): Bits = same_type_compop(that, Lt )
  def >   (that: Bits): Bits = same_type_compop(that, Gt )
  def <=  (that: Bits): Bits = same_type_compop(that, Leq)
  def >=  (that: Bits): Bits = same_type_compop(that, Geq)
  def =/= (that: Bits): Bits = same_type_compop(that, Neq)
  def === (that: Bits): Bits = same_type_compop(that, Eq )

  def || (that: Bits): Bits = { // Bool Only
    require(width.value == 1)
    require(that.width.value == 1)
    this | that
  }
  def && (that: Bits): Bits = { // Bool Only
    require(width.value == 1)
    require(that.width.value == 1)
    this & that
  }

  def << (that: Int): Bits = {
    if (that < 0) throwException(s"Negative shift amounts are illegal (got $that)")
    else if (that == 0 ) this
    else same_type_binop(that, this.width + that, Shl)
  }
  def << (that: Bits) =
    same_type_binop(that, this.width.dynamicShiftLeft(that.width), Dshl, false)

  def >> (that: Int): Bits = {
    if (that < 0)
      throwException(s"Negative shift amounts are illegal (got $that)")
    if (that == 0) this
    else
      tpe match {
        case UIntType(_) =>
          binop(UInt(this.width.shiftRight(that)), Shr, that)
        case SIntType(_) =>
          binop(SInt(this.width.shiftRight(that)), Shr, that)
      }
  }
  def >> (that: Bits): Bits = {
    require(isUInt(that), s"$that must be UInt")
    val res_width = (this.width, that._ref) match {
      case (IntWidth(w), Some(l: Literal)) =>
        if (w < l.value) error(s"LHS'width: $l < RHS's shift amount: ${l.value}")
        else w - l.value
      case (IntWidth(w), _) => w
      case (_, _) => error(s"Not support UnknownWidth!")
    }
    tpe match {
      case UIntType(_) =>
        binop(UInt(Width(res_width)), Dshr, that)
      case SIntType(_) =>
        binop(SInt(Width(res_width)), Dshr, that)
    }
  }

  def orR : Bits = redop(Orr )
  def andR: Bits = redop(Andr)
  def xorR: Bits = redop(Xorr)

  def unary_! : Bits = this === 0.U(1.W)

  def unary_~ : Bits = {
    tpe match {
      case UIntType(_) =>
        unop(UInt(width), Not)
      case SIntType(_) =>
        unop(SInt(width), Not)
    }
  }

  def zext(): Bits = pushOp(SInt(width + 1), Cvt, ref)


  // As Type Converter
  def asUInt = {
    val cvt_type = tpe match {
      case _: SIntType => UnsignedType
      case _ => DontCvtType
    }
    val new_bits = copy(cvt_type)
    tpe match {
      case _: UIntType => new_bits
      case SIntType(w) =>
        _ref match {
          case Some(r) =>
            r match {
              case _: UIntLiteral =>
                error(s"SIntType shouldn't contain UIntLiteral")
              case s@SIntLiteral(v, _) =>
                val ref = UIntLiteral(v + (if (v < 0) BigInt(1) << s.getWidth.toInt else 0), IntWidth(s.getWidth))
                new_bits.setRef(ref)
              case _ =>
            }
          case None =>
        }
        new_bits.tpe = UIntType(w)
        new_bits
    }
  }

  def asSInt = {
    val cvt_type = tpe match {
      case _: UIntType => SignedType
      case _ => DontCvtType
    }
    val new_bits = copy(cvt_type)
    val res = tpe match {
      case _: SIntType => new_bits
      case UIntType(w) =>
        _ref match {
          case Some(r) =>
            getRef match {
              case u@UIntLiteral(v, _) =>
                val ref = SIntLiteral(v - ((v >> (u.getWidth.toInt - 1)) << u.getWidth.toInt), IntWidth(u.getWidth))
                new_bits.setRef(ref)
              case _: SIntLiteral =>
                error(s"UIntType shouldn't contain SIntLiteral")
              case _ =>
            }
          case None =>
        }
        new_bits.tpe = SIntType(w)
        new_bits
    }
    res
  }

  def cvt_1_bit_type (t: Type): Bits = {
    width match {
      case IntWidth(w) if w == 1 =>
        val new_bits = copy(DontCvtType)
        new_bits.tpe = t
        new_bits
      case _ => throwException(s"can't covert ${this.getClass.getSimpleName}$width to $t")
    }
  }
  def asBool  = cvt_1_bit_type(UIntType(width))
  def asClock = cvt_1_bit_type(ClockType      )
  def asClockNeg = cvt_1_bit_type(ClockNegType      )
  def asReset = cvt_1_bit_type(SyncResetType  )
  def asAsyncPosReset = cvt_1_bit_type(AsyncPosResetType)
  def asAsyncNegReset = cvt_1_bit_type(AsyncNegResetType)
}

object UInt {
  def apply(): Bits = {
    apply(Width())
  }
  def apply(name: String): Bits = {
    apply(Width()).suggestName(name)
  }

  def apply(width: Width): Bits = new Bits(UIntType(width))

  def apply(width: Width, name: String = ""): Bits = {
    if (name == "") new Bits(UIntType(width)) else (new Bits(UIntType(width))).suggestName(name)
  }

  def Lit(value: BigInt, width: Width): Bits = {
    val lit = UIntLiteral(value, width)
    val result = UInt(IntWidth(lit.getWidth))
    lit.bindLitArg(result)
  }
}

object Analog {
  def apply(): Bits = {
    apply(Width())
  }
  def apply(name: String): Bits = {
    apply(Width()).suggestName(name)
  }

  def apply(width: Width): Bits = knitkit.InOut(new Bits(AnalogType(width)))

  def apply(width: Width, name: String = ""): Bits = {
    knitkit.InOut(if (name == "") new Bits(AnalogType(width)) else (new Bits(AnalogType(width))).suggestName(name))
  }
}

object SInt {
  def apply(): Bits = apply(Width())
  def apply(name: String): Bits = {
    apply(Width()).suggestName(name)
  }

  def apply(width: Width): Bits = new Bits(SIntType(width))
  def apply(width: Width, name: String = ""): Bits = {
    if (name == "") new Bits(SIntType(width)) else (new Bits(SIntType(width))).suggestName(name)
  }

  def Lit(value: BigInt, width: Width): Bits = {
    val lit = SIntLiteral(value, width)
    val result = SInt(IntWidth(lit.getWidth))
    lit.bindLitArg(result)
  }
}

object Bool {
  def apply(): Bits = UInt(1.W)
  def apply(name: String): Bits = {
    apply().suggestName(name)
  }

  def Lit(x: Boolean): Bits = {
    val result = Bool()
    val lit = UIntLiteral(if (x) 1 else 0, IntWidth(1))
    lit.bindLitArg(result)
  }
}

object Clock {
  def apply(): Bits = new Bits(ClockType)
  def apply(name: String): Bits = {
    apply().suggestName(name)
  }
}

object ClockNeg {
  def apply(): Bits = new Bits(ClockNegType)
  def apply(name: String): Bits = {
    apply().suggestName(name)
  }
}

object Reset {
  def apply(): Bits = new Bits(SyncResetType)
  def apply(name: String): Bits = {
    apply().suggestName(name)
  }
}

object AsyncPosReset {
  def apply(): Bits = new Bits(AsyncPosResetType)
  def apply(name: String): Bits = {
    apply().suggestName(name)
  }
}

object AsyncNegReset {
  def apply(): Bits = new Bits(AsyncNegResetType)
  def apply(name: String): Bits = {
    apply().suggestName(name)
  }
}


object DontCare extends Bits(DontCareType) {
  binding = DontCareBinding
}

trait BitsOps { this: Bits =>
  def wrap_op(that: Data, op: Bits => Bits): Bits = that match {
    case b: Bits => op(b)
    case a: Aggregate => Builder.error(s"Bits Ops support Bits only, Not for Aggregate!")
    case v: Vec => Builder.error(s"Bits Ops support Bits only, Not for Vec!")
  }
  def +  (that: Data): Bits = wrap_op(that, + )
  def +& (that: Data): Bits = wrap_op(that, +&)
  def -  (that: Data): Bits = wrap_op(that, - )
  def &  (that: Data): Bits = wrap_op(that, & )
  def |  (that: Data): Bits = wrap_op(that, | )
  def ^  (that: Data): Bits = wrap_op(that, ^ )
  def *  (that: Data): Bits = wrap_op(that, * )
  def /  (that: Data): Bits = wrap_op(that, / )
  def %  (that: Data): Bits = wrap_op(that, % )

  def <   (that: Data): Bits = wrap_op(that, <  )
  def >   (that: Data): Bits = wrap_op(that, >  )
  def <=  (that: Data): Bits = wrap_op(that, <= )
  def >=  (that: Data): Bits = wrap_op(that, >= )
  def =/= (that: Data): Bits = wrap_op(that, =/=)
  def === (that: Data): Bits = wrap_op(that, ===)

  def || (that: Data): Bits = wrap_op(that, ||)
  def && (that: Data): Bits = wrap_op(that, &&)

  def << (that: Int ): Bits
  def << (that: Data): Bits = wrap_op(that, <<)

  def >> (that: Int ): Bits
  def >> (that: Data): Bits = wrap_op(that, >>)
}
