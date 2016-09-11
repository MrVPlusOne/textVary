package jiayiwei.essay

import jiayiwei.essay.EssayStat.PhraseUsage
import jiayiwei.essay.FSA.Phrase
import jiayiwei.essay.WordWithRange.Root

import scala.collection.mutable



class StatResult{
  val stat = new mutable.HashMap[Phrase, Vector[PhraseUsage]]()
  var wordCount = 0

  def addUsage(usage: PhraseUsage): Unit = {
    val phrase = EssayStat.getPhraseFromUsage(usage)
    val oldUse = stat.getOrElse(phrase, Vector())
    stat(phrase) = oldUse :+ usage
  }

  def template(title: String, data: Seq[(Phrase, Vector[PhraseUsage])]) = {
    s"\n----$title----\n" +
    data.sortBy(_._2.length).map{
      case (p, ps) =>
        val detail = ps.map{p => p.map(_.original).mkString(" ")}.mkString(" | ")
        s"* ${p.mkString(" ")} : ${ps.length}\n\t${detail}"
    }.mkString("\n")
  }

  def phrasesStat = {
    template("Phrases Usage", stat.filter{case (p, _) => p.length>1}.toList)
  }

  override def toString = {
    template("Statistics", stat.toList)
  }
}

class EssayStat(isTrivialWord: WordWithRange => Boolean, phraseMap: FSAState){
  def longestPhraseInSentence(words: Seq[Root]) = {
    def iterate(words: Seq[Root], currentState: FSAState, lastAccept: Option[Int], currentLen: Int): Option[Int] = {
      val accept = if(currentState.acceptable) Some(currentLen) else lastAccept
      if(words.isEmpty){
        accept
      }else{
        val w = words.head
        currentState.nextState(w) match{
          case Some(n) =>
            iterate(words.tail, n, accept, currentLen+1)
          case None =>
            accept
        }
      }
    }
    iterate(words, phraseMap, None, 0)
  }

  
  def stat(sentenceParts: Seq[SentencePart]): StatResult = {
    val result = new StatResult

    def sentenceWordUse(words: Seq[WordWithRange]): Unit = {
      if(words.nonEmpty){
        val roots = words.toStream.map(_.root)
        longestPhraseInSentence(roots) match {
          case Some(l) =>
            val (p, left) = words.splitAt(l)
            result.addUsage(p.toList)
            sentenceWordUse(left)
          case None =>
            if(!isTrivialWord(words.head))
              result.addUsage(List(words.head))

            sentenceWordUse(words.tail)
        }
      }
    }

    sentenceParts.foreach{ sentence =>
      sentenceWordUse(sentence.words)
      result.wordCount += sentence.words.length
    }

    result
  }

}

object EssayStat {
  type PhraseUsage = List[WordWithRange]

  def getPhraseFromUsage(usage: PhraseUsage): Phrase = usage.map(_.root)

  def rangeOfPhraseUsage(usage: PhraseUsage) = {
    TextRange(usage.head.range.start, usage.last.range.until)
  }

  def fileDefined(dir: String, stemmer: Stemmer) = {
    val trivialSet = new mutable.HashSet[Root]()

    for{
      line <- LoadFile.load(s"$dir/trivial-words.txt") if !line.trim.startsWith("//")
      word <- line.split("\\s+")
    }{
      trivialSet += stemmer.stem(word)
    }

    new EssayStat(isTrivialWord = w => trivialSet.contains(w.root), FSA.fileDefined(dir,stemmer))
  }

  lazy val standard = fileDefined(LoadFile.standard, SimpleStemmer.standard)

  val sample =
    """
      |I believe the total number of cars twenty years later will be fewer than today. Although our industry is becoming more and more strong and there is still quite much need of cars in many developing countries today, there are three important reasons which I believe will reduce the amount of cars in the future:
      |
      |First, I believe the increasingly use of new innovations in means of transport will replace the utility of traditional ones, including the use of cars. Through the history of human, we've never stopped creating new methods of traveling. In ancient days, we tamed horses, invented boats and carriages. And about one hundred years ago, the first car was designed and put into use. So it's hard to believe that we will stop here and not invent some new means of transport. Those cool traveling machines you read or heard of from science fictions, like flying cars, personal mini airplanes or city transport belts, may well get popular in some near future. And when more people have adapted to those new ways of travel, they will never turn back again. Our ancestors were once used to riding on the backs of horses or sitting in carriages, but as soon as the appearance of modern cars and trains, the use of those old methods was quickly replaced. And you can even hardly see any horses in a city nowadays. Unexceptionally, this principle will apply to cars as well, so the people driving cars in that day may be as rare as those who rides a horse you see today.
      |
      |Second, besides those new inventions, the current trend in our traffic study is heading towards a direction of effectiveness. Congestion has become a serious problem of modern cities and many great minds are working on it. So I believe there will soon be some good solutions to this problem, like building more efficient public transportation system and make them more efficient. People's opinions may change as well, and people will realize the harm of too many cars and are more willing to use those alternatives instead. Just take the example of my surroundings, I can feel that fewer people I know like driving cars today than the past.
      |
      |Third, let's talk about something that is happening right around us. Auto-driven cars are beginning to catch the public's attention recently. There have been some auto-driven cars already put into use in Singapore, and Uber's auto car plan is about to start as well. And one major advantage of auto-driven cars is their outstanding responsiveness. Their use will drastically reduce the waiting time of passengers, and also results in a lower the idle time percentage than traditional taxis. So, in another word, there will be fewer taxis as well as fews cars, but the transporting capacity will be increased. And the auto-driven cars' ease of use will also help them soon becoming more popular, and as a result, more people will choose to rely on them instead of buying their own cars. These two factors together will naturally reduce the total amount of cars.
      |
      |So in conclusion, because the reasons I've given above, I come to the prediction that there will be fewer cars in the next twenty years.
    """.stripMargin


  def main(args: Array[String]) {

    val result = standard.stat(EssayParser.standard.parseText(sample))
    println{
      result.phrasesStat
    }
  }
}
