package csexp.tokenize

import java.io.EOFException
import java.io.InputStream

/**
 * Positioned input-stream with push-back capability.
 */
private[tokenize] class PositionedInputStream(private val underlying: InputStream) {

  /**
   * Current position.
   */
  def position: Int = _position

  /**
   * Current position.
   */
  private[this] var _position: Int = 0

  /**
   * Read the next byte. Returns None if EOF has been reached.
   */
  def nextByte(): Option[Char] = {
    val ch = underlying.read()
    if (ch == -1) {
      None
    } else {
      _position += 1
      Some(ch.toChar)
    }
  }

  /**
   * Read a number of bytes into a buffer, throwing an EOFException
   * if the buffer cannot be filled before EOF is reached.
   */
  def nextBytes(n: Int): Array[Byte] = {
    val buf = new Array[Byte](n)
    var count = 0
    while (count < n) {
      // Read a bit more
      val readCount = underlying.read(buf, count, n - count)
      if (readCount < 0) {
        throw new EOFException()
      }
      // Move along
      count += readCount
      _position += readCount
    }
    buf
  }

}
