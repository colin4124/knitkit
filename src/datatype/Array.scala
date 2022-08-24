package knitkit

import collection.mutable.HashMap

import ir._
import Utils._
import knitkit.internal._

// Array
class Arr(
  val element: Bits,
  val dimension: Int*,
) extends Bits(element.tpe) {
  override def cloneType: this.type = {
    val a = new Arr(element, dimension:_*).asInstanceOf[this.type]

    a.root = a
    a.init_elements()
    a
  }

  var _root: Option[Arr] = None

  def root: Arr = _root.get
  def root_=(target: Arr): Unit = {
    if (_root.isDefined) {
      if (_root.get != target) {
        throw RebindingException(s"Attempted reassignment of parent to ${this.computeName(None, "")}")
      }
    }
    _root = Some(target)
  }


  val elements : HashMap[String, Arr] = HashMap()
  val ele_cache: HashMap[String, Arr] = HashMap()

  override def apply(idx: Int*): Arr = {
    val name = if (idx.size == 1) s"${idx(0)}" else idx.mkString("_")
    if (ele_cache.contains(name)) {
      ele_cache(name)
    } else {
      val ele = gen_ele(idx)
      ele_cache += (name -> ele)
      ele
    }
  }

  def add_ele(idx: Int*): Unit = {
    val name = if (idx.size == 1) s"${idx(0)}" else idx.mkString("_")
    val ele = gen_ele(idx)
    ele_cache += (name -> ele)
    elements  += (name -> ele)
  }

  def arr_connect(arr: Arr, concise: Boolean): Unit = {
    require(dimension == arr.dimension, s"$dimension =/= ${arr.dimension}")
    if (dimension.isEmpty) {
      this.connect(arr, concise)
    } else {
      val names = gen_idx_name(dimension.toList, Seq())
      names foreach { name =>
        val idx = name.split("_").toList map { _.toInt }
        println(idx)
        apply(idx: _*).connect(arr.apply(idx: _*), concise)
      }
    }
  }

  def vec_connect(vec: Vec, concise: Boolean): Unit = {
    // require(dimension == arr.dimension, s"$dimension =/= ${arr.dimension}")
    val names = gen_idx_name(dimension.toList, Seq())
    names foreach { name =>
      val idx = name.split("_").toList map { _.toInt }
      vec.get_ele(idx: _*) := apply(idx: _*)
    }
  }

  def gen_ele(idx: Seq[Int]): Arr = {
    val drop_right_num = idx.size
    val ele = new Arr(element, dimension.drop(drop_right_num): _*)
    val set_parent = _root match {
      case Some(p) =>
        p
      case None =>
        this
    }
    val parent_binding = _root match {
      case Some(p) => p._binding
      case _ => _binding
    }

    ele.root = set_parent
    ele.setRef(NodeArray(this, idx))
    ele.direction = set_parent.direction

    parent_binding match {
	    case Some(WireBinding(_)) =>
        ele._binding = _binding
      case Some(RegBinding(_)) =>
        ele._binding = _binding
        Builder.forcedUserModule.copyRegInfo(root, ele)
      case ohter =>
        ele._binding = _binding
        // println(_binding)
        // Builder.error(s"TODO")
    }

    ele.init_elements()
    ele.elements foreach { case(_, e) => e.root = set_parent }
    ele
  }

  def init_elements(): Unit = {
    require(_root.isDefined)
    val names = gen_idx_name(dimension.toList, Seq())
    names foreach { name =>
      val idx = name.split("_").toList map { _.toInt }
      add_ele(idx: _*)
    }
  }

  override def bind(target: Binding): Unit = {
    target match {
	    case RegBinding(_) =>
        binding = target
        Builder.forcedUserModule.copyRegInfo(root, this)
        elements foreach { case (_, e) =>
          e.bind(target)
        }
      case _ =>
        binding = target
        elements foreach { case (_, e) =>
          e.bind(target)
        }
    }
  }
}

object Arr {
  def apply(b: Bits, dimension: Int*): Arr = {
    val a = new Arr(b, dimension:_*)
    a.root = a
    a.init_elements()
    a
  }
}

object ArrInit {
  def apply(b: Bits, dimension: Int*): Arr = {
    requireIsHardware(b, "reg initializer")

    val arr = new Arr(b, dimension:_*)
    arr._binding = b._binding
    arr.root = arr
    arr.init_elements()
    arr
  }
}
