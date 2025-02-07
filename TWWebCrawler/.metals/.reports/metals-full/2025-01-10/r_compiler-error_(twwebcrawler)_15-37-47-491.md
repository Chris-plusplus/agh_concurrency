file:///C:/studia/tw/TWWebCrawler/src/main/scala/webcrawler/WebCrawler.scala
### dotty.tools.dotc.MissingCoreLibraryException: Could not find package scala from compiler core libraries.
Make sure the compiler core libraries are on the classpath.
   

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
uri: file:///C:/studia/tw/TWWebCrawler/src/main/scala/webcrawler/WebCrawler.scala
text:
```scala
object WebCrawler extends App {
  import scala.io.Source
  import org.htmlcleaner.HtmlCleaner
  import java.net.URL

  val url = "http://google.com"
  //val html = Source.fromURL(url)

  val cleaner = new HtmlCleaner
  val props = cleaner.getProperties

  //val rootNode = cleaner.clean(html.mkString) 
  val rootNode = cleaner.clean(new URL(url))

  val elements = rootNode.getElementsByName("a", true) 
  elements map { elem => 
    val url = elem.getAttributeByName("href")
    println(url.toString) 
  }
}

```



#### Error stacktrace:

```
dotty.tools.dotc.core.Denotations$.select$1(Denotations.scala:1326)
	dotty.tools.dotc.core.Denotations$.recurSimple$1(Denotations.scala:1354)
	dotty.tools.dotc.core.Denotations$.recur$1(Denotations.scala:1356)
	dotty.tools.dotc.core.Denotations$.staticRef(Denotations.scala:1360)
	dotty.tools.dotc.core.Symbols$.requiredPackage(Symbols.scala:944)
	dotty.tools.dotc.core.Definitions.ScalaPackageVal(Definitions.scala:215)
	dotty.tools.dotc.core.Definitions.ScalaPackageClass(Definitions.scala:218)
	dotty.tools.dotc.core.Definitions.AnyClass(Definitions.scala:281)
	dotty.tools.dotc.core.Definitions.syntheticScalaClasses(Definitions.scala:2184)
	dotty.tools.dotc.core.Definitions.syntheticCoreClasses(Definitions.scala:2199)
	dotty.tools.dotc.core.Definitions.init(Definitions.scala:2215)
	dotty.tools.dotc.core.Contexts$ContextBase.initialize(Contexts.scala:921)
	dotty.tools.dotc.core.Contexts$Context.initialize(Contexts.scala:544)
	dotty.tools.dotc.interactive.InteractiveDriver.<init>(InteractiveDriver.scala:41)
	dotty.tools.pc.CachingDriver.<init>(CachingDriver.scala:30)
	dotty.tools.pc.ScalaPresentationCompiler.$init$$$anonfun$1(ScalaPresentationCompiler.scala:85)
```
#### Short summary: 

dotty.tools.dotc.MissingCoreLibraryException: Could not find package scala from compiler core libraries.
Make sure the compiler core libraries are on the classpath.
   