package jiayiwei.essay

/**
 * Created by weijiayi on 9/8/16.
 */
object CommonPhrases {
  def main(args: Array[String]) {
    val lines = LoadFile.load("commonPhrases_irreg.txt")
    for(line <- lines if line.nonEmpty && !line.contains("…") && !line.contains("．")){
      println(line.trim)
    }
  }
}
