package com.kyc.customers.services;

import com.kyc.core.exception.KycGraphqlException;
import com.kyc.core.model.web.RequestData;
import com.kyc.core.properties.KycMessages;
import com.kyc.customers.mappers.CustomerMapper;
import com.kyc.customers.model.graphql.input.CustomerFilter;
import com.kyc.customers.model.graphql.input.CustomerInput;
import com.kyc.customers.model.graphql.input.CustomerName;
import com.kyc.customers.model.graphql.types.Customer;
import com.kyc.customers.repositories.jdbc.CustomerRepository;
import com.kyc.customers.repositories.stores.CustomerStoreProcedure;
import graphql.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.graphql.execution.ErrorType;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerStoreProcedure customerStoreProcedure;

    @Mock
    private KycMessages kycMessages;

    @InjectMocks
    private CustomerService customerService;


    @Test
    public void getAllCustomers_retrieveCustomers_returnListCustomers(){

        given(customerRepository.getAllCustomers()).willReturn(Collections.singletonList(new Customer()));

        List<Customer> list = customerService.getAllCustomers(RequestData.<Void>builder().build());
        Assert.assertNotEmpty(list);
    }

    @Test
    public void getCustomerByFilter_retrieveById_returnCustomer(){

        CustomerFilter filter = new CustomerFilter();
        filter.setId(1);
        RequestData<CustomerFilter> req = RequestData.<CustomerFilter>builder()
                .body(filter)
                .build();
        customerService.getCustomerByFilter(req);
        verify(customerRepository,times(1)).getCustomerById(1);
    }

    @Test
    public void getCustomerByFilter_retrieveByRfc_returnCustomer(){

        CustomerFilter filter = new CustomerFilter();
        filter.setRfc("TEST");
        RequestData<CustomerFilter> req = RequestData.<CustomerFilter>builder()
                .body(filter)
                .build();
        customerService.getCustomerByFilter(req);
        verify(customerRepository,times(1)).getCustomerByRfc("TEST");
    }

    @Test
    public void getCustomerByFilter_retrieveByName_returnCustomer(){

        CustomerFilter filter = new CustomerFilter();
        CustomerName name = new CustomerName();
        name.setFirstName("TEST");
        filter.setName(name);

        RequestData<CustomerFilter> req = RequestData.<CustomerFilter>builder()
                .body(filter)
                .build();
        customerService.getCustomerByFilter(req);
        verify(customerRepository,times(1)).getCustomerByNames(name);
    }

    @Test
    public void getCustomerByFilter_retrieveByUnknownFilter_returnEmptyCustomer(){

        CustomerFilter filter = new CustomerFilter();

        RequestData<CustomerFilter> req = RequestData.<CustomerFilter>builder()
                .body(filter)
                .build();
        customerService.getCustomerByFilter(req);
        verify(customerRepository,times(0)).getCustomerById(anyInt());
        verify(customerRepository,times(0)).getCustomerByRfc(anyString());
        verify(customerRepository,times(0)).getCustomerByNames(any(CustomerName.class));
    }

    @Test
    public void createCustomer_createNewCustomer_returnIdCustomer(){

        CustomerInput customerInput = new CustomerInput();
        RequestData<CustomerInput> req = RequestData.<CustomerInput>builder()
                .body(customerInput)
                .build();

        given(customerMapper.toCustomerType(any(CustomerInput.class))).willReturn(new Customer());
        given(customerStoreProcedure.getIdCustomer(any(Customer.class))).willReturn(0).willReturn(1);
        doNothing().when(customerStoreProcedure).saveCustomer(any(CustomerInput.class));

        int id = customerService.createCustomer(req);
        Assertions.assertEquals(1,id);
    }

    @Test
    public void createCustomer_CustomerWasFound_throwKycException(){

        KycGraphqlException ex = Assertions.assertThrows(KycGraphqlException.class,()->{

            CustomerInput customerInput = new CustomerInput();
            RequestData<CustomerInput> req = RequestData.<CustomerInput>builder()
                    .body(customerInput)
                    .build();

            given(customerMapper.toCustomerType(any(CustomerInput.class))).willReturn(new Customer());
            given(customerStoreProcedure.getIdCustomer(any(Customer.class))).willReturn(1);

            customerService.createCustomer(req);
        });
        Assertions.assertEquals(ErrorType.BAD_REQUEST,ex.getErrorType());
    }

    @Test
    public void createCustomer_errorCreatingCustomer_throwKycException(){

        KycGraphqlException ex = Assertions.assertThrows(KycGraphqlException.class,()->{

            CustomerInput customerInput = new CustomerInput();
            RequestData<CustomerInput> req = RequestData.<CustomerInput>builder()
                    .body(customerInput)
                    .build();

            given(customerMapper.toCustomerType(any(CustomerInput.class))).willReturn(new Customer());
            given(customerStoreProcedure.getIdCustomer(any(Customer.class))).willReturn(0);
            doThrow(new InvalidDataAccessResourceUsageException("error"))
                    .when(customerStoreProcedure).saveCustomer(any(CustomerInput.class));

            customerService.createCustomer(req);
        });
        Assertions.assertEquals(ErrorType.INTERNAL_ERROR,ex.getErrorType());
    }

    @Test
    public void updateCustomer_updatingCustomer_updatedCustomer(){

        CustomerInput customerInput = new CustomerInput();
        RequestData<CustomerInput> req = RequestData.<CustomerInput>builder()
                .pathParams(Collections.singletonMap("id",1))
                .body(customerInput)
                .build();

        given(customerRepository.getCustomerById(1)).willReturn(new Customer());
        doNothing().when(customerStoreProcedure).updateCustomer(anyInt(),any(CustomerInput.class));

        Customer result = customerService.updateCustomer(req);
        Assert.assertNotNull(result);
    }

    @Test
    public void updateCustomer_customerIdNonExistent_throwsKycException(){

        KycGraphqlException ex = Assertions.assertThrows(KycGraphqlException.class,()->{

            CustomerInput customerInput = new CustomerInput();
            RequestData<CustomerInput> req = RequestData.<CustomerInput>builder()
                    .pathParams(Collections.singletonMap("id",1))
                    .body(customerInput)
                    .build();

            given(customerRepository.getCustomerById(1)).willReturn(null);

            customerService.updateCustomer(req);
        });
        Assertions.assertEquals(ErrorType.BAD_REQUEST,ex.getErrorType());
    }

    @Test
    public void updateCustomer_errorUpdatingCustomer_throwsKycException(){

        KycGraphqlException ex = Assertions.assertThrows(KycGraphqlException.class,()->{

            CustomerInput customerInput = new CustomerInput();
            RequestData<CustomerInput> req = RequestData.<CustomerInput>builder()
                    .pathParams(Collections.singletonMap("id",1))
                    .body(customerInput)
                    .build();

            given(customerRepository.getCustomerById(1)).willReturn(new Customer());
            doThrow(new InvalidDataAccessResourceUsageException("error"))
                    .when(customerStoreProcedure).updateCustomer(anyInt(),any(CustomerInput.class));

            customerService.updateCustomer(req);
        });
        Assertions.assertEquals(ErrorType.INTERNAL_ERROR,ex.getErrorType());
    }

    @Test
    public void deleteCustomer_deletingCustomer_deletedCustomer(){

        RequestData<Void> req = RequestData.<Void>builder()
                .pathParams(Collections.singletonMap("id",1))
                .build();

        given(customerRepository.getCustomerById(anyInt())).willReturn(new Customer()).willReturn(null);
        doNothing().when(customerStoreProcedure).deleteCustomer(1);

        Boolean result = customerService.deleteCustomer(req);
        Assert.assertTrue(result);
    }

    @Test
    public void deleteCustomer_deletingCustomer_NoDeletedCustomer(){

        RequestData<Void> req = RequestData.<Void>builder()
                .pathParams(Collections.singletonMap("id",1))
                .build();

        given(customerRepository.getCustomerById(1)).willReturn(new Customer()).willReturn(new Customer());
        doNothing().when(customerStoreProcedure).deleteCustomer(1);

        Boolean result = customerService.deleteCustomer(req);
        Assert.assertFalse(result);
    }

    @Test
    public void deleteCustomer_CustomerNonExistent_throwsKycException(){

        KycGraphqlException ex = Assertions.assertThrows(KycGraphqlException.class,()->{

            RequestData<Void> req = RequestData.<Void>builder()
                    .pathParams(Collections.singletonMap("id",1))
                    .build();

            given(customerRepository.getCustomerById(1)).willReturn(null);

            customerService.deleteCustomer(req);
        });
        Assertions.assertEquals(ErrorType.BAD_REQUEST,ex.getErrorType());
    }

    @Test
    public void deleteCustomer_errorDeletingCustomer_throwsKycException(){

        KycGraphqlException ex = Assertions.assertThrows(KycGraphqlException.class,()->{

            RequestData<Void> req = RequestData.<Void>builder()
                    .pathParams(Collections.singletonMap("id",1))
                    .build();

            given(customerRepository.getCustomerById(1)).willReturn(new Customer());
            doThrow(new InvalidDataAccessResourceUsageException("error"))
                    .when(customerStoreProcedure).deleteCustomer(1);

            customerService.deleteCustomer(req);
        });
        Assertions.assertEquals(ErrorType.INTERNAL_ERROR,ex.getErrorType());
    }

}
