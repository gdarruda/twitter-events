import scala.collection.JavaConverters._
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.time.OffsetDateTime
import com.twitter.clientlib.model.{Tweet, User}
import scala.annotation.tailrec

object App {

  def loadUserTweets(twitter: Twitter, 
                     user: String,
                     startTime : Option[OffsetDateTime] = None,
                     sinceId : Option[String] = None) =
    
    val startTime = Some(OffsetDateTime.parse("2022-01-01T15:20:30+08:00"))

    @tailrec
    def walkTokens(user: User, 
                   tweetList: List[Tweet] = List.empty,
                   token: Option[String] = None) : List[Tweet] = 
      
      twitter.getUserTweets(user.getId, paginationToken = token, startTime = startTime) match 
        case Some((tweets, meta)) => 
          
          val nextToken = if meta.getNextToken == null then None else Some(meta.getNextToken)

          if (meta.getNextToken() == null) tweetList
          else walkTokens(user, tweetList = tweets ++ tweetList, token = nextToken) 

        case None => tweetList

    twitter.getUser(user) match 
        case Some(user) => walkTokens(user)
        case None => Set()
  

  def main(args: Array[String]) : Unit = 
    
    val conf = ConfigFactory.load()
    val twitter = new Twitter(conf.getString("twitter.bearerToken"))
    val startTime = Some(OffsetDateTime.parse(conf.getString("twitter.startTime")))

    println(loadUserTweets(twitter, "gdarruda", startTime = startTime))

}
