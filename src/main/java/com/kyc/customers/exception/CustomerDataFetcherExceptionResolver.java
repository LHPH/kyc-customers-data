package com.kyc.customers.exception;

import com.kyc.core.exception.KycGraphqlException;
import com.kyc.core.model.web.MessageData;
import com.kyc.core.properties.KycMessages;
import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kyc.customers.constants.AppConstants.MSG_CODE_000;
import static com.kyc.customers.constants.AppConstants.MSG_CODE_001;

@Component
public class CustomerDataFetcherExceptionResolver implements DataFetcherExceptionResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerDataFetcherExceptionResolver.class);

    @Autowired
    private KycMessages kycMessages;

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable exception, DataFetchingEnvironment environment) {

        GraphQLError error;
        MessageData messageData;
        String message;
        ErrorClassification errorType;
        Map<String, Object> extensions;

        if(exception instanceof KycGraphqlException){

            KycGraphqlException ex = (KycGraphqlException)exception;
            LOGGER.error("{}",ex.printError());
            message = ex.getErrorData().getMessage();
            errorType = ex.getErrorType();
            extensions = ex.getExtensions();

        }
        else if(exception instanceof ConstraintViolationException){

            ConstraintViolationException ex = (ConstraintViolationException)exception;
            LOGGER.error(" ",exception);
            messageData = kycMessages.getMessage(MSG_CODE_001);
            errorType = ErrorType.BAD_REQUEST;
            message = ex.getMessage();
            extensions = setExtensions(messageData);
        }
        else{

            LOGGER.error(" ",exception);
            messageData = kycMessages.getMessage(MSG_CODE_000);
            message = exception.getMessage();
            errorType = ErrorType.INTERNAL_ERROR;
            extensions = setExtensions(messageData);
        }

        error = GraphqlErrorBuilder.newError(environment)
                .message(message)
                .extensions(extensions)
                .errorType(errorType)
                .build();

        return Mono.just(Collections.singletonList(error));
    }

    private HashMap<String,Object> setExtensions(MessageData messageData){

        HashMap<String,Object> extensions = new HashMap<>();
        if(messageData!=null){

            extensions.put("code",messageData.getCode());
            extensions.put("description",messageData.getMessage());
            extensions.put("time",messageData.getTime());
        }
        return extensions;
    }
}
