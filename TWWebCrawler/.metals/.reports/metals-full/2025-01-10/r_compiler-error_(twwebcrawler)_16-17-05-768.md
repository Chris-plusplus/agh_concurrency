file:///C:/studia/tw/TWWebCrawler/src/main/scala/webcrawler/WebCrawler.scala
### java.nio.file.InvalidPathException: Illegal char <:> at index 3: jar:file:///C:/Users/Chris/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.15/scala-library-2.13.15-sources.jar!/scala/util/Try.scala

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
offset: 774
uri: file:///C:/studia/tw/TWWebCrawler/src/main/scala/webcrawler/WebCrawler.scala
text:
```scala
import org.htmlcleaner.TagNode
import scala.compiletime.ops.int
import scala.concurrent._
import scala.collection.Factory
import scala.util.Failure
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
object WebCrawler extends App {
  import scala.io.Source
  import org.htmlcleaner.HtmlCleaner
  import java.net.URL

  def recursiveStep(elem: TagNode, n: Int): Future[String] = Future {
    val url = elem.getAttributeByName("href");
    if(n != 0){
        val elems = elem.getElementsByName("a", true)
        elems map { elem2 => 
            val url2 = elem2.getAttributeByName("href");
            val node = new HtmlCleaner().clean(new URL(url2))
            recursiveStep(node, n - 1) onComplete {
                c@@
            }
        }
    }
    url
  }

  val url = "http://google.com"
  //val html = Source.fromURL(url)

  val cleaner = new HtmlCleaner
  val props = cleaner.getProperties

  //val rootNode = cleaner.clean(html.mkString) 
  val rootNode = cleaner.clean(new URL(url))

  recursiveStep(rootNode, 5) onComplete {
    case Success(value) => print(s"$value");
    case Failure(exception) => exception.printStackTrace()
  }
//   val elements = rootNode.getElementsByName("a", true) 
//   elements map { elem => 
//     val url = elem.getAttributeByName("href")
//     println(url.toString) 
//   }

}

```



#### Error stacktrace:

```
java.base/sun.nio.fs.WindowsPathParser.normalize(WindowsPathParser.java:204)
	java.base/sun.nio.fs.WindowsPathParser.parse(WindowsPathParser.java:175)
	java.base/sun.nio.fs.WindowsPathParser.parse(WindowsPathParser.java:77)
	java.base/sun.nio.fs.WindowsPath.parse(WindowsPath.java:92)
	java.base/sun.nio.fs.WindowsFileSystem.getPath(WindowsFileSystem.java:231)
	java.base/java.nio.file.Path.of(Path.java:148)
	java.base/java.nio.file.Paths.get(Paths.java:69)
	scala.meta.io.AbsolutePath$.apply(AbsolutePath.scala:58)
	scala.meta.internal.metals.MetalsSymbolSearch.$anonfun$definitionSourceToplevels$2(MetalsSymbolSearch.scala:70)
	scala.Option.map(Option.scala:242)
	scala.meta.internal.metals.MetalsSymbolSearch.definitionSourceToplevels(MetalsSymbolSearch.scala:69)
	dotty.tools.pc.completions.CaseKeywordCompletion$.dotty$tools$pc$completions$CaseKeywordCompletion$$$sortSubclasses(MatchCaseCompletions.scala:342)
	dotty.tools.pc.completions.CaseKeywordCompletion$$anon$1.applyOrElse(MatchCaseCompletions.scala:218)
	dotty.tools.pc.completions.CaseKeywordCompletion$$anon$1.applyOrElse(MatchCaseCompletions.scala:114)
	scala.PartialFunction$Lifted.apply(PartialFunction.scala:338)
	scala.PartialFunction$Lifted.apply(PartialFunction.scala:334)
	scala.Option.collect(Option.scala:462)
	dotty.tools.pc.completions.CaseKeywordCompletion$.contribute(MatchCaseCompletions.scala:248)
	dotty.tools.pc.completions.Completions.advancedCompletions(Completions.scala:407)
	dotty.tools.pc.completions.Completions.completions(Completions.scala:122)
	dotty.tools.pc.completions.CompletionProvider.completions(CompletionProvider.scala:135)
	dotty.tools.pc.ScalaPresentationCompiler.complete$$anonfun$1(ScalaPresentationCompiler.scala:150)
```
#### Short summary: 

java.nio.file.InvalidPathException: Illegal char <:> at index 3: jar:file:///C:/Users/Chris/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.15/scala-library-2.13.15-sources.jar!/scala/util/Try.scala