package jiayiwei.essay

import FSA._

class FSAState(var acceptable: Boolean) {
  private var connections = Map[FSAWord, FSAState]()
  
  private def addConnection(word: FSAWord, state: FSAState) = {
    connections += (word -> state)
  }
  
  def nextState(word: FSAWord): Option[FSAState] = {
    connections.get(word)
  }

  def subPhrases: List[Phrase] = {
    val subs: List[Phrase] = connections.toList.flatMap{
      case (w,s) => s.subPhrases.map(w::_)
    }
    if(acceptable) emptyPhrase :: subs else subs
  }

  def addPhrase(phrase: Phrase): Unit ={
    phrase match {
      case Nil =>
        acceptable = true
      case w::t =>
        val next = nextState(w) match {
          case None =>
            val s = new FSAState(acceptable = false)
            addConnection(w, s)
            s
          case Some(s) => s
        }
        next.addPhrase(t)
    }
  }
}



object FSA{
  type FSAWord = String
  type Phrase = List[FSAWord]
  val emptyPhrase = List[FSAWord]()


  def newRoot() = new FSAState(acceptable = false)

  def createFromPhrases(phrases: Seq[Phrase]) = {
    val s = newRoot()
    phrases.foreach(p => s.addPhrase(p))
    s
  }

  def fileDefined(dir: String, stemmer: Stemmer) = {
    val s = newRoot()
    var phraseNum = 0
    for{
      line <- LoadFile.load(s"$dir/common-phrases.txt") if !line.trim.startsWith("//")
      words = line.split("\\s+") if words.length>=2
    }{
      s.addPhrase(words.map(stemmer.stem).toList)
      phraseNum += 1
    }
    println(s"User defined phrase model with $phraseNum phrases loaded.")
    s
  }
}