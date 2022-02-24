package knitkit

object WhenCase {
  def apply(dest: Bits, mapping: Seq[(Bits, Bits)]): Unit = {
    apply(dest, None, mapping)
  }

  def apply(dest: Bits, default: Bits, mapping: Seq[(Bits, Bits)]): Unit = {
    apply(dest, Some(default), mapping)
  }

  def apply(dest: Bits, default: Option[Bits], mapping: Seq[(Bits, Bits)]): Unit = {
    val init = mapping(0)
    val tail = mapping.tail

    val init_when = when (init._1) { dest := init._2 }
    tail foreach { case (cond, res) => init_when.elsewhen(cond) { dest := res } }

    default match {
      case Some(d) =>
        init_when otherwise { dest := d }
      case None =>
    }
  }
}

