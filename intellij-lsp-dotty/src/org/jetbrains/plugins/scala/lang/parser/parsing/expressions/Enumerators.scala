package org.jetbrains.plugins.scala.lang.parser.parsing.expressions

import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes
import org.jetbrains.plugins.scala.lang.parser.ScalaElementTypes
import org.jetbrains.plugins.scala.lang.parser.parsing.builder.ScalaPsiBuilder
import org.jetbrains.plugins.scala.lang.parser.parsing.patterns.Guard

/**
* @author Alexander Podkhalyuzin
* Date: 06.03.2008
*/

/*
 * Enumerators ::= Generator {semi Enumerator}
 */
object Enumerators extends Enumerators {
  override protected def generator = Generator
  override protected def guard = Guard
  override protected def enumerator = Enumerator
}

trait Enumerators {
  protected def generator: Generator
  protected def enumerator: Enumerator
  protected def guard: Guard

  def parse(builder: ScalaPsiBuilder): Boolean = {
    val enumsMarker = builder.mark
    if (!generator.parse(builder)) {
      enumsMarker.drop()
      return false
    }
    var exit = true
    while (exit) {
      val guard = builder.getTokenType match {
        case ScalaTokenTypes.tSEMICOLON =>
          builder.advanceLexer()
          false
        case _ if builder.newlineBeforeCurrentToken => false
        case _ if this.guard.parse(builder) => true
        case _ => exit = false; true
      }
      if (!guard && !enumerator.parse(builder)) exit = false
    }
    enumsMarker.done(ScalaElementTypes.ENUMERATORS)
    return true
  }
}