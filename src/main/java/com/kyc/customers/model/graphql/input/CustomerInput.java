package com.kyc.customers.model.graphql.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
    @Pattern(regexp ="^[A-Z]{4}[\\d]{6}([A-Z\\d]{3})?$")
    private String rfc;

    @Pattern(regexp ="^[\\d]{10}$")
    private String homePhone;

    @NotNull
    @Pattern(regexp ="^[\\d]{10}$")
    private String cellPhone;

    @NotNull
    @Email
    private String email;

    private Boolean active;

    @NotNull
    @Valid
    private CustomerAddressInput address;
}
