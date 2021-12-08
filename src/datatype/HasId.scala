package knitkit

import collection.mutable.ArrayBuffer

import internal._
import ir._

trait HasId {
  var _parent: Option[BaseModule] = Builder.currentModule
  _parent.foreach(_.addId(this))

  val _id: Long = Builder.idGen.next

  var _parentID: Option[HasId] = None

  var decl_name: String = ""

  val _prefix = ArrayBuffer[String]()
  val _suffix = ArrayBuffer[String]()

  def clearPrefix: this.type = {
    _prefix.clear()
    this
  }
  def clearSuffix: this.type = {
    _suffix.clear()
    this
  }

  def clearWithName(name: =>String): this.type = {
    clearPrefix
    clearSuffix
    suggested_name = Some(name)
    this
  }

  var suggested_name: Option[String] = None

  def suggestName(name: =>String, alter: Boolean = true): this.type = {
    if (suggested_name.isEmpty || alter) {
      suggested_name = Some(name)
    }
    this
  }
  def suggestedName: Option[String] = suggested_name

  var _ref: Option[Expression] = None
  def setRef(imm: Expression): Unit = {
    _ref = Some(imm)
  }
  def setRef(parent: Data, name: String): Unit = {
    setRef(SubField(Node(parent), name))
  }
  def getRef: Expression = _ref.get

  def computeName(defaultPrefix: Option[String], defaultSeed: String): String = {
    def buildPrefix(base: String, prefix: Seq[String]): String = {
      if (prefix.isEmpty) base else prefix.mkString("_") + "_" + base
    }
    def buildSuffix(base: String, suffix: Seq[String]): String = {
      if (suffix.isEmpty) base else base + suffix.map( "_" + _ ).reduce(_ + _)
    }

    val name = if (suggested_name.isDefined) {
      buildSuffix(buildPrefix(suggested_name.get, _prefix.toSeq.reverse), _suffix.toSeq.reverse)
    } else {
      val candidate_name = buildSuffix(buildPrefix("", _prefix.toSeq.reverse), _suffix.toSeq.reverse)
      if (candidate_name != "") {
        candidate_name.drop(1)
      } else {
        defaultPrefix match {
          case Some(p) => buildPrefix(defaultSeed, List(p))
          case None => defaultSeed
        }
      }
    }
    if (_parentID.isDefined) {
      val parent_name = _parentID.get.computeName(None, "")
      (parent_name, name) match {
        case ("", "") => ""
        case ( p, "") => p
        case ("",  n) => n
        case ( p,  n) => s"${p}_${n}"
      }
    } else {
      name
    }
  }

  def forceName(prefix: Option[String], default: =>String, namespace: Namespace, rename: Boolean = true): Unit = {
    if(_ref.isEmpty) {
      val candidate_name = computeName(prefix, default)
      val available_name = namespace.name(candidate_name, rename)
      val tpe = this match {
        case b: Bits => b.tpe
        case _ => UnknownType
      }
      //val n = this match {
      //  case b: Bits => b.tpe match {
      //    case SIntType(_) => "$signed(" + available_name + ")"
      //    case _ => available_name
      //  }
      //  case _ => available_name
      //}
      //setRef(Reference(n, tpe))
      setRef(Reference(available_name, tpe))
    }
  }
}
