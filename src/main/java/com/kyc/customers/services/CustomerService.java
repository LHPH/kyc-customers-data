package com.kyc.customers.services;

import com.kyc.core.exception.KycGraphqlException;
import com.kyc.core.model.web.MessageData;
import com.kyc.core.model.web.RequestData;
import com.kyc.core.properties.KycMessages;
import com.kyc.customers.mappers.CustomerMapper;
import com.kyc.customers.model.graphql.input.CustomerFilter;
import com.kyc.customers.model.graphql.input.CustomerInput;
import com.kyc.customers.model.graphql.types.Customer;
import com.kyc.customers.repositories.jdbc.CustomerRepository;
import com.kyc.customers.repositories.stores.CustomerStoreProcedure;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kyc.customers.constants.AppConstants.MSG_CODE_001;
import static com.kyc.customers.constants.AppConstants.MSG_CODE_002;
import static com.kyc.customers.constants.AppConstants.MSG_CODE_003;

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
            Customer customer = customerMapper.toCustomerType(input);
            LOGGER.info("Verifying if the customer data is not already registered");
            int id = customerStoreProcedure.getIdCustomer(customer);

            if(id==0){

                LOGGER.info("The customer data is not registered");
                customerStoreProcedure.saveCustomer(input);
                LOGGER.info("The customer data was registered");
                int newId = customerStoreProcedure.getIdCustomer(customer);
                LOGGER.info("The new customer is {}",newId);
                return newId;
            }
            LOGGER.warn("The customer data is already registered, matches with {}",id);
            MessageData messageData = kycMessages.getMessage(MSG_CODE_003);

            throw KycGraphqlException.builderGraphqlException()
                    .inputData(req)
                    .errorData(messageData)
                    .errorType(ErrorType.BAD_REQUEST)
                    .build();
        }
        catch(DataAccessException ex){
            MessageData messageData = kycMessages.getMessage(MSG_CODE_002);

            throw KycGraphqlException.builderGraphqlException()
                    .inputData(req)
                    .exception(ex)
                    .errorData(messageData)
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .build();
        }
    }

    public Customer updateCustomer(RequestData<CustomerInput> req){

        try{

            CustomerInput input = req.getBody();
            String param = ObjectUtils.toString(req.getPathParams().get("id"),()-> null);
            Integer id = NumberUtils.toInt(param);

            LOGGER.info("Verifying if the customer {} exists",id);
            Customer customer = customerRepository.getCustomerById(id);
            if(customer!=null){

                LOGGER.info("Updating the customer data with id {}",id);
                customerStoreProcedure.updateCustomer(id,input);
                LOGGER.info("The customer data with id {} was updated",id);
                return customerRepository.getCustomerById(id);
            }

            LOGGER.warn("The customer data with id {} is not registered",id);
            MessageData messageData = kycMessages.getMessage(MSG_CODE_001);
            throw KycGraphqlException.builderGraphqlException()
                    .inputData(req)
                    .errorData(messageData)
                    .errorType(ErrorType.BAD_REQUEST)
                    .build();
        }
        catch(DataAccessException ex){

            MessageData messageData = kycMessages.getMessage(MSG_CODE_002);

            throw KycGraphqlException.builderGraphqlException()
                    .inputData(req)
                    .exception(ex)
                    .errorData(messageData)
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .build();
        }
    }

    public Boolean deleteCustomer(RequestData<Void> req){

        try{
            String param = ObjectUtils.toString(req.getPathParams().get("id"),()-> null);
            Integer id = NumberUtils.toInt(param);

            LOGGER.info("Verifying if the customer data exists for {}",id);
            Customer customer = customerRepository.getCustomerById(id);
            if(customer!=null){

                LOGGER.info("Deleting the customer data with id {}",id);
                customerStoreProcedure.deleteCustomer(id);
                boolean result = customerRepository.getCustomerById(id) == null;
                LOGGER.info("The customer data for {} was deleted {}",id,result);
                return result;
            }

            LOGGER.warn("The customer data is not registered");
            MessageData messageData = kycMessages.getMessage(MSG_CODE_001);
            throw KycGraphqlException.builderGraphqlException()
                    .inputData(req)
                    .errorData(messageData)
                    .errorType(ErrorType.BAD_REQUEST)
                    .build();
        }
        catch(DataAccessException ex){

            MessageData messageData = kycMessages.getMessage(MSG_CODE_002);

            throw KycGraphqlException.builderGraphqlException()
                    .inputData(req)
                    .exception(ex)
                    .errorData(messageData)
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .build();
        }
    }
}
