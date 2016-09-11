
import jiayiwei.essay.EssayParser
import utest._
import utest.framework.{Test, Tree}

import scala.util.Random

object EssayParserTest extends TestSuite{
  override def tests: Tree[Test] = this {
    val parser = EssayParser.standard

    'test_simple_text {
      val sample = "This was, really good! Am I right? End."
      val r = parser.parseText(sample)
      assert(r.length == 4)
    }

    'random_text_test {
      val random = new Random()
      val charList = (('a' to 'g') ++ ('H' to 'N') ++ "@#$%^&*()-+=~/<>" ++ "      \n\n,,,,....??!!;;\t").toArray
      val upper = charList.length
      def randomChar: Char = {
        val n = random.nextInt(upper)
        charList(n)
      }

      ((0 until 10) ++ (10 to 100 by 10) ++ (1000 to 2000 by 500)).foreach { i =>
        val text = String.copyValueOf((0 until 10*i).map { _ => randomChar }.toArray)
        parser.parseText(text)
      }
    }

    'sentence_test {
      val s1 = " \n\t   texts fight a fights   in addition great, in addition"
      val result = parser.parseText(s1)
      assert(result.length == 2)
      val raw = result.head.words.map(_.original)
      assert(raw == Seq("texts","fight","a","fights","in","addition","great"))
    }
  }
}
