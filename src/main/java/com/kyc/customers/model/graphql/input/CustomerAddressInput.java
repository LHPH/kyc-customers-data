package com.kyc.customers.model.graphql.input;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class CustomerAddressInput {

    @NotNull
    @Pattern(regexp ="^[\\p{IsLatin}\\s\\.\\-]{3,20}$")
    private String street;

    @NotNull
    @Pattern(regexp ="^[\\dA-Z\\s]{1,5}$")
    private String streetNumber;

    @NotNull
    @Pattern(regexp ="^[\\d]{5}$")
    private String postalCode;

    @NotNull
    @Min(1)@Max(2000)
    private Integer idNeighborhood;
}
