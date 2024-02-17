package com.kyc.customers.controller;

import com.kyc.core.model.graphql.RequestGraphqlData;
import com.kyc.core.properties.KycMessages;
import com.kyc.customers.model.graphql.types.Customer;
import com.kyc.customers.services.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@GraphQlTest(CustomerController.class)
@ActiveProfiles("test")
public class CustomerControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private KycMessages kycMessages;

    @Test
    public void customers_retrievingCustomers_returnListCustomers(){

        Customer customer = new Customer();
        customer.setId(1);

        given(customerService.getAllCustomers(any(RequestGraphqlData.class)))
                .willReturn(Collections.singletonList(customer));

        graphQlTester.documentName("getAllCustomers")
                .execute()
                .path("customers[*].id")
                .hasValue();
    }

    @Test
    public void customer_retrieveCustomerWithFilter_returnCustomer(){

        Customer customer = new Customer();
        customer.setId(1);

        given(customerService.getCustomerByFilter(any(RequestGraphqlData.class)))
                .willReturn(customer);

        graphQlTester.documentName("getCustomerById")
                .variable("id",1)
                .execute()
                .path("customer.id")
                .hasValue();
    }

    @Test
    public void addCustomer_addingNewCustomer_returnCustomerId(){

        given(customerService.createCustomer(any(RequestGraphqlData.class))).willReturn(1);

        graphQlTester.documentName("addCustomer")
                .execute()
                .path("addCustomer")
                .entity(Integer.class)
                .isEqualTo(1);
    }

    @Test
    public void updateCustomer_updatingCustomer_returnUpdateCustomerData(){

        Customer customer = new Customer();
        customer.setId(1);

        given(customerService.updateCustomer(any(RequestGraphqlData.class))).willReturn(customer);

        graphQlTester.documentName("updateCustomer")
                .execute()
                .path("updateCustomer.id")
                .entity(Integer.class)
                .isEqualTo(1);
    }

    @Test
    public void deleteCustomer_deletingCustomer_returnBooleanTrue(){

        given(customerService.deleteCustomer(any(RequestGraphqlData.class))).willReturn(true);

        graphQlTester.documentName("deleteCustomer")
                .variable("id",1)
                .execute()
                .path("deleteCustomer")
                .entity(Boolean.class)
                .isEqualTo(true);
    }
}
