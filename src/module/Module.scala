package knitkit

import scala.collection.mutable.{ArrayBuffer, HashMap}

import ir._
import internal._
import Utils._

abstract class BaseModule {
  if (!Builder.readyForModuleConstr) {
    throwException("Error: attempted to instantiate a Module without wrapping it in Module().")
  }
  Builder.readyForModuleConstr = false

  Builder.currentModule = Some(this)

  def apply() = {
    val inst = genInst()
    pushInst(inst)
    inst
  }

  def pushInst[T <: Instance](inst: T): Unit

  var _closed = false
  def isClosed = _closed

  val _namespace = Namespace.empty

  val _ids = ArrayBuffer[HasId]()
  def addId(d: HasId): Unit = {
    require(!_closed, "Can't write to module after module close")
    _ids += d
  }
  def getIds = {
    require(_closed, "Can't get ids before module close")
    _ids.toSeq
  }

  val _ports = new ArrayBuffer[Data]()

  def getModulePorts = {
    require(_closed, "Can't get ports before module close")
    _ports.toSeq
  }

  def getPortMap(): Map[String, Data] = {
    (getModulePorts map { p =>
       p.decl_name match {
         case "" =>
           p.suggestedName.get -> p
         case name =>
           name -> p
       }
    }).toMap
  }

  def genInst() = new Instance(getPortMap())

  def desiredName: String = this.getClass.getName.split('.').last

  final lazy val name = desiredName

  def genPortIR(port: Data): Seq[Port] = port match {
      case v: Vec =>
        v.getElements map { x => genPortIR(x) } reduce { _ ++ _ }
      case a: Aggregate =>
        a.getElements map { x => genPortIR(x) } reduce { _ ++ _ }
      case b: Bits =>
        val dir = b.direction match {
          case SpecifiedDirection.Output => ir.Output
          case SpecifiedDirection.Input  => ir.Input
          case SpecifiedDirection.InOut  => ir.InOut
          case _ => Builder.error(s"Port Dir Error: ${b.direction}")
        }
        Seq(Port(b, dir))
  }

  def namePorts(names: HashMap[HasId, String]): Unit = {
    for (port <- getModulePorts) {
      val pre_name = port.computeName(None, "")
      (if (pre_name == "") None else Some(pre_name)).orElse(names.get(port)) match {
        case Some(name) =>
          if (_namespace.contains(name)) {
            Builder.error(s"""Unable to name port $port to "$name" in ${this.name},""" +
              " name is already taken by another port!")
          }
          port match {
            case v: Vec =>
              v.setRef(Reference(_namespace.name(name)))
            case a: Aggregate =>
              a.setRef(Reference(_namespace.name(name)))
            case b: Bits =>
              b.setRef(Reference(_namespace.name(name), b.tpe))
          }
        case None => throwException(s"Unable to name port $port in ${this.name}, " +
                                      "try making it a public field of the Module")
      }
    }
  }

  def passThroughIO[T <: Data](d: T) = {
    require(!_closed, "Can't auto connect IO when module closed!")
    d.bypass = true
    d
  }

  def generateComponent(): Component

  def getPublicFields(rootClass: Class[_]): Seq[java.lang.reflect.Method] = {
    def is_final(modifier: Int) =
      (modifier & java.lang.reflect.Modifier.FINAL) == java.lang.reflect.Modifier.FINAL
    // Suggest names to nodes using runtime reflection
    def getValNames(c: Class[_]): Set[String] = {
      if (c == rootClass) {
        Set()
      } else {
        getValNames(c.getSuperclass) ++ c.getDeclaredFields.filter(x => is_final(x.getModifiers())).map(_.getName)
      }
    }
    val valNames = getValNames(this.getClass)
    def isPublicVal(m: java.lang.reflect.Method) =
      m.getParameterTypes.isEmpty && valNames.contains(m.getName) && !m.getDeclaringClass.isAssignableFrom(rootClass)

    this.getClass.getMethods.toIndexedSeq.sortWith(_.getName < _.getName).filter(isPublicVal(_))
  }

  def nameIds(rootClass: Class[_]): HashMap[HasId, String] = {
    val names = new HashMap[HasId, String]()
    def name(node: HasId, name: String): Unit = {
      if (!names.contains(node)) {
        names.put(node, name)
      }
    }
    for (m <- getPublicFields(rootClass)) {
      Builder.nameRecursively(m.getName, m.invoke(this), name)
    }
    names
  }

  def bindIoInPlace[T <: Data](iodef: T): Unit = {
    iodef match {
      case b: Bits      =>
        requireIsknitkitType(b, "io type")
        iodef.bind(PortBinding(this))
      case a: Aggregate =>
        a.getElements foreach { e => bindIoInPlace(e) }
        a.bind(PortBinding(this))
      case v: Vec =>
        v.getElements foreach { e => bindIoInPlace(e) }
        v.bind(PortBinding(this))
    }
  }

  def IO[T <: Data](iodef: T): T = {
    IO(iodef, None)
  }

  def IO[T <: Data](iodef: T, name: String): T = {
    IO(iodef, Some(name))
  }

  def IO[T <: Data](iodef: T, name: Option[String]): T = {
    require(!isClosed, "Can't add more ports after module close")

    bindIoInPlace(iodef)

    name match {
      case Some(n) =>
        iodef.suggestName(n)
      case None =>
    }

    _ports += iodef
    iodef
  }

  def cloneIO[T <: Data](src: T, name: String = "") = {
    val dest = src match {
      case v: Vec       => v.clone(clone_fn_base _)
      case a: Aggregate => a.clone(clone_fn_base _)
      case b: Bits      => b.clone(clone_fn_base _)
    }
    val orig_bypass = dest.bypass
    val io = IO(dest)
    io <> src
    if (name != "") {
      io.decl_name = name
      io.suggestName(name)
    }
    io
  }
}

object Module {
  def apply[T <: BaseModule](bc: => T): T = {
    if (Builder.readyForModuleConstr) {
      Builder.error("Error: Called Module() twice without instantiating a Module.")
    }
    Builder.readyForModuleConstr = true

    val parent = Builder.currentModule

    val (saveClock, saveReset)  = (Builder.currentClock, Builder.currentReset)
    Builder.currentClock = None
    Builder.currentReset = None

    val module: T = bc

    module match {
      case m: RawModule => m.autoConnectPassIO()
      case _ =>
    }

    if (Builder.readyForModuleConstr) {
      Builder.error("Error: attempted to instantiate a Module, but nothing happened. " +
                       "This is probably due to rewrapping a Module instance with Module().")
    }

    Builder.currentModule = parent
    Builder.currentClock = saveClock
    Builder.currentReset = saveReset

    val component = module.generateComponent()
    Builder.components += component

    module
  }
}
