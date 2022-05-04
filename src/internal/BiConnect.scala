package knitkit.internal

import knitkit._
import knitkit.ir._
import knitkit.Utils._

object BiConnect {
  def BothDriversException =
    BiConnectException(": Both Left and Right are drivers")
  def NeitherDriverException =
    BiConnectException(": Neither Left nor Right is a driver")
  def UnknownDriverException =
    BiConnectException(": Locally unclear whether Left or Right (both internal)")
  def UnknownRelationException =
    BiConnectException(": Left or Right unavailable to current module.")
  def MissingLeftFieldException(field: String) =
    BiConnectException(s".$field: Left Record missing field ($field).")
  def MissingRightFieldException(field: String) =
    BiConnectException(s": Right Record missing field ($field).")
  def MismatchedException(left: String, right: String) =
    BiConnectException(s": Left ($left) and Right ($right) have different types.")

  def connect(left: Data, right: Data, context_mod: RawModule, concise: Boolean): Unit = {
    (left, right) match {
      case (left_r: Arr , right_r: Arr ) =>
        require(left_r.dimension == right_r.dimension, s"${left_r.dimension} =/= ${right_r.dimension}")

        left_r.setConn(right_r)
        right_r.setConn(left_r)

        val names = gen_idx_name(left_r.dimension.toList, Seq())
        names foreach { name =>
          val idx = name.split("_").toList map { _.toInt }
          elemConnect(left_r(idx: _*), right_r(idx: _*), context_mod, concise)
        }
      case (left_r: Vec, right_r: Arr) =>
        if (right_r.elements.isEmpty) {
          elemConnect(left_r(0).asBits, right_r, context_mod, concise)
        } else {
          val names = gen_idx_name(right_r.dimension.toList, Seq())
          names foreach { name =>
            val idx = name.split("_").toList map { _.toInt }
            elemConnect(left_r.get_ele(idx: _*).asBits, right_r(idx: _*), context_mod, concise)
          }
        }
      case (left_r: Arr, right_r: Vec ) =>
        if (left_r.elements.isEmpty) {
          elemConnect(left_r, right_r(0).asBits, context_mod, concise)
        } else {
          left_r.elements foreach { case (name, ele) =>
            val idx = name.split("_").toList map { _.toInt }
            elemConnect(ele, right_r.get_ele(idx: _*).asBits, context_mod, concise)
          }
        }
      case (left_e: Bits, right_e: Bits) => {
        elemConnect(left_e, right_e, context_mod, concise)
      }
      case (left_r: Bundle, right_r: Bits) =>
        for((_, left_sub) <- left_r.elements) {
          connect(left_sub, right_r, context_mod, concise)
        }
      case (left_r: Vec, right_r: Bits) =>
        for(left_sub <- left_r.elements) {
          connect(left_sub, right_r, context_mod, concise)
        }
      case (left_r: Bundle, right_r: Bundle) =>
        aggConnect(left_r, right_r, context_mod, concise)
      case (left_r: Vec, right_r: Vec) =>
        vecConnect(left_r, right_r, context_mod, concise)
      case (left, right) => throw MismatchedException(left.toString, right.toString)
    }
  }

  def vecConnect(left_r: Vec, right_r: Vec, context_mod: RawModule, concise: Boolean): Unit = {
    (left_r.getElements zip right_r.getElements) foreach { case (l, r) =>
      connect(l, r, context_mod, concise)
    }
  }

  def vecDontCareConnect(left_r: Vec, right_r: Bits, context_mod: RawModule, concise: Boolean): Unit = {
    require(right_r.tpe == DontCareType, "Bundle Only can connect to DontCare")
    left_r.getElements foreach { l =>
      connect(l, right_r, context_mod, concise)
    }
  }

  def aggConnect(left_r: Bundle, right_r: Bundle, context_mod: RawModule, concise: Boolean): Unit = {
    for((field, right_sub) <- right_r.elements) {
      if(!left_r.elements.isDefinedAt(field)) {
        throw MissingLeftFieldException(field)
      }
    }
    for((field, left_sub) <- left_r.elements) {
      try {
        right_r.elements.get(field) match {
          case Some(right_sub) => connect(left_sub, right_sub, context_mod, concise)
          case None => {
            throw MissingRightFieldException(field)
          }
        }
      } catch {
        case BiConnectException(message) => throw BiConnectException(s".$field$message")
      }
    }
  }

