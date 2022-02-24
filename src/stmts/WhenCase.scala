package knitkit

object WhenCase {
  def apply(dest: Data, mapping: Seq[(Data, Data)]): Unit = {
    apply(dest, None, mapping)
  }

  def apply(dest: Data, default: Data, mapping: Seq[(Data, Data)]): Unit = {
    apply(dest, Some(default), mapping)
  }

  def apply(dest: Data, default: Option[Data], mapping: Seq[(Data, Data)]): Unit = {
    val init = mapping(0)
    val tail = mapping.tail

    val init_when = when (init._1.asBits) { dest := init._2 }
    tail foreach { case (cond, res) => init_when.elsewhen(cond.asBits) { dest := res } }

    default match {
      case Some(d) =>
        init_when otherwise { dest := d }
      case None =>
    }
  }
}

