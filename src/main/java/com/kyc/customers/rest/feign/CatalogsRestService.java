package com.kyc.customers.rest.feign;

import com.kyc.core.model.web.ResponseData;
import com.kyc.customers.rest.model.GetPostalCodeDataResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "KYC-CATALOGS")
public interface CatalogsRestService {

    @GetMapping("/catalogs/single/kyc-postal-codes/{postal-code}")
    ResponseData<GetPostalCodeDataResponse> getPostalCode(@PathVariable("postal-code") String postalCode);
}
