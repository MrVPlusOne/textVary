import jiayiwei.essay.SimpleStemmer
import utest._
import utest.framework.{Test, Tree}


object StemmerTest extends TestSuite{
  override def tests: Tree[Test] = this {

    'simple_stemmer_check {
      val s = SimpleStemmer.standard

      assert(s.stem("adapted") == "adapt")
      assert(s.stem("going") == "go")
      assert(s.stem("comes") == "come")
      assert(s.stem("it") == "it")
    }
  }
}
