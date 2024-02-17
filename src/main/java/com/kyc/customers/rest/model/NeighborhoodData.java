package com.kyc.customers.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NeighborhoodData {

    @JsonProperty("id_neighborhood")
    private Integer idNeighborhood;
    @JsonProperty("neighborhood")
    private String neighborhood;
}