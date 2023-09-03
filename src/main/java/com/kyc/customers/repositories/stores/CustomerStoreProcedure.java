package com.kyc.customers.repositories.stores;

import com.kyc.core.factory.SimpleJdbcCallFactory;
import com.kyc.core.model.config.SimpleJdbcCallParams;
import com.kyc.customers.enums.OperationEnum;
import com.kyc.customers.model.graphql.input.CustomerAddressInput;
import com.kyc.customers.model.graphql.input.CustomerInput;
import com.kyc.customers.model.graphql.types.Customer;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.Properties;

@Repository
public class CustomerStoreProcedure {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerStoreProcedure.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final Properties queriesProps;

    private final SimpleJdbcCall simpleJdbcCall;

    @Autowired
    public CustomerStoreProcedure(JdbcTemplate jdbcTemplate,
                                  NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                  @Qualifier("queriesProps") Properties queriesProps,
                                  SimpleJdbcCallFactory factory){

        SimpleJdbcCallParams params = SimpleJdbcCallParams.builder()
                .procedureName("SP_KYC_PROCESS_CUSTOMER")
                .build();

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.queriesProps = queriesProps;
        simpleJdbcCall = factory.getSimpleJdbcCall(params,false);
    }

    public void saveCustomer(CustomerInput customer){

        LOGGER.info("Ejecutando SP con Operacion {}", OperationEnum.INSERT);
        SqlParameterSource params = getParametersUp(0,customer,OperationEnum.INSERT);
        simpleJdbcCall.execute(params);
        LOGGER.info("Se termino de ejecutar SP con Operacion {}",OperationEnum.INSERT);
    }

    public void updateCustomer(Integer id, CustomerInput customer){

        LOGGER.info("Ejecutando SP con Operacion {}",OperationEnum.UPDATE);
        SqlParameterSource params = getParametersUp(id,customer,OperationEnum.UPDATE);

        String sql = queriesProps.get("SP_KYC_PROCESS_CUSTOMER").toString();
        namedParameterJdbcTemplate.update(sql,params);
        //simpleJdbcCall.execute(params);
        LOGGER.info("Se termino de ejecutar SP con Operacion {}",OperationEnum.UPDATE);
    }

    public void deleteCustomer(Integer id){

        LOGGER.info("Ejecutando SP con Operacion {}",OperationEnum.DELETE);
        SqlParameterSource params = getParametersUp(id,OperationEnum.DELETE);
        simpleJdbcCall.execute(params);
        LOGGER.info("Se termino de ejecutar SP con Operacion {}",OperationEnum.DELETE);
    }

    public int getIdCustomer(Customer customer){

        String sql = queriesProps.get("SP_KYC_GET_ID_CUSTOMER").toString();
        Object [] obj = new Object[]{customer.getFirstName(),customer.getSecondName(),
                customer.getLastName(),customer.getSecondLastName(),customer.getRfc()};

        return ObjectUtils
                .defaultIfNull(jdbcTemplate.queryForObject(sql,Integer.class,obj),0);
    }

    private SqlParameterSource getParametersUp(Integer id, OperationEnum operation){
        CustomerInput customer = new CustomerInput();
        customer.setAddress(new CustomerAddressInput());
        return getParametersUp(id,customer,operation);
    }

    private SqlParameterSource getParametersUp(Integer id,CustomerInput customer, OperationEnum operation){

        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("OPERATION",operation.getOperation())
                .addValue("P_ID_CUSTOMER",id)
                .addValue("P_FIRST_NAME",customer.getFirstName())
                .addValue("P_SECOND_NAME",customer.getSecondName())
                .addValue("P_LAST_NAME",customer.getLastName())
                .addValue("P_SECOND_LAST_NAME",customer.getSecondLastName())
                .addValue("P_RFC",customer.getRfc())
                .addValue("P_AGE",customer.getAge())
                .addValue("P_HOME_PHONE",customer.getHomePhone())
                .addValue("P_CELL_PHONE",customer.getCellPhone())
                .addValue("P_EMAIL",customer.getEmail())
                .addValue("P_ACTIVE",determinateActive(customer.getActive(),operation))
                .addValue("P_STREET",customer.getAddress().getStreet())
                .addValue("P_STREET_NUMBER",customer.getAddress().getStreetNumber())
                .addValue("P_POSTAL_CODE",customer.getAddress().getPostalCode())
                .addValue("P_ID_NEIGHBOURHOOD",customer.getAddress().getIdNeighborhood());

        return in;
    }

    private Boolean determinateActive(Boolean value,OperationEnum operation){

        switch(operation){
            case INSERT:
                return Boolean.TRUE;
            case DELETE:
                return Boolean.FALSE;
            default:
                return value;
        }

    }
}
