package com.kyc.customers.repositories.mappers;

import com.kyc.customers.model.graphql.types.Customer;
import com.kyc.customers.model.graphql.types.CustomerAddress;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(ResultSet resultSet, int i) throws SQLException {

        Customer customer = new Customer();
        customer.setId(resultSet.getInt("ID"));
        customer.setFirstName(resultSet.getString("FIRST_NAME"));
        customer.setSecondName(resultSet.getString("SECOND_NAME"));
        customer.setLastName(resultSet.getString("LAST_NAME"));
        customer.setSecondLastName(resultSet.getString("SECOND_LAST_NAME"));
        customer.setRfc(resultSet.getString("RFC"));
        customer.setAge(resultSet.getString("AGE"));
        customer.setCellPhone(resultSet.getString("CELL_PHONE"));
        customer.setHomePhone(resultSet.getString("HOME_PHONE"));
        customer.setActive(resultSet.getBoolean("ACTIVE"));
        customer.setEmail(resultSet.getString("EMAIL"));

        CustomerAddress address = new CustomerAddress();
        address.setStreet(resultSet.getString("STREET"));
        address.setStreetNumber(resultSet.getString("STREET_NUMBER"));
        address.setNeighbourhood(resultSet.getString("NEIGHBOURHOOD"));
        address.setIdState(resultSet.getInt("ID_STATE"));

        customer.setAddress(address);

        return customer;
    }
}