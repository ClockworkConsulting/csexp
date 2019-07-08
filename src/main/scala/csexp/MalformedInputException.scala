package csexp

/**
 * Input for parsing was malformed. The position is the byte
 * position of the error.
 */
class MalformedInputException(
    position: Int,
    message: String)
  extends RuntimeException(s"$message: at position $position")
