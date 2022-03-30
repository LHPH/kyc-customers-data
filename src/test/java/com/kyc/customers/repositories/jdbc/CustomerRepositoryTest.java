package com.kyc.customers.repositories.jdbc;

import com.kyc.customers.model.graphql.input.CustomerName;
import com.kyc.customers.model.graphql.types.Customer;
import com.kyc.customers.repositories.mappers.CustomerMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CustomerRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private Properties queriesProps;

    @InjectMocks
    private CustomerRepository customerRepository;


    @BeforeAll
    public static void setUp(){
        MockitoAnnotations.openMocks(new CustomerRepositoryTest());
    }

    @Test
    public void getAllCustomers_retrievingCustomers_returnCustomersList(){

        given(queriesProps.get("getAllCustomers")).willReturn("SQL");
        given(jdbcTemplate.query(anyString(),any(CustomerMapper.class)))
                .willReturn(Collections.singletonList(new Customer()));

        List<Customer> result = customerRepository.getAllCustomers();

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    public void getCustomerById_retrievingCustomer_returnCustomer(){

        given(queriesProps.get("getCustomerById")).willReturn("SQL");
        given(jdbcTemplate.query(anyString(),any(CustomerMapper.class),anyInt()))
                .willReturn(Collections.singletonList(new Customer()));

        Customer result = customerRepository.getCustomerById(1);
        Assertions.assertNotNull(result);
    }

    @Test
    public void getCustomerById_retrievingNonExistentCustomer_returnNull(){

        given(queriesProps.get("getCustomerById")).willReturn("SQL");
        given(jdbcTemplate.query(anyString(),any(CustomerMapper.class),anyInt()))
                .willReturn(Collections.emptyList());

        Customer result = customerRepository.getCustomerById(1);
        Assertions.assertNull(result);
    }

    @Test
    public void getCustomerByRfc_retrievingCustomer_returnCustomer(){

        given(queriesProps.get("getCustomerByRfc")).willReturn("SQL");
        given(jdbcTemplate.query(anyString(),any(CustomerMapper.class),anyString()))
                .willReturn(Collections.singletonList(new Customer()));

        Customer result = customerRepository.getCustomerByRfc("TEST");
        Assertions.assertNotNull(result);
    }

    @Test
    public void getCustomerByRfc_retrievingNonExistentCustomer_returnNull(){

        given(queriesProps.get("getCustomerByRfc")).willReturn("SQL");
        given(jdbcTemplate.query(anyString(),any(CustomerMapper.class),anyString()))
                .willReturn(Collections.emptyList());

        Customer result = customerRepository.getCustomerByRfc("TEST");
        Assertions.assertNull(result);
    }

    @Test
    public void getCustomerByNames_retrievingCustomer_returnCustomer(){

        CustomerName customerName = new CustomerName();
        customerName.setFirstName("TEST");
        customerName.setSecondName("TEST");
        customerName.setLastName("TEST");
        customerName.setSecondLastName("TEST");

        given(queriesProps.get("getCustomerByName")).willReturn("SQL");
        given(jdbcTemplate.query(anyString(),any(CustomerMapper.class)
                ,anyString(),anyString(),anyString(),anyString()))
                .willReturn(Collections.singletonList(new Customer()));

        Customer result = customerRepository.getCustomerByNames(customerName);
        Assertions.assertNotNull(result);

    }

    @Test
    public void getCustomerByNames_retrievingNonExistentCustomer_returnNull(){

        CustomerName customerName = new CustomerName();
        customerName.setFirstName("TEST");
        customerName.setSecondName("TEST");
        customerName.setLastName("TEST");
        customerName.setSecondLastName("TEST");

        given(queriesProps.get("getCustomerByName")).willReturn("SQL");
        given(jdbcTemplate.query(anyString(),any(CustomerMapper.class)
                ,anyString(),anyString(),anyString(),anyString()))
                .willReturn(Collections.emptyList());

        Customer result = customerRepository.getCustomerByNames(customerName);
        Assertions.assertNull(result);
    }

}
