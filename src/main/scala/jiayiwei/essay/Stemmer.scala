package jiayiwei.essay

import scala.collection.mutable

object SimpleStemmer{

  def fileDefined(directory: String) = {
    val irregMap = new mutable.HashMap[String,String]()

    def addWord(root: String)(forms: String*): Unit = {
      forms.foreach{
        f => irregMap += (f -> root)
      }
    }

    for {
      line <- LoadFile.load(s"$directory/irregular-words.txt") if !line.trim.startsWith("//")
    }{
      val list = line.trim.split("\\s+")
      if(list.length >= 2){
        addWord(list.head)(list.tail :_*)
      }
    }

    println(s"User defined stemmer with ${irregMap.size} irregular stemming rules loaded.")
    new SimpleStemmer(irregMap)
  }

  lazy val standard = SimpleStemmer.fileDefined(LoadFile.standard)
}

/**
 * A simple stemmer using a combination of stemming dictionary and Porter's algorithm
 * @param irregMap
 */
class SimpleStemmer(irregMap: mutable.Map[String,String]) extends Stemmer {
   val porter = PorterStemmer

   override def stem(wordText: String): String = {
     val lowerCase = wordText.toLowerCase
     irregMap.get(lowerCase) match {
       case Some(result) => result
       case None =>
         porter.stem(lowerCase)
     }
   }
 }

trait Stemmer{
  def stem(word: String): String
}

object StemmerLib{

  val stemmer = SimpleStemmer.standard
  def main(args: Array[String]) {
    while (true){
      val word = Console.in.readLine()
      if(word.isEmpty)
        return
      println(stemmer.stem(word))
    }
  }
}