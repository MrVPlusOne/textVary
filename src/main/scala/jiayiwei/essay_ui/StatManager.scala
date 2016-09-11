package jiayiwei.essay_ui

import jiayiwei.essay.EssayStat.PhraseUsage
import jiayiwei.essay.FSA.Phrase
import jiayiwei.essay.{TextRange, EssayParser, EssayStat}
import rx.{Ctx, Rx, Var}



class StatManager(markerPane: MarkerTextPane, statModel: EssayStat, essayParser: EssayParser, implicit val ctx: Ctx.Owner) {
  import StatManager._

  val underlineThicknessVar = Var(3f)

  private val statDataVar = Var(getStat)
  val wordCountVar = Rx{ statDataVar().wordCount }
  
  private val caretDotVar = Var(currentCaretDot)
  private val markersToDisplay = Rx{
    val colorBlocks = statDataVar().phraseMap.flatMap {
      case (p, ps) =>
        val frequency = ps.length
        val line = ColorBlock(ColorBlock.colorFromFrequency(frequency))
        ps.map { usage =>
          val range = EssayStat.rangeOfPhraseUsage(usage)
          MarkRegion(range, line)
        }
    }

    val highlights = statDataVar().infoRegions.find(_.range.isNearTo(caretDotVar())) match {
      case Some(info) =>
        val usageList = statDataVar().phraseMap(info.phrase)
        val frequency = usageList.length
        val block = ColorUnderline(ColorUnderline.colorFromFrequency(frequency), underlineThicknessVar())
        usageList.map { usage =>
          val range = EssayStat.rangeOfPhraseUsage(usage)
          MarkRegion(range, block)
        }
      case None =>
        Seq()
    }

    colorBlocks ++ highlights
  }

  markersToDisplay.trigger{
    markerPane.setMarkers(markersToDisplay.now.toSeq)
  }
  
  def getStat: StatData = {
    val text = markerPane.getText
    val result = statModel.stat(essayParser.parseText(text))
    val phraseMap = result.stat.toMap.filter{case (p,ps) => ps.length>=2}
    val infoRegions = phraseMap.toArray.flatMap{
      case (p, ps) => ps.map{usage =>
        val range = EssayStat.rangeOfPhraseUsage(usage)
        InfoRegion(p, range)
      }
    }.sortBy(_.range.start)
    
    StatData(infoRegions,phraseMap, result.wordCount)
  }
  
  def currentCaretDot = markerPane.getCaret.getDot
  
  def editCallBack() = {
    statDataVar() = getStat
  }

  def caretCallBack() = {
    caretDotVar() = currentCaretDot
  }
}

object StatManager {
  case class InfoRegion(phrase: Phrase , range: TextRange)

  case class StatData(infoRegions: Array[InfoRegion], phraseMap: Map[Phrase, Vector[PhraseUsage]], wordCount: Int)
}
