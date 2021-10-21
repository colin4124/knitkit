package knitkit.internal

import knitkit._

class Namespace(keywords: Set[String]) {
  private val names = collection.mutable.HashMap[String, Long]()
  for (keyword <- keywords)
    names(keyword) = 1

  def contains(elem: String): Boolean = names.contains(elem)

  def name(elem: String, is_rename: Boolean = true): String = {
    if (this contains elem) {
      if (is_rename) {
        name(rename(elem))
      } else {
        elem
      }
    } else {
      names(elem) = 1
      elem
    }
  }

  private def rename(n: String): String = {
    val index = names(n)
    val tryName = s"${n}_${index}"
    names(n) = index + 1
    if (this contains tryName) rename(n) else tryName
  }
}

object Namespace {
  def empty: Namespace = new Namespace(Set.empty[String])
}
