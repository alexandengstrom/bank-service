use rdkafka::config::ClientConfig;
use rdkafka::producer::{FutureProducer, FutureRecord};
use std::time::Duration;

fn get_producer() -> FutureProducer{
    let producer: FutureProducer = ClientConfig::new()
    .set("bootstrap.servers", "kafka:9092") // Adjust with your Kafka server address
    .set("debug", "all")
    .create()
    .expect("Producer creation failed");

    producer
}

pub async fn send_kafka_message(message: &str) {
    let producer = get_producer();

    let record = FutureRecord::to("bank-api") 
        .key("message")
        .payload(message);

    match producer.send(record, Duration::from_secs(5)).await {
        Ok(_) => println!("Message sent successfully!"),
        Err((e, _)) => eprintln!("Error sending message: {:?}", e),
    }
}
