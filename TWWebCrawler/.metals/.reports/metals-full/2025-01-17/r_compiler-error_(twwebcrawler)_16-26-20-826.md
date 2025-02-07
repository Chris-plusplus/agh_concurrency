file:///C:/studia/tw/TWWebCrawler/src/main/scala/webcrawler/WebCrawler.scala
### java.nio.file.InvalidPathException: Illegal char <:> at index 3: jar:file:///C:/Users/Chris/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.15/scala-library-2.13.15-sources.jar!/scala/Array.scala

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
offset: 647
uri: file:///C:/studia/tw/TWWebCrawler/src/main/scala/webcrawler/WebCrawler.scala
text:
```scala
import org.htmlcleaner.TagNode
import scala.compiletime.ops.int
import scala.concurrent._
import scala.collection.Factory
import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import java.time.Duration
object WebCrawler extends App {
  import scala.io.Source
  import org.htmlcleaner.HtmlCleaner
  import java.net.URL

  def recursiveStep(url: String, n: Int): Future[Unit] = Future {
    val cleaner = new HtmlCleaner
    val rootNode = cleaner.clean(new URL(url))
    val elements = rootNode.getElementsByName("a", true) 
    elements m@@ { elem => {
        val url2 = elem.getAttributeByName("href")
        println(url2.toString)
    }}
  }

  val url = "http://google.com"
  //val html = Source.fromURL(url)

  //val rootNode = cleaner.clean(html.mkString) 
  var fut = recursiveStep(url, 5);
//   fut onComplete {
//       case Success(value) => println(s"${value}");
//       case Failure(exception) => exception.printStackTrace();
//     }
    Thread.sleep(2000);
    // var result = List[String]();
    // val cleaner = new HtmlCleaner

    // //val rootNode = cleaner.clean(html.mkString) 
    // val rootNode = cleaner.clean(new URL(url))
    // val elements = rootNode.getElementsByName("a", true) 
    // elements map { elem => 
    //     val url2 = elem.getAttributeByName("href")
    //     println(url2.toString)
    // }
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
	dotty.tools.pc.completions.CaseKeywordCompletion$.matchContribute(MatchCaseCompletions.scala:292)
	dotty.tools.pc.completions.Completions.advancedCompletions(Completions.scala:349)
	dotty.tools.pc.completions.Completions.completions(Completions.scala:122)
	dotty.tools.pc.completions.CompletionProvider.completions(CompletionProvider.scala:135)
	dotty.tools.pc.ScalaPresentationCompiler.complete$$anonfun$1(ScalaPresentationCompiler.scala:150)
```
#### Short summary: 

java.nio.file.InvalidPathException: Illegal char <:> at index 3: jar:file:///C:/Users/Chris/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.15/scala-library-2.13.15-sources.jar!/scala/Array.scala