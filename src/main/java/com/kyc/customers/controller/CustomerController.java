package com.kyc.customers.controller;

import com.kyc.core.model.graphql.RequestGraphqlData;
import com.kyc.core.model.web.RequestData;
import com.kyc.customers.model.graphql.input.CustomerFilter;
import com.kyc.customers.model.graphql.input.CustomerInput;
import com.kyc.customers.model.graphql.types.Customer;
import com.kyc.customers.services.CustomerService;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Controller
public class CustomerController {

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @QueryMapping
    public List<Customer> customers(DataFetchingEnvironment environment){

        RequestGraphqlData<Void> req = RequestGraphqlData.<Void>builder()
                .environment(environment)
                .build();
        return customerService.getAllCustomers(req);
    }

    @QueryMapping
    public Customer customer(@Argument CustomerFilter filter,
                             DataFetchingEnvironment environment){

        RequestGraphqlData<CustomerFilter> req = RequestGraphqlData.<CustomerFilter>builder()
                .payload(filter)
                .environment(environment)
                .build();

        return customerService.getCustomerByFilter(req);
    }

    @MutationMapping
    public Integer addCustomer(@Argument @Valid CustomerInput customer){

        LOGGER.info("{}",customer);
        RequestGraphqlData<CustomerInput> req = RequestGraphqlData.<CustomerInput>builder()
                .payload(customer)
                .build();
        return customerService.createCustomer(req);
    }

    @MutationMapping
    public Customer updateCustomer(@Argument Integer id, @Argument @Valid CustomerInput customer){

        RequestGraphqlData<CustomerInput> req = RequestGraphqlData.<CustomerInput>builder()
                .arguments(Collections.singletonMap("id",id))
                .payload(customer)
                .build();

        return customerService.updateCustomer(req);
    }

    @MutationMapping
    public Boolean deleteCustomer(@Argument Integer id){

        RequestGraphqlData<Void> req = RequestGraphqlData.<Void>builder()
                .arguments(Collections.singletonMap("id",id))
                .build();
        return customerService.deleteCustomer(req);
    }
}
