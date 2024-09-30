package com.luxottica.testautomation.configuration;

import com.luxottica.testautomation.utils.InjectionUtil;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class Config {

    @Value("${myel.baseUrl}")
    private String baseUrl;
    @Value("${myel.endpoints.sessionUrl}")
    private String sessionUrl;
    @Value("${myel.endpoints.personificationUrl}")
    private String personificationUrl;
    @Value("${myel.endpoints.precart}")
    private String precart;
    @Value("${myel.endpoints.doors}")
    private String doors;
    @Value("${myel.endpoints.usercontext}")
    private String userContext;
    @Value("${myel.endpoints.facets}")
    private String facets;

    @Bean
    public static InjectionUtil injectionUtil() {
        return new InjectionUtil();
    }
}
