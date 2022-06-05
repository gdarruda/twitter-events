import java.time.OffsetDateTime

import scala.collection.JavaConverters._
import scala.annotation.tailrec

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import database.SQLite
import twitter.TwitterService
import database.Profile
import com.twitter.clientlib.model.Tweet

object App {

  def main(args: Array[String]) : Unit = 
    
    val profiles = SQLite
      .loadProfiles
      .map(profile => 
        if profile.userId == null then
          TwitterService.loadUser(profile) match 
            case None => (false, None)
            case Some(user) => (true, Some(Profile(profile.username, user.getId, profile.sinceId)))
        else (false, Some(profile)))
      .map((needsUpdate, profile) => 
        val tweets = profile match
          case Some(profile) => TwitterService.loadUserTweets(profile)
          case None => List.empty[Tweet]
        (tweets.length > 0 || needsUpdate, tweets))
    
    // profiles
    // profiles.filter(_._1).foreach(SQLite.)

    // .flatMap(profile => TwitterService.loadUserTweets(profile, ))
    // SQLite
    //   .loadProfiles
    //   .foreach(println)
    //   .flatMap(profile => loadUserTweets(twitter, 
    //                                      profile.username))

    SQLite.ctx.close()
}
