import java.time.OffsetDateTime

import scala.collection.JavaConverters._
import scala.annotation.tailrec

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import com.twitter.clientlib.model.{Tweet, User}

import database.SQLite

object App {

  def loadUserTweets(twitter: Twitter, 
                     user: String,
                     startTime : Option[OffsetDateTime] = None,
                     sinceId : Option[String] = None) =

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
        case None => List.empty[Tweet]
  

  def main(args: Array[String]) : Unit = 
    
    val conf = ConfigFactory.load()
    val twitter = new Twitter(conf.getString("twitter.bearerToken"))
    val startTime = Some(OffsetDateTime.parse(conf.getString("twitter.startTime")))

    // loadUserTweets(twitter, "gdarruda", startTime = startTime)
    println(SQLite.loadProfile("gdarruda"))

}
