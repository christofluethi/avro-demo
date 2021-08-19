package ch.shaped.kafka.avro.customer.consumer.boundary;

import ch.shaped.kafka.avro.Customer;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.quarkus.runtime.StartupEvent;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class SimpleKafkaConsumer {

    private KafkaConsumer<String, Customer> consumer;
    private final Logger logger = LoggerFactory.getLogger(SimpleKafkaConsumer.class.getName());

    @Inject
    DataSocket socket;

    @ConfigProperty(name = "topic")
    String topic;

    @ConfigProperty(name = "bootstrap.servers")
    String bootstrapServer;

    @ConfigProperty(name = "schema.registry.url")
    String schemaRegistryUrl;

    void onStart(@Observes StartupEvent ev) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServer);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "customer-consumer-" + UUID.randomUUID().toString());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "raw-consumergroup");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        this.consumer = new KafkaConsumer<>(props);

        try {
            CompletableFuture.runAsync(this::runConsumer);
        } catch(Exception e) {
            logger.warn("Exception catched: ",e);
        }
    }

    public void runConsumer() {
        logger.info("Starting consumer-poll");

        this.consumer.subscribe(List.of(this.topic));

        while (true) {
            ConsumerRecords<String, Customer> records = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
            for (ConsumerRecord<String, Customer> record : records) {
                Customer customer = record.value();
                String cStr = customer.getFirstName() + " " + customer.getLastName()+" "+customer.getAge()+" "+customer.getHeight()+" "+customer.getAutomatedEmail();
                logger.info("Consumed: " + cStr);
                socket.broadcast(cStr);
            }
        }
    }
}
