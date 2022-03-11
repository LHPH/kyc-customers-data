package com.kyc.customers.model.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CustomerInput {

    @NotNull
    @Pattern(regexp ="^[\\p{IsLatin}\\s]{3,50}$")
    private String firstName;

    @Pattern(regexp ="^[\\p{IsLatin}\\s]{3,50}$")
    private String secondName;

    @NotNull
    @Pattern(regexp ="^[\\p{IsLatin}\\s]{3,50}$")
    private String lastName;

    @Pattern(regexp ="^[\\p{IsLatin}\\s]{3,50}$")
    private String secondLastName;

    @NotNull
    @Pattern(regexp ="^[1-9]([\\d])?$")
    private String age;

    @NotNull
    @Pattern(regexp ="^[A-Z]{4}[\\d]{6}([A-Z\\d])?$")
    private String rfc;

    @Pattern(regexp ="^[\\d]{10}$")
    private String homePhone;

    @NotNull
    @Pattern(regexp ="^[\\d]{10}$")
    private String cellPhone;

    @NotNull
    @Email
    private String email;

    @NotNull
    private Boolean active;

    @NotNull
    @Valid
    private CustomerAddressInput address;
}
