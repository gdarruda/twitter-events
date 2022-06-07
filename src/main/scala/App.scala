import java.time.OffsetDateTime

import scala.collection.JavaConverters._
import scala.annotation.tailrec

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import twitter.TwitterService
import com.twitter.clientlib.model.Tweet

import database.Profile
import database.SQLite
import kafka.KafkaService

import scala.collection.parallel.CollectionConverters.*

object App {

  def main(args: Array[String]) : Unit = 
    
    val conf = ConfigFactory.load()

    val profiles = SQLite.loadProfiles.par
      .map{profile => 
        
        if profile.userId == null then
          TwitterService.loadUser(profile) match 
            case None => (false, None)
            case Some(user) => (true, Some(Profile(profile.username, user.getId, profile.sinceId)))
        else 
          (false, Some(profile))

      }
      .map{(needsUpdate, profile) => 
        
        val tweets = profile match
          case Some(profile) => TwitterService.loadUserTweets(profile)
          case None => List.empty[Tweet]
        
        (tweets.length > 0 || needsUpdate, tweets, profile)
      }
    
    profiles
      .flatMap(_._2)
      .foreach{tweet => 
        KafkaService.publish(conf.getString("kafka.topic.tweets"), tweet.toJson)
      }

    profiles
      .filter(_._1)
      .foreach{(_, tweets, profile) => 

        val lastTweet = tweets.map(_.getId.toLong).max

        SQLite.updateProfile(Profile(username = profile.get.username,
                                     userId = profile.get.userId,
                                     sinceId = lastTweet.toString))
      }

    SQLite.ctx.close
    KafkaService.producer.close

}
