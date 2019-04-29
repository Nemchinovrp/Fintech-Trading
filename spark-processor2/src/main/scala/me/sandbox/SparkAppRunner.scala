package me.sandbox

import me.sandbox.sql.streaming.sink.kafka.MessagePublisher

object SparkAppRunner extends App {
  		// // Set up Kafka brokers and KafkaProducer to send messages to brokers
	println("please wait ... ( at least 100 seconds before first white colored log line come up )"	)
	MessagePublisher
  		// // Set up Spark session to read stream (consume) from kafka brokers,
  		// // do any transformations/processing
	SparkStructStreaming

}




