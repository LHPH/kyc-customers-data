package com.kyc.customers.repositories.stores;

import com.kyc.core.factory.SimpleJdbcCallFactory;
import com.kyc.customers.model.graphql.input.CustomerAddressInput;
import com.kyc.customers.model.graphql.input.CustomerInput;
import com.kyc.customers.model.graphql.types.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CustomerStoreProcedureTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Mock(name = "queriesProps")
    private Properties queriesProps;

    @Mock
    private SimpleJdbcCall simpleJdbcCall;

    @Mock
    private SimpleJdbcCallFactory simpleJdbcCallFactory;

    @InjectMocks
    private CustomerStoreProcedure customerStoreProcedure;

    @BeforeAll
    public static void setUp(){

        MockitoAnnotations.openMocks(new CustomerStoreProcedureTest());
    }

    @BeforeEach
    public void initMocks(){

        given(simpleJdbcCallFactory.getSimpleJdbcCall(any(),anyBoolean())).willReturn(simpleJdbcCall);
        customerStoreProcedure = new CustomerStoreProcedure(jdbcTemplate,namedParameterJdbcTemplate,queriesProps,simpleJdbcCallFactory);
    }

    @Test
    public void saveCustomer_savingCustomer_success(){

        CustomerInput customerInput = new CustomerInput();
        customerInput.setAddress(new CustomerAddressInput());

        customerStoreProcedure.saveCustomer(customerInput);
        verify(simpleJdbcCall,times(1)).execute(any(SqlParameterSource.class));
    }

    @Test
    public void updateCustomer_updatingCustomer_success(){

        CustomerInput customerInput = new CustomerInput();
        customerInput.setAddress(new CustomerAddressInput());

        given(queriesProps.get("SP_KYC_PROCESS_CUSTOMER")).willReturn("SQL");

        customerStoreProcedure.updateCustomer(1,customerInput);
        verify(namedParameterJdbcTemplate,times(1)).update(anyString(),any(SqlParameterSource.class));
    }

    @Test
    public void deleteCustomer_deletingCustomer_success(){

        customerStoreProcedure.deleteCustomer(1);
        verify(simpleJdbcCall,times(1)).execute(any(SqlParameterSource.class));
    }

    @Test
    public void getIdCustomer_retrievingId_retrievedId(){

        Customer customer = new Customer();
        customer.setFirstName("TEST");
        customer.setSecondName("TEST");
        customer.setLastName("TEST");
        customer.setSecondLastName(null);
        customer.setRfc("TEST");

        given(queriesProps.get("SP_KYC_GET_ID_CUSTOMER")).willReturn("SQL");
        given(jdbcTemplate.queryForObject("SQL",Integer.class,"TEST","TEST","TEST",null,"TEST"))
                .willReturn(1);
        int id = customerStoreProcedure.getIdCustomer(customer);
        Assertions.assertEquals(1,id);
    }


}
