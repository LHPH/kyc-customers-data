package com.kyc.customers.exception;

import com.kyc.core.enums.MessageType;
import com.kyc.core.exception.KycGraphqlException;
import com.kyc.core.model.web.MessageData;
import com.kyc.core.properties.KycMessages;
import com.kyc.customers.model.graphql.input.CustomerInput;
import graphql.GraphQLError;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.ResultPath;
import graphql.language.Field;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironmentImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.graphql.execution.ErrorType;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import static com.kyc.customers.constants.AppConstants.MSG_CODE_000;
import static com.kyc.customers.constants.AppConstants.MSG_CODE_001;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerDataFetcherExceptionResolverTest {

    @Mock
    private KycMessages kycMessages;

    //Mocks for DataFetchingEnvironmentImpl
    @Mock
    private Field field;

    @Mock
    private ExecutionStepInfo executionStepInfo;

    @Mock
    private DataFetchingEnvironmentImpl environment;
    //

    @InjectMocks
    private CustomerDataFetcherExceptionResolver resolver;


    @BeforeAll
    public static void setUp(){
        MockitoAnnotations.openMocks(new CustomerDataFetcherExceptionResolverTest());
    }

    @Test
    public void resolveException_processKycGraphqlException_returnListOfGraphqlError(){

        when(environment.getField()).thenReturn(field);
        when(field.getSourceLocation()).thenReturn(SourceLocation.EMPTY);
        when(environment.getExecutionStepInfo()).thenReturn(executionStepInfo);
        when(executionStepInfo.getPath()).thenReturn(ResultPath.rootPath());

        KycGraphqlException ex = KycGraphqlException.builderGraphqlException()
                .errorType(ErrorType.INTERNAL_ERROR)
                .errorData(new MessageData("CODE","MESSAGE", MessageType.ERROR))
                .build();

        Mono<List<GraphQLError>> result = resolver.resolveException(ex,environment);

        List<GraphQLError> list = result.block(Duration.ofMillis(100));
        Assertions.assertNotNull(list);
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals("MESSAGE",list.get(0).getMessage());
    }

    @Test
    public void resolveException_processConstraintViolationException_returnListOfGraphqlError(){

        when(environment.getField()).thenReturn(field);
        when(field.getSourceLocation()).thenReturn(SourceLocation.EMPTY);
        when(environment.getExecutionStepInfo()).thenReturn(executionStepInfo);
        when(executionStepInfo.getPath()).thenReturn(ResultPath.rootPath());
        when(kycMessages.getMessage(MSG_CODE_001))
                .thenReturn(new MessageData("CODE","INVALID",MessageType.ERROR));

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        CustomerInput input = new CustomerInput();

        Set<ConstraintViolation<CustomerInput>> violations = validator.validate(input);
        ConstraintViolationException ex = new ConstraintViolationException(violations);

        Mono<List<GraphQLError>> result = resolver.resolveException(ex,environment);

        List<GraphQLError> list = result.block(Duration.ofMillis(100));
        Assertions.assertNotNull(list);
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(ErrorType.BAD_REQUEST,list.get(0).getErrorType());
    }

    @Test
    public void resolveException_processUnknownException_returnListOfGraphqlError(){

        when(environment.getField()).thenReturn(field);
        when(field.getSourceLocation()).thenReturn(SourceLocation.EMPTY);
        when(environment.getExecutionStepInfo()).thenReturn(executionStepInfo);
        when(executionStepInfo.getPath()).thenReturn(ResultPath.rootPath());
        when(kycMessages.getMessage(MSG_CODE_000))
                .thenReturn(new MessageData("CODE","UNKNOWN",MessageType.ERROR));

        NullPointerException ex = new NullPointerException("test null");

        Mono<List<GraphQLError>> result = resolver.resolveException(ex,environment);

        List<GraphQLError> list = result.block(Duration.ofMillis(100));
        Assertions.assertNotNull(list);
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals("test null",list.get(0).getMessage());
    }
}
