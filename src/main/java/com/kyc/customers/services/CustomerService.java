package com.kyc.customers.services;

import com.kyc.core.exception.KycRestException;
import com.kyc.core.model.web.MessageData;
import com.kyc.core.model.web.RequestData;
import com.kyc.core.properties.KycMessages;
import com.kyc.customers.mappers.CustomerMapper;
import com.kyc.customers.model.graphql.input.CustomerInput;
import com.kyc.customers.model.graphql.types.Customer;
import com.kyc.customers.model.graphql.input.CustomerFilter;
import com.kyc.customers.repositories.jdbc.CustomerRepository;
import com.kyc.customers.repositories.stores.CustomerStoreProcedure;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.List;

import static com.kyc.customers.constants.AppConstants.MSG_CODE_001;
import static com.kyc.customers.constants.AppConstants.MSG_CODE_002;

@Service
public class CustomerService {

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerStoreProcedure customerStoreProcedure;

    @Autowired
    private KycMessages kycMessages;

    public List<Customer> getAllCustomers(RequestData<Void> req){

        List<Customer> customers = customerRepository.getAllCustomers();
        LOGGER.info("Retrieving {} customers",customers.size());
        return customers;
    }

    public Customer getCustomerByFilter(RequestData<CustomerFilter> req){

        CustomerFilter filter = req.getBody();

        if(filter.getId()!=null){
            LOGGER.info("Retrieving customer by id {}",filter.getId());
            return customerRepository.getCustomerById(filter.getId());
        }
        else if(filter.getRfc()!=null){
            LOGGER.info("Retrieving customer by RFC {}",filter.getRfc());
            return customerRepository.getCustomerByRfc(filter.getRfc());
        }
        else if(filter.getName()!=null){
            LOGGER.info("Retrieving customer by name");
            return customerRepository.getCustomerByNames(filter.getName());
        }
        LOGGER.info("The criteria is not valid, return empty result");
        return new Customer();
    }

    public Integer createCustomer(RequestData<CustomerInput> req){

        try{
            CustomerInput input = req.getBody();
            LOGGER.info("{}",input);
            Customer customer = customerMapper.toCustomerType(input);
            customerStoreProcedure.saveCustomer(input);
            return customerStoreProcedure.getIdCustomer(customer);
        }
        catch(DataAccessException ex){
            MessageData messageData = kycMessages.getMessage(MSG_CODE_002);

            throw KycRestException.builder()
                    .inputData(req)
                    .exception(ex)
                    .errorData(messageData)
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .build();
        }
    }

    public Customer updateCustomer(RequestData<CustomerInput> req){

        try{
            CustomerInput input = req.getBody();
            String param = ObjectUtils.toString(req.getPathParams().get("id"),()-> null);
            Integer id = NumberUtils.toInt(param);

            Customer customer = customerRepository.getCustomerById(id);
            if(customer!=null){
                customerStoreProcedure.updateCustomer(id,input);
                return customerRepository.getCustomerById(id);
            }

            MessageData messageData = kycMessages.getMessage(MSG_CODE_001);
            throw KycRestException.builder()
                    .inputData(req)
                    .errorData(messageData)
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        catch(DataAccessException ex){

            MessageData messageData = kycMessages.getMessage(MSG_CODE_002);

            throw KycRestException.builder()
                    .inputData(req)
                    .exception(ex)
                    .errorData(messageData)
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .build();
        }
    }

    public Boolean deleteCustomer(RequestData<Void> req){

        try{
            String param = ObjectUtils.toString(req.getPathParams().get("id"),()-> null);
            Integer id = NumberUtils.toInt(param);

            Customer customer = customerRepository.getCustomerById(id);
            if(customer!=null){

                customerStoreProcedure.deleteCustomer(id);
                return customerRepository.getCustomerById(id) == null;
            }

            MessageData messageData = kycMessages.getMessage(MSG_CODE_001);
            throw KycRestException.builder()
                    .inputData(req)
                    .errorData(messageData)
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        catch(DataAccessException ex){

            MessageData messageData = kycMessages.getMessage(MSG_CODE_002);

            throw KycRestException.builder()
                    .inputData(req)
                    .exception(ex)
                    .errorData(messageData)
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .build();
        }
    }
}
