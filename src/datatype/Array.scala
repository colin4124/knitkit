package knitkit

import collection.mutable.HashMap

import ir._
import Utils._
import knitkit.internal._
import scala.collection.mutable.ArrayBuffer

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

  def is_root = root == this
  def is_leaf = dimension.isEmpty

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


  def elements: Seq[Arr] = (ele_cache flatMap { case (_, arr) =>
    if (arr.ele_cache.values.isEmpty) {
      Seq(arr)
    } else {
      arr.elements
    }
  }).toSeq

  val ele_cache: HashMap[Int, Arr] = HashMap()

  var idx_stack: Seq[Int] = Seq()

  override def litOption: Option[BigInt] = element.litOption

  override def apply(idx: Int*): Arr = {
    if (idx.size == 0) {
      this
    } else {
      val name = idx.head
      if (ele_cache.contains(name)) {
        ele_cache(name)(idx.tail: _*)
      } else {
        val ele = gen_ele(idx_stack ++ Seq(name))
        ele_cache += (name -> ele)
        ele(idx.tail: _*)
      }
    }
  }

  def add_ele(idx: Int*): Unit = {
    val name = idx.head
    val ele = gen_ele(idx_stack ++ Seq(name))
    ele_cache += (name -> ele)
  }

  def arr_connect(arr: Arr, concise: Boolean): Unit = {
    require(dimension == arr.dimension, s"$dimension =/= ${arr.dimension}")
    if (dimension.isEmpty) {
      this.connect(arr, concise)
    } else {
      val names = gen_idx_name(dimension.toList, Seq())
      names foreach { name =>
        val idx = name.split("_").toList map { _.toInt }
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

  def gen_ele(idx_stack: Seq[Int]): Arr = {
    val ele = new Arr(element, dimension.tail: _*)
    ele.idx_stack = idx_stack
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
    ele.setRef(NodeArray(ele.root, ele.idx_stack))
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
    ele.elements foreach { e => e.root = set_parent }
    ele
  }

  def init_elements(): Unit = {
    if (!is_leaf) {
      require(_root.isDefined)
      val names = 0 until dimension.head
      names foreach { name =>
        val idx = Seq(name)
        add_ele(idx: _*)
      }
    }
  }

  override def bind(target: Binding): Unit = {
    target match {
	    case RegBinding(_) =>
        binding = target
        Builder.forcedUserModule.copyRegInfo(root, this)
        ele_cache.values foreach { e =>
          e.bind(target)
        }
      case _ =>
        binding = target
        ele_cache.values foreach { e =>
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
