package com.kyc.customers.config;

import com.kyc.core.config.LoadSimpleSqlConfig;
import com.kyc.core.config.DebugInputRequestConfig;
import com.kyc.core.factory.SimpleJdbcCallFactory;
import com.kyc.core.properties.KycMessages;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Import(value = {LoadSimpleSqlConfig.class, KycMessages.class})
public class GeneralConfig {

    @Bean
    public SimpleJdbcCallFactory simpleJdbcCallFactory(JdbcTemplate jdbcTemplate){
        return new SimpleJdbcCallFactory(jdbcTemplate);
    }
}
