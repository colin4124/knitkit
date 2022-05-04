package object knitkit {
  import scala.language.implicitConversions

  import ir.Width
  import internal._

  /** Synonyms, moved from main package object - maintain scope. */
  type ValidIO = Valid
  val ValidIO = Valid
  val DecoupledIO = Decoupled
  val VecInit = Vec

  implicit class fromBigIntToLiteral(bigint: BigInt) {
    def B: Bits = bigint match {
      case bigint if bigint == 0 => Bool.Lit(false)
      case bigint if bigint == 1 => Bool.Lit(true)
      case bigint => Builder.error(s"Cannot convert $bigint to Bool, must be 0 or 1"); Bool.Lit(false)
    }
    def U: Bits = UInt.Lit(bigint, Width())
    def S: Bits = SInt.Lit(bigint, Width())
    def U(width: Width): Bits = UInt.Lit(bigint, width)
    def S(width: Width): Bits = SInt.Lit(bigint, width)

    def asUInt: Bits = UInt.Lit(bigint, Width())
    def asSInt: Bits = SInt.Lit(bigint, Width())
    def asUInt(width: Width): Bits = UInt.Lit(bigint, width)
    def asSInt(width: Width): Bits = SInt.Lit(bigint, width)
  }
  implicit class fromIntToLiteral(int: Int) extends fromBigIntToLiteral(int)
  implicit class fromLongToLiteral(long: Long) extends fromBigIntToLiteral(long)

  implicit class fromStringToLiteral(str: String) {
    def U: Bits = str.asUInt
    def U(width: Width): Bits = str.asUInt(width)

    def asUInt: Bits = {
      val bigInt = parse(str)
      UInt.Lit(bigInt, Width(bigInt.bitLength max 1))
    }
    def asUInt(width: Width): Bits = UInt.Lit(parse(str), width)

    protected def parse(n: String): BigInt = {
      val (base, num) = n.splitAt(1)
      val radix = base match {
        case "x" | "h" => 16
        case "d" => 10
        case "o" => 8
        case "b" => 2
        case _ => Builder.error(s"Invalid base $base");
      }
      BigInt(num.filterNot(_ == '_'), radix)
    }
  }

  implicit class fromBooleanToLiteral(boolean: Boolean) {
    def B: Bits = Bool.Lit(boolean)
    def asBool: Bits = Bool.Lit(boolean)
  }

  implicit class fromIntToWidth(int: Int) {
    def W: Width = Width(int)
  }
  type knitkitException = internal.knitkitException

  class BindingException(message: String) extends knitkitException(message)

  case class MixedDirectionBundleException(message: String) extends BindingException(message)
  case class RebindingException              (message: String) extends BindingException(message)
  case class ExpectedknitkitTypeException    (message: String) extends BindingException(message)
  case class ExpectedHardwareException       (message: String) extends BindingException(message)

  case class BiConnectException  (message: String) extends knitkitException(message)
  case class MonoConnectException(message: String) extends knitkitException(message)
}
