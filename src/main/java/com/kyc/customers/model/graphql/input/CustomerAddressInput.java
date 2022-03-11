package com.kyc.customers.model.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
    @Pattern(regexp ="^[\\dA-Z\\s]{2,5}$")
    private String streetNumber;

    @NotNull
    @Pattern(regexp ="^[\\d]{10}$")
    private String neighbourhood;

    @NotNull
    @Min(1)@Max(32)
    private Integer idState;
}
