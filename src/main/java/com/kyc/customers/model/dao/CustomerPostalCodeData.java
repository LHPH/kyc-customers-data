package com.kyc.customers.model.dao;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerPostalCodeData {

    private String postalCode;
    private Integer idNeighborhood;
    private String neighborhood;
    private Integer idCity;
    private String city;
    private Integer idState;
    private String state;
}
