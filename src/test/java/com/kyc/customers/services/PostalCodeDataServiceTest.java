package com.kyc.customers.services;

import com.kyc.core.exception.KycGraphqlException;
import com.kyc.core.model.web.ResponseData;
import com.kyc.core.properties.KycMessages;
import com.kyc.customers.model.dao.CustomerPostalCodeData;
import com.kyc.customers.model.graphql.types.CustomerAddress;
import com.kyc.customers.rest.feign.CatalogsRestService;
import com.kyc.customers.rest.model.GetPostalCodeDataResponse;
import com.kyc.customers.rest.model.NeighborhoodData;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PostalCodeDataServiceTest {

    @Mock
    private CatalogsRestService catalogsRestService;

    @Mock
    private KycMessages kycMessages;

    @InjectMocks
    private PostalCodeDataService postalCodeDataService;

    private GetPostalCodeDataResponse resultService;

    @BeforeEach
    public void setUp(){

        resultService = new GetPostalCodeDataResponse();
        resultService.setPostalCode("01000");
        resultService.setIdCity(1);
        resultService.setCity("CITY");
        resultService.setIdState(2);
        resultService.setState("STATE");

        NeighborhoodData n1 = new NeighborhoodData();
        NeighborhoodData n2 = new NeighborhoodData();

        n1.setIdNeighborhood(50);
        n1.setNeighborhood("NEIGHBORHOOD 50");
        n2.setIdNeighborhood(1);
        n2.setNeighborhood("NEIGHBORHOOD 1");

        resultService.setNeighborhoods(Arrays.asList(n1,n2));
    }

    @Test
    public void getCustomerPostalCodeData_getPostalData_returnPostalData(){

        CustomerAddress customerAddress = new CustomerAddress();
        customerAddress.setPostalCode("01000");
        customerAddress.setIdNeighborhood(1);

        given(catalogsRestService.getPostalCode(("01000")))
                .willReturn(ResponseData.of(resultService));

        CustomerPostalCodeData result = postalCodeDataService.getCustomerPostalCodeData(customerAddress);

        Assertions.assertEquals(resultService.getIdCity(),result.getIdCity());
        Assertions.assertEquals(resultService.getCity(),result.getCity());
        Assertions.assertEquals(resultService.getIdState(),result.getIdState());
        Assertions.assertEquals(resultService.getState(),result.getState());
        Assertions.assertEquals(resultService.getNeighborhoods().get(1).getNeighborhood(),result.getNeighborhood());

    }

    @Test
    public void getCustomerPostalCodeData_errorInService_throwException(){

        KycGraphqlException ex = Assertions.assertThrows(KycGraphqlException.class,()->{

            CustomerAddress customerAddress = new CustomerAddress();
            customerAddress.setPostalCode("01000");
            customerAddress.setIdNeighborhood(1);

            Response response = Response.builder()
                    .reason("fail")
                    .request(Request.create(Request.HttpMethod.GET,"http://localhost",new HashMap<>(),null, StandardCharsets.UTF_8,null))
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();

            FeignException feignException = FeignException.errorStatus("test",response);

            given(catalogsRestService.getPostalCode(("01000")))
                    .willThrow(feignException);

            postalCodeDataService.getCustomerPostalCodeData(customerAddress);
        });
        Assertions.assertEquals(ErrorType.INTERNAL_ERROR,ex.getErrorType());
    }
}
