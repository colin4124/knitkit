package knitkit

import org.apache.commons.text.StringEscapeUtils

import ir._
import internal._

/** Parameters for ExtModules */
sealed abstract class Param
case class IntParam(value: BigInt) extends Param
case class DoubleParam(value: Double) extends Param
case class StringParam(value: StringLit) extends Param
/** Unquoted String */
case class RawParam(value: String) extends Param

case class StringLit(string: String) {
  /** Returns an escaped and quoted String */
  def escape: String = {
    "\"" + serialize + "\""
  }
  def serialize: String = StringEscapeUtils.escapeJava(string)

  /** Returns an escaped and quoted String */
  def verilogEscape: String = {
    // normalize to turn things like ö into o
    import java.text.Normalizer
    val normalized = Normalizer.normalize(string, Normalizer.Form.NFD)
    val ascii = normalized flatMap StringLit.toASCII
    ascii.mkString("\"", "", "\"")
  }
}
object StringLit {
  /** Maps characters to ASCII for Verilog emission */
  private def toASCII(char: Char): List[Char] = char match {
    case nonASCII if !nonASCII.isValidByte => List('?')
    case '"' => List('\\', '"')
    case '\\' => List('\\', '\\')
    case c if c >= ' ' && c <= '~' => List(c)
    case '\n' => List('\\', 'n')
    case '\t' => List('\\', 't')
    case _ => List('?')
  }

  /** Create a StringLit from a raw parsed String */
  def unescape(raw: String): StringLit = {
    StringLit(StringEscapeUtils.unescapeJava(raw))
  }
}

abstract class ExtModule(params_raw: Map[String, Any] = Map.empty[String, Param]) extends BaseModule {
  def this(a: (String, Any), r: (String, Any)*) = this((a :: r.toList).toMap)

  val params = params_raw map { case (name, param) =>
    name -> (param match {
      case p: Int    => IntParam(BigInt(p))
      case p: Long   => IntParam(BigInt(p))
      case p: BigInt => IntParam(p)
      case p: Double => DoubleParam(p)
      case p: String => StringParam(StringLit(p))
      case p: Param  => p
    })
  }

  def pushInst[T <: Instance](inst: T): Unit = {
    require(_closed, "Can't push instance before module close")
    val cur_module = Builder.forcedUserModule
    cur_module._inst_stmts += DefInstance(inst, name, params)
  }

  def generateComponent(): Component = {
    require(!_closed, "Can't generate module more than once")
    _closed = true

    val names = nameIds(classOf[ExtModule])

    namePorts(names)

    for ((node, name) <- names) {
      if (node.decl_name == "") {
        node.decl_name = name
      }

      node.suggestName(name, alter = false)
    }

    for (id <- getIds) {
      id match {
        case agg: Aggregate =>
          agg.forceName(None, default="AGG", _namespace, rename = false)
          agg._onModuleClose
        case vec: Vec =>
          vec.forceName(None, default="VEC", _namespace, rename = false)
          vec._onModuleClose
        case _ =>
      }
    }

    val modulePorts = getModulePorts flatMap { p => genPortIR(p) }

    DefBlackBox(name, modulePorts, params)
  }
}
