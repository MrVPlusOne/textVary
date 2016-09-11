import jiayiwei.essay.{EssayStat, FSA}
import jiayiwei.essay.FSA.Phrase
import utest._
import utest.framework.{Test, Tree}


object FSATest extends TestSuite {
  override def tests: Tree[Test] = this {
    val p1 = List("hello", "there","how","are","you")
    val p2 = List("hello","there","world")
    val p3 = List("something", "irrelevant")
    val ps3 = List(p1,p2,p3)

    'single_phrase {
      val state = FSA.newRoot()
      state.addPhrase(p1)
      assert(state.subPhrases == List(p1))
    }

    'multi_phrase {
      assert(FSA.createFromPhrases(ps3).subPhrases.toSet == ps3.toSet)
    }

    'longest_phrase_test {
      val fsa = FSA.createFromPhrases(ps3)
      val stat = new EssayStat(_=>false, fsa)
      assert(stat.longestPhraseInSentence(Seq("x", "y", "z")).isEmpty)
      assert(stat.longestPhraseInSentence(Seq("hello")).isEmpty)
      assert(stat.longestPhraseInSentence(p3).contains(2))
      val a = stat.longestPhraseInSentence(Seq("hello", "there", "world", "extra"))
      assert(a == Some(3))
    }
  }
}
