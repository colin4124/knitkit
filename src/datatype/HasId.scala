package knitkit

import internal._
import ir._

trait HasId {
  var _parent: Option[BaseModule] = Builder.currentModule
  _parent.foreach(_.addId(this))

  val _id: Long = Builder.idGen.next

  var decl_name: String = ""

  var suggested_name: Option[String] = None

  def suggestName(name: =>String): this.type = {
    if(suggested_name.isEmpty) suggested_name = Some(name)
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

  def forceName(default: =>String, namespace: Namespace): Unit =
    if(_ref.isEmpty) {
      val candidate_name = suggested_name.getOrElse(default)
      val available_name = namespace.name(candidate_name)
      val tpe = this match {
        case b: Bits => b.tpe
        case _ => UnknownType
      }
      val n = this match {
        case b: Bits => b.tpe match {
          case SIntType(_) => "$signed(" + available_name + ")"
          case _ => available_name
        }
        case _ => available_name
      }
      setRef(Reference(n, tpe))
    }
}
