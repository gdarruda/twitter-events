package twitter

import com.twitter.clientlib.model.{Tweet, User}

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import database.Profile
import twitter.TwitterAPI

import scala.annotation.tailrec
import fansi.Str
import java.time.OffsetDateTime

object TwitterService {

    val conf = ConfigFactory.load()
    val twitter = new TwitterAPI(conf.getString("twitter.bearerToken"))

    def loadUser(profile: Profile) : Option[User] = twitter.getUser(profile.username)

    def loadUserTweets(profile: Profile) : List[Tweet] =

        @tailrec
        def walkTokens(tweetList: List[Tweet] = List.empty,
                       sinceId: Option[String] = None,
                       startTime: Option[OffsetDateTime] = None,
                       token: Option[String] = None) : List[Tweet] = 
        
            twitter.getUserTweets(profile.userId, 
                                  paginationToken = token,
                                  startTime = startTime,
                                  tweetFields = Set("id", "created_at", "text", "author_id"),
                                  sinceId = sinceId) match 
                case Some((tweets, meta)) => 
                
                    val nextToken = if meta.getNextToken == null then None else Some(meta.getNextToken)

                    if (nextToken.isEmpty) tweets ++ tweetList
                    else walkTokens(tweetList = tweets ++ tweetList, token = nextToken) 

                case None => tweetList

        val startTime = if (profile.sinceId == null) 
                        then Some(OffsetDateTime.parse(conf.getString("twitter.startTime"))) 
                        else None
        
        val sinceId = startTime match 
                        case Some(_) => None
                        case None => Some(profile.sinceId)

        walkTokens(sinceId = sinceId, startTime = startTime)
}
