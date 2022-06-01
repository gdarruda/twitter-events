import com.twitter.clientlib.api.TwitterApi
import com.twitter.clientlib.TwitterCredentialsOAuth2
import com.twitter.clientlib.TwitterCredentialsBearer
import com.twitter.clientlib.ApiException

import com.twitter.clientlib.model.User
import com.twitter.clientlib.model.Tweet

import scala.collection.JavaConverters._
import java.time.OffsetTime
import java.time.OffsetDateTime
import com.typesafe.scalalogging.StrictLogging
import com.twitter.clientlib.model.GenericTweetsTimelineResponseMeta

class Twitter(bearerToken: String) extends StrictLogging {

  class APICaller[A] {
    def callAPI(call: () => A, name: String) : Option[A] =
      try 
          logger.info(s"Calling API ($name)")
          Some(call())
      catch
        case e : ApiException => 
          logger.error(s"Exception when calling API ($name)")
          logger.error("Status code: " + e.getCode())
          logger.error("Reason: " + e.getResponseBody())
          logger.error("Response headers: " + e.getResponseHeaders())
          logger.error(e.getStackTrace().map(_.toString()).reduce(_ + _))
          None
  }
  
  private lazy val apiInstance = {
    
    logger.info("Creating API Instance...")

    val apiInstance = new TwitterApi()
    apiInstance.setTwitterCredentials(new TwitterCredentialsBearer(bearerToken))
    apiInstance
  }

  def getUser(username: String, 
              expansions : Set[String] = Set.empty[String],
              userFields : Set[String] = Set.empty[String],
              tweetFields : Set[String] = Set.empty[String]) = 

      new APICaller[User].callAPI(() => apiInstance
          .users()
          .findUserByUsername(username, 
                              expansions.asJava, 
                              tweetFields.asJava, 
                              userFields.asJava).getData(), "getUser")

  def getUserTweets(userID: String,
                    sinceId : Option[String] = None,
                    untilId: Option[String] = None,
                    maxResults : Option[Int] = None,
                    exclude : Set[String] = Set.empty[String],
                    paginationToken : Option[String] = None,
                    startTime : Option[OffsetDateTime] = None,
                    endTime : Option[OffsetDateTime] = None,
                    expansions : Set[String] = Set.empty[String],
                    tweetFields : Set[String] = Set.empty[String],
                    userFields: Set[String] = Set.empty[String],
                    mediaFields: Set[String] = Set.empty[String],
                    placeFields: Set[String] = Set.empty[String],
                    pollFields: Set[String] = Set.empty[String]
                    ) = 

    new APICaller[(List[Tweet], GenericTweetsTimelineResponseMeta)].callAPI(
      () => 
        val request = apiInstance
          .tweets()
          .usersIdTweets(userID, 
                          sinceId.getOrElse(null), 
                          untilId.getOrElse(null), 
                          maxResults.getOrElse(null).asInstanceOf[Integer], 
                          exclude.asJava, 
                          paginationToken.getOrElse(null), 
                          startTime.getOrElse(null), 
                          endTime.getOrElse(null), 
                          expansions.asJava, 
                          tweetFields.asJava,
                          userFields.asJava, 
                          mediaFields.asJava,
                          placeFields.asJava, 
                          pollFields.asJava)
        (request.getData().asScala.toList, request.getMeta())
      ,
      "getUserTweets"
    )
    
}
