package ch.shaped.kafka.avro.customer.producer.control;

import ch.shaped.kafka.avro.Customer;
import ch.shaped.kafka.avro.customer.producer.boundary.SimpleKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CustomerService {

    @Inject
    SimpleKafkaProducer producer;

    public static final Logger logger = LoggerFactory.getLogger(CustomerService.class.getSimpleName());

    public void publish(Customer customer) {
        logger.info("Sending customer: " + customer.getFirstName() + " " + customer.getLastName());
        try {
            producer.sendMessage(customer);
        } catch (Exception e) {
            logger.warn("Exception: "+e.getMessage(), e);
        }
    }
}
