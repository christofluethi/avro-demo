package ch.shaped.kafka.avro.customer.producer.boundary;

import ch.shaped.kafka.avro.customer.producer.control.CustomerMapper;
import ch.shaped.kafka.avro.customer.producer.control.CustomerService;
import ch.shaped.kafka.avro.customer.producer.entity.CustomerDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/customer")
public class CustomerResource {

    @Inject
    CustomerService customerService;

    @Inject
    CustomerMapper mapper;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(CustomerDto dto) {
        customerService.publish(mapper.toEntity(dto));
        return Response.status(Response.Status.CREATED).build();
    }
}
