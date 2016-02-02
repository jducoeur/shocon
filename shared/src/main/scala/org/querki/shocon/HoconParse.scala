package org.querki.shocon

import fastparse.all._
import fastparse.core.Result._

/**
 * Quick-and-dirty rudimentary parser for parts of the HOCON syntax.
 * 
 * @author jducoeur
 */
object HoconParse {
  
  // Mandatory and optional whitespace
  val wP = P(CharsWhile(_.isWhitespace))
  val wOptP = P(CharsWhile(_.isWhitespace, 0))
  
  // The separator between key-value pairs. Note that commas are optional in HOCON
  val kvSepP = P(wOptP ~ ("," ~ wOptP).?)
  
  // A single "name", following JS syntax
  val nameFirstP = P((CharPred(_.isLetter) | "_" | "$").!)
  val nameRestP = P((CharsWhile(c => c.isLetterOrDigit | c == '_' | c == '$')).!)
  // TODO: if the quotes are present, they must be balanced:
  val nameP = P("\"".? ~ (nameFirstP ~ nameRestP).! ~ "\"".?)
  
  // A path to a final element. This may be one or more levels deep:
  val pathP = P(nameP.rep(min = 1, sep = "." ~! Pass))
  
  // Triple-quoted strings can contain anything, including newlines and ordinary quotes:
  val tripleQuotedP:Parser[HCValue] = P("\"\"\"" ~ (!"\"\"\"" ~ AnyChar).rep.! ~ "\"\"\"") map (SimpleValue(_))
  
  // Conventional quoted strings.
  // TODO: interpret \r, \n, etc, correctly.
  // TODO: reject newlines and other control chars inside single-quoted strings.
  val quotedP:Parser[HCValue] = P("\"" ~ (!"\"" ~ AnyChar).rep.! ~ "\"") map (SimpleValue(_))
  
  // A sub-object
  val objP:Parser[HCValue] = P("{" ~ objectGutsP ~ "}")
  
  val vP = P(tripleQuotedP | quotedP | objP)
  
  // A single key-value pair, but it might be a nested value
  // TODO: according to the HOCON standard, you can omit the "=" in the case of object values
  val kv:Parser[ObjectValue] = P(pathP ~ wOptP ~ (":" | "=") ~ wOptP ~ vP) map { case (path, v) =>
    val inner = ObjectValue(Map(path.last -> v))
    if (path.length == 1)
      inner
    else
      path.dropRight(1).foldRight(inner) { (node, current) =>
        ObjectValue(Map(node -> current))
      }
  }
  
  // The actual content of an object -- a bunch of key-value pairs
  val objectGutsP:Parser[ObjectValue] = P(wOptP ~ kv.rep(sep=kvSepP) ~ wOptP) map { kvs =>
    val allvs = kvs.map(_.vs).reduce(_ ++ _)
    ObjectValue(allvs)
  }
  
  // The top level -- note that the braces are optional
  // TODO: if the braces are present, they should be balanced
  val topP = P("{".? ~ wOptP ~ objectGutsP ~ wOptP ~ "}".?)
  
  /**
   * The main parser. Note that, at the top level, it returns a single "object" value, for the
   * anonymous "root" object. Top-level keys come under that.
   */
  def apply(text:String):ObjectValue = {
    topP.parse(text) match {
      case Success(ov, _) => ov
      // TODO: add better failure reporting. Once we upgrade to FastParse 0.3.1, use the new fields in Failure.
      case Failure(parser, index) => {
        throw new Exception(s"Failed to parse in $parser at $index")
      }
    }
  }
}