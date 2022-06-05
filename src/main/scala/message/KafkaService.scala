package kafka

import java.util.Properties
import com.typesafe.config.ConfigFactory

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

object KafkaService {
  
    val props = new Properties()
    val conf = ConfigFactory.load()
    
    props.put("bootstrap.servers", conf.getString("kafka.bootstrap.servers"))
    props.put("key.serializer", conf.getString("kafka.key.serializer"))
    props.put("value.serializer", conf.getString("kafka.value.serializer"))

    lazy val producer = new KafkaProducer[String, String](props)

    def publish(topic: String, message: String) = 
        producer.send(new ProducerRecord[String, String](topic, topic, message))
    
}
