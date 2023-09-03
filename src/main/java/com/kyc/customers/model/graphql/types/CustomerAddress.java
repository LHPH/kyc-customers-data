package com.kyc.customers.model.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CustomerAddress {

    private String street;
    private String streetNumber;
    private String postalCode;
    private Integer idNeighborhood;
    private String neighbourhood;
    private Integer idCity;
    private String city;
    private Integer idState;
    private String state;
}
