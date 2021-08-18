package knitkit.internal

class knitkitException(message: String, cause: Throwable = null) extends Exception(message, cause)

object throwException {
  def apply(s: String, t: Throwable = null): Nothing =
    throw new knitkitException(s, t)
}