  private def issueConnectL2R(left: Bits, right: Bits, context_mod: RawModule, concise: Boolean): Unit = {
    right.connect(left, concise)
  }
  private def issueConnectR2L(left: Bits, right: Bits, context_mod: RawModule, concise: Boolean): Unit = {
    left.connect(right, concise)
  }

  def elemConnect(left: Bits, right: Bits, context_mod: RawModule, concise: Boolean): Unit = {
    import SpecifiedDirection.{Internal, Input, Output, InOut} // Using extensively so import these
    val left_mod: BaseModule  = left.binding.location.getOrElse(context_mod)
    val right_mod: BaseModule = right.binding.location.getOrElse(context_mod)

    val left_direction  = left.direction
    val right_direction = right.direction

    // CASE: Context is same module that both left node and right node are in
    if( (context_mod == left_mod) && (context_mod == right_mod) ) {
      ((left_direction, right_direction): @unchecked) match {
        //    SINK          SOURCE
        //    CURRENT MOD   CURRENT MOD
        case (Input   , Output  ) => issueConnectL2R(left, right, context_mod, concise)
        case (Input   , Internal) => issueConnectL2R(left, right, context_mod, concise)
        case (Internal, Output  ) => issueConnectL2R(left, right, context_mod, concise)

        case (Output  , Input   ) => issueConnectR2L(left, right, context_mod, concise)
        case (Output  , Internal) => issueConnectR2L(left, right, context_mod, concise)
        case (Internal, Input   ) => issueConnectR2L(left, right, context_mod, concise)

        case (Input   , Input   ) => throw BothDriversException
        case (Output  , Output  ) => throw BothDriversException
        case (Internal, Internal) =>
          issueConnectR2L(left, right, context_mod, concise)
          // throw UnknownDriverException
      }
    }

    // CASE: Context is same module as sink node and right node is in a child module
    else if( (left_mod == context_mod) && (right_mod != context_mod) ) {
      // Thus, right node better be a port node and thus have a direction
      ((left_direction, right_direction): @unchecked) match {
        //    SINK        SOURCE
        //    CURRENT MOD CHILD MOD
        case (Input,        Input)  => issueConnectL2R(left, right, context_mod, concise)
        case (Internal,     Input)  => issueConnectL2R(left, right, context_mod, concise)

        case (InOut,         InOut) => issueConnectR2L(left, right, context_mod, concise)
        case (Output,       Output) => issueConnectR2L(left, right, context_mod, concise)
        case (Internal,     Output) => issueConnectR2L(left, right, context_mod, concise)

        case (Input,        Output) => throw BothDriversException
        case (Output,       Input)  => throw NeitherDriverException
        case (_,            Internal) => throw UnknownRelationException
      }
    }

    // CASE: Context is same module as source node and sink node is in child module
    else if( (right_mod == context_mod) && (left_mod != context_mod) ) {
      // Thus, left node better be a port node and thus have a direction
      ((left_direction, right_direction): @unchecked) match {
        //    SINK          SOURCE
        //    CHILD MOD     CURRENT MOD
        case (InOut   , InOut   ) => issueConnectR2L(left, right, context_mod, concise)
        case (Input   , Input   ) => issueConnectR2L(left, right, context_mod, concise)
        case (Input   , Internal) => issueConnectR2L(left, right, context_mod, concise)
        case (Output  , Output  ) => issueConnectL2R(left, right, context_mod, concise)
        case (Output  , Internal) => issueConnectL2R(left, right, context_mod, concise)
        case (Input   , Output  ) => throw NeitherDriverException
        case (Output  , Input   ) => throw BothDriversException
        case (Internal, _       ) => throw UnknownRelationException
      }
    }

    // CASE: Context is the parent module of both the module containing sink node
    //                                        and the module containing source node
    //   Note: This includes case when sink and source in same module but in parent
    else {
      ((left_direction, right_direction): @unchecked) match {
        //    SINK      SOURCE
        //    CHILD MOD CHILD MOD
        case (Input,     Output  ) => issueConnectR2L(left, right, context_mod, concise)
        case (Output,    Input   ) => issueConnectL2R(left, right, context_mod, concise)

        case (Input,     Input   ) => throw NeitherDriverException
        case (Output,    Output  ) => throw BothDriversException
        case (_,         Internal) => throw UnknownRelationException
        case (Internal,  _       ) => throw UnknownRelationException
      }
    }
  }
}
