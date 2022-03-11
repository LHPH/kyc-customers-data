package com.kyc.customers.model.graphql.input;

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
public class CustomerName {

    private String firstName;
    private String secondName;
    private String lastName;
    private String secondLastName;
}
