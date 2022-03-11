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
public class Customer {

    private Integer id;
    private String firstName;
    private String secondName;
    private String lastName;
    private String secondLastName;
    private String age;
    private String rfc;
    private String homePhone;
    private String cellPhone;
    private String email;
    private Boolean active;
    private CustomerAddress address;

}