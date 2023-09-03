package com.kyc.customers.mappers;

import com.kyc.customers.model.graphql.input.CustomerAddressInput;
import com.kyc.customers.model.graphql.input.CustomerInput;
import com.kyc.customers.model.graphql.types.Customer;
import com.kyc.customers.model.graphql.types.CustomerAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CustomerMapper {


    @Mappings({
            @Mapping(target ="firstName" ,source = "source.firstName"),
            @Mapping(target ="secondName" ,source = "source.secondName"),
            @Mapping(target ="lastName" ,source = "source.lastName"),
            @Mapping(target ="secondLastName" ,source = "source.secondLastName"),
            @Mapping(target ="age" ,source = "source.age"),
            @Mapping(target ="rfc" ,source = "source.rfc"),
            @Mapping(target ="homePhone" ,source = "source.homePhone"),
            @Mapping(target ="email" ,source = "source.email"),
            @Mapping(target ="active" ,source = "source.active"),
            @Mapping(target ="address" ,source = "source.address",qualifiedByName = "toCustomerAddressType")
    })
    Customer toCustomerType(CustomerInput source);


    @Named("toCustomerAddressType")
    @Mappings({
            @Mapping(target ="street" ,source = "source.street"),
            @Mapping(target ="streetNumber" ,source = "source.streetNumber"),
            @Mapping(target ="postalCode" ,source = "source.postalCode"),
            @Mapping(target ="idNeighborhood" ,source = "source.idNeighborhood")
    })
    CustomerAddress toCustomerAddressType(CustomerAddressInput source);
}
