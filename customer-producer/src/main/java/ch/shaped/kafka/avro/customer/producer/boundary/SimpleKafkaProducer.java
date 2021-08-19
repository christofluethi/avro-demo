package ch.shaped.kafka.avro.customer.producer.boundary;

import ch.shaped.kafka.avro.Customer;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.serializers.subject.TopicNameStrategy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.Properties;
import java.util.UUID;

@ApplicationScoped
public class SimpleKafkaProducer {

    private KafkaProducer<String, Customer> producer;

    private String id;

    private Logger logger = LoggerFactory.getLogger(SimpleKafkaProducer.class.getName());

    @ConfigProperty(name = "topic")
    String topic;

    @ConfigProperty(name = "bootstrap.servers")
    String bootstrapServer;

    @ConfigProperty(name = "schema.registry.url")
    String schemaRegistryUrl;

    @PostConstruct
    public void init() {
        this.id = UUID.randomUUID().toString();

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServer);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "customer-producer-" + id);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, false);
        props.put(KafkaAvroSerializerConfig.VALUE_SUBJECT_NAME_STRATEGY, TopicNameStrategy.class.getName());
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        this.producer = new KafkaProducer<>(props);
    }

    public void sendMessage(Customer customer) throws Exception {
        String message = "Message - " + System.nanoTime() + " - " + UUID.randomUUID().toString();
        RecordMetadata rm = this.producer.send(new ProducerRecord<>(topic, null, customer)).get();
        this.logger.debug("Producer(id={},partition={}) wrote message: {}", id, rm.partition(), message);
    }
}
