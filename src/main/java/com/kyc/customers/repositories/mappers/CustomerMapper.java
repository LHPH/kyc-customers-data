package com.kyc.customers.repositories.mappers;

import com.kyc.customers.model.graphql.types.Customer;
import com.kyc.customers.model.graphql.types.CustomerAddress;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CustomerMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(ResultSet resultSet, int i) throws SQLException {

        Customer customer = new Customer();
        customer.setId(resultSet.getInt("ID"));
        customer.setFirstName(StringUtils.upperCase(resultSet.getString("FIRST_NAME")));
        customer.setSecondName(StringUtils.upperCase(resultSet.getString("SECOND_NAME")));
        customer.setLastName(StringUtils.upperCase(resultSet.getString("LAST_NAME")));
        customer.setSecondLastName(StringUtils.upperCase(resultSet.getString("SECOND_LAST_NAME")));
        customer.setRfc(resultSet.getString("RFC"));
        customer.setAge(resultSet.getString("AGE"));
        customer.setCellPhone(resultSet.getString("CELL_PHONE"));
        customer.setHomePhone(resultSet.getString("HOME_PHONE"));
        customer.setActive(resultSet.getBoolean("ACTIVE"));
        customer.setEmail(resultSet.getString("EMAIL"));

        CustomerAddress address = new CustomerAddress();
        address.setStreet(StringUtils.upperCase(resultSet.getString("STREET")));
        address.setStreetNumber(StringUtils.upperCase(resultSet.getString("STREET_NUMBER")));

        address.setPostalCode(resultSet.getString("POSTAL_CODE"));
        address.setIdNeighborhood(resultSet.getInt("ID_NEIGHBORHOOD"));

        customer.setAddress(address);

        return customer;
    }
}