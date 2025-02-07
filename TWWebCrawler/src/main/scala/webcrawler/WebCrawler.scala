import org.htmlcleaner.TagNode
import scala.concurrent._
import scala.collection.Factory
import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.ConcurrentLinkedQueue
object WebCrawler extends App {
  import scala.io.Source
  import org.htmlcleaner.HtmlCleaner
  import java.net.URL

  def recursiveStep(url: String, n: Int): Future[ConcurrentLinkedQueue[String]] = Future {
    val result = new ConcurrentLinkedQueue[String]()
    val futureQueue = new ConcurrentLinkedQueue[Future[ConcurrentLinkedQueue[String]]]();
    if (n > 0){
        val cleaner = new HtmlCleaner
        val node = cleaner.clean(new URL(url))
        val elements = node.getElementsByName("a", true) 
        elements map { elem => {
            var newUrl = elem.getAttributeByName("href")
            if(newUrl != null && newUrl.startsWith("http")) {
                //newUrl = if (newUrl.startsWith("http")) newUrl else (url + newUrl);
                result.add(newUrl)
                val fut = recursiveStep(newUrl, n - 1);
                fut onComplete {
                    case Success(subqueue) => result.addAll(subqueue);
                    case Failure(_) =>;
                }
                futureQueue.add(fut)
            }
        }}
    }

    futureQueue.forEach(fut => Await.ready(fut, Duration.Inf))

    result
  }

  val url = "https://home.agh.edu.pl/~faliszew/"

    // dla 3 może się zaciąć
    val fut = recursiveStep(url, 3);
    fut onComplete {
        case Success(value) => {
            value.forEach(println);
            println(s"link count = ${value.size()}");
        }
        case Failure(_) =>;
    }
    Await.ready(fut, Duration.Inf)
}
