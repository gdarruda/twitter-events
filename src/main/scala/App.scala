import scala.collection.JavaConverters._
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

object App {

  def main(args: Array[String]) : Unit = {
    
    val conf = ConfigFactory.load()
    val twitter = new Twitter(conf.getString("twitter.bearerToken"))

    val b = twitter.getUser("gdarruda") match {
      case Some(user) => twitter.getUserTweets(user.getId)
      case None => None
    }

    println(b)

  }
}
