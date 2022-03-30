package com.kyc.customers.repositories.jdbc;

import com.kyc.customers.model.graphql.input.CustomerName;
import com.kyc.customers.model.graphql.types.Customer;
import com.kyc.customers.repositories.mappers.CustomerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Properties;

@Repository
public class CustomerRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("queriesProps")
    private Properties queriesProps;

    public List<Customer> getAllCustomers(){

        String sql = String.valueOf(queriesProps.get("getAllCustomers"));

        return jdbcTemplate.query(sql,new CustomerMapper());
    }

    public Customer getCustomerById(Integer id){

        String sql = String.valueOf(queriesProps.get("getCustomerById"));

        List<Customer> customers = jdbcTemplate.query(sql,new CustomerMapper(),id);

        if(!customers.isEmpty()){
            return customers.get(0);
        }
        return null;
    }

    public Customer getCustomerByRfc(String rfc){

        String sql = String.valueOf(queriesProps.get("getCustomerByRfc"));

        List<Customer> customers = jdbcTemplate.query(sql,new CustomerMapper(),rfc);

        if(!customers.isEmpty()){
            return customers.get(0);
        }
        return null;
    }

    public Customer getCustomerByNames(CustomerName dataCustomer){

        String sql = String.valueOf(queriesProps.get("getCustomerByName"));

        List<Customer> customers = jdbcTemplate.query(sql,new CustomerMapper(),
                dataCustomer.getFirstName(),dataCustomer.getSecondName(),
                dataCustomer.getLastName(),dataCustomer.getSecondLastName());

        if(!customers.isEmpty()){
            return customers.get(0);
        }
        return null;
    }
}
