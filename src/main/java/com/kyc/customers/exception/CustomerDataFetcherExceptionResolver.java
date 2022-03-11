package com.kyc.customers.exception;

import com.kyc.core.exception.KycRestException;
import com.kyc.core.model.web.MessageData;
import com.kyc.core.properties.KycMessages;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpStatus;
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
        HttpStatus httpStatus;

        if(exception instanceof KycRestException){

            KycRestException ex = (KycRestException)exception;
            LOGGER.error("{}",exception.toString());
            messageData = ex.getErrorData();
            httpStatus = ex.getStatus();
        }
        else if(exception instanceof ConstraintViolationException){

            LOGGER.error(" ",exception);
            messageData = kycMessages.getMessage(MSG_CODE_001);
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        else{

            LOGGER.error(" ",exception);
            messageData = kycMessages.getMessage(MSG_CODE_000);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        Map<String, Object> extensions = new HashMap<>();
        extensions.put("code",messageData.getCode());
        extensions.put("time",messageData.getTime());

        error = GraphqlErrorBuilder.newError(environment)
                .message(messageData.getMessage())
                .extensions(extensions)
                .errorType(getErrorType(httpStatus))
                .build();

        return Mono.just(Collections.singletonList(error));
    }

    private ErrorType getErrorType(HttpStatus httpStatus){

        switch (httpStatus){
            case UNAUTHORIZED:
                return ErrorType.UNAUTHORIZED;
            case BAD_REQUEST:
            case UNPROCESSABLE_ENTITY:
                return ErrorType.BAD_REQUEST;
            default:
                return ErrorType.INTERNAL_ERROR;
        }
    }
}
