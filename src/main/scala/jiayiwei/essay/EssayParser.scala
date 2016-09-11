package jiayiwei.essay

import fastparse.core.Parsed.{Failure, Success}
import jiayiwei.essay.WordWithRange.Root

case class TextRange(start: Int, until: Int){
  override def toString = s"[$start, $until]"

  def isNearTo(pos: Int) = start <= pos && pos <= until
}

object WordWithRange{
  type Root = String
}

case class WordWithRange(original: String, root: Root, range: TextRange){
  override def toString = s"$range($original->$root)"
}
case class SentencePart(words: Seq[WordWithRange], range: TextRange){
  override def toString = s"$range{${words.mkString(" ")}}"
}

class EssayParser(stemmer: Stemmer) {
  import fastparse.all._

  val capitalLetter = CharIn('A' to 'Z')
  val space = CharIn(" \n\t")

  val sentencePartDivider = space.rep ~ (CharIn(",;:!?()\"") ~ space.rep | CharIn(".-") ~ space.rep ~ &(capitalLetter|End))

  val wordStopper = sentencePartDivider | space

  val wordParser = P(Index ~ !wordStopper ~ (CharPred(_ != ' ') ~ !(wordStopper|End)).rep.! ~ AnyChar.! ~ Index).map{
    case (start,l,r, end) =>
      val original = l+r.toString
      WordWithRange(original, stemmer.stem(original), TextRange(start, end))
  }

  val sentencePartParser = P(Index ~ wordParser.rep(sep=space.rep(min=1), min = 1) ~ Index).map{
    case (start, words, end) => SentencePart(words, TextRange(start, end))
  }


  val essayParser = wordStopper.rep ~ sentencePartParser.rep(sep=sentencePartDivider.rep(min=1)) ~ wordStopper.rep ~ End

  def parseText(text: String) = {
    essayParser.parse(text) match{
      case Success(parts, _) =>
        parts
      case f:Failure =>
        println("Fail to parse: ")
        println(text)
        throw new Exception(f.toString)
    }
  }
}

object EssayParser {
  lazy val standard = new EssayParser(SimpleStemmer.standard)

  val sampleText =
    """
      |Horse and horses!
      |First, 1.5 years ago, some super-man was there. I believe the increasingly use of new innovations in means of transport will replace the utility of traditional ones, including the use of cars. Through the history of human, we've never stopped creating new methods of traveling. In ancient days, we tamed horses, invented boats and carriages. And about one hundred years ago, the first car was designed and put into use. So it's hard to believe that we will stop here and not invent some new means of transport. Those cool traveling machines you read or heard of from science fictions, like flying cars, personal mini airplanes or city transport belts, may well get popular in some near future. And when more people have adapted to those new ways of travel, they will never turn back again. Our ancestors were once used to riding on the backs of horses or sitting in carriages, but as soon as the appearance of modern cars and trains, the use of those old methods was quickly replaced. And you can even hardly see any horses in a city nowadays. Unexceptionally, this principle will apply to cars as well, so the people driving cars in that day may be as rare as those who rides a horse you see today.
    """.stripMargin

  def main(args: Array[String]) {
    standard.parseText("""H !,.a""".stripMargin).foreach(println)
  }

}
