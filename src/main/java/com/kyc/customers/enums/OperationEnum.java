package com.kyc.customers.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OperationEnum {

    INSERT(0),UPDATE(1),DELETE(2);

    private final Integer operation;

}