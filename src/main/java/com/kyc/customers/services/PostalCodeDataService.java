package com.kyc.customers.services;

import com.kyc.core.exception.KycGraphqlException;
import com.kyc.core.model.MessageData;
import com.kyc.core.model.web.ResponseData;
import com.kyc.core.properties.KycMessages;
import com.kyc.customers.model.dao.CustomerPostalCodeData;
import com.kyc.customers.model.graphql.types.CustomerAddress;
import com.kyc.customers.rest.feign.CatalogsRestService;
import com.kyc.customers.rest.model.GetPostalCodeDataResponse;
import com.kyc.customers.rest.model.NeighborhoodData;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Service;

import static com.kyc.customers.constants.AppConstants.MSG_CODE_002;

@Service
public class PostalCodeDataService {

    private final static Logger LOGGER = LoggerFactory.getLogger(PostalCodeDataService.class);

    @Autowired
    private CatalogsRestService catalogsRestService;

    @Autowired
    private KycMessages kycMessages;

    public CustomerPostalCodeData getCustomerPostalCodeData(CustomerAddress customerAddress){

        try{
            String postalCode = customerAddress.getPostalCode();
            Integer idNeighborhood = customerAddress.getIdNeighborhood();
            LOGGER.info("Retrieving data of postal code {}",postalCode);

            ResponseData<GetPostalCodeDataResponse> response = catalogsRestService.getPostalCode(postalCode);
            GetPostalCodeDataResponse result = response.getData();

            CustomerPostalCodeData data = new CustomerPostalCodeData();
            data.setPostalCode(postalCode);
            data.setIdNeighborhood(idNeighborhood);
            data.setIdCity(result.getIdCity());
            data.setCity(result.getCity());
            data.setIdState(result.getIdState());
            data.setState(result.getState());

            for(NeighborhoodData neighborhoodData : result.getNeighborhoods()){

                if(idNeighborhood.equals(neighborhoodData.getIdNeighborhood())){
                    data.setNeighborhood(neighborhoodData.getNeighborhood());
                    break;
                }
            }
            return data;
        }
        catch(FeignException feignException){

            MessageData messageData = kycMessages.getMessage(MSG_CODE_002);

            throw KycGraphqlException.builderGraphqlException()
                    .inputData(customerAddress)
                    .exception(feignException)
                    .errorData(messageData)
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .build();
        }
    }
}
