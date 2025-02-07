file:///C:/studia/tw/TWWebCrawler/src/main/scala/webcrawler/WebCrawler.scala
### java.nio.file.InvalidPathException: Illegal char <:> at index 3: jar:file:///C:/Users/Chris/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.15/scala-library-2.13.15-sources.jar!/scala/Array.scala

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
offset: 1483
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

  def recursiveStep(url: String, n: Int): Future[List[String]] = Future {
    // val url = elem.getAttributeByName("href");
    // var result = List[String]();
    // println(s"$url");
    // println(s"$n");
    // if(n != 0 && url != null){
    //     println("1");
    //     result :+ url
    //     val node = new HtmlCleaner().clean(new URL(url))
    //     val elems = elem.getElementsByName("a", true)
    //     elems foreach { elem2 => 
    //         recursiveStep(elem2, n - 1) onComplete {
    //             case Success(value) => {
    //                 println(value.length)
    //                 result ++= value
    //             }
    //             case Failure(exception) => exception.printStackTrace()
    //         }
    //     }
    // }
    // println(result.length)
    // result

    
    var result = List[String]();
    val cleaner = new HtmlCleaner
    val props = cleaner.getProperties

    //val rootNode = cleaner.clean(html.mkString) 
    val rootNode = cleaner.clean(new URL(url))
    val elements = rootNode.getElementsByName("a", true) 
    elements m@@ { elem => {
        val elemUrl = elem.getAttributeByName("href")
        val elemUrlStr = if (elemUrl.startsWith("http"))  elemUrl.toString() else (url + elemUrl);

        result :+ elemUrlStr;
        recursiveStep(elemUrlStr, n - 1) onComplete {
            case Success(value) => result ++= value;
            case Failure(exception) => exception.printStackTrace();
        };
    }}

    result;
  }

  val url = "http://google.com"
  //val html = Source.fromURL(url)

  val cleaner = new HtmlCleaner
  val props = cleaner.getProperties

  //val rootNode = cleaner.clean(html.mkString) 
  val rootNode = cleaner.clean(new URL(url))

  recursiveStep(url, 5) onComplete {
    case Success(value) => value foreach println;
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
	dotty.tools.pc.completions.CaseKeywordCompletion$.matchContribute(MatchCaseCompletions.scala:292)
	dotty.tools.pc.completions.Completions.advancedCompletions(Completions.scala:349)
	dotty.tools.pc.completions.Completions.completions(Completions.scala:122)
	dotty.tools.pc.completions.CompletionProvider.completions(CompletionProvider.scala:135)
	dotty.tools.pc.ScalaPresentationCompiler.complete$$anonfun$1(ScalaPresentationCompiler.scala:150)
```
#### Short summary: 

java.nio.file.InvalidPathException: Illegal char <:> at index 3: jar:file:///C:/Users/Chris/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.15/scala-library-2.13.15-sources.jar!/scala/Array.scala