package ch.shaped.kafka.avro.customer.producer.control;

import ch.shaped.kafka.avro.Customer;
import ch.shaped.kafka.avro.customer.producer.entity.CustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface CustomerMapper {

    /*
     * For Creating
     */
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "age", source = "age")
    @Mapping(target = "height", source = "height")
    @Mapping(target = "weight", source = "weight")
    Customer toEntity(CustomerDto dto);
}
