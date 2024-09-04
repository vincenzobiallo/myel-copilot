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
    @Value("${myel.sessionUrl}")
    private String sessionUrl;
    @Value("${myel.personificationUrl}")
    private String personificationUrl;
    @Value("${myel.precart}")
    private String precart;

    @Value("${labels.endpoint}")
    private String labelsEndpoint;
    @Value("${labels.header.key}")
    private String labelsHeaderKey;
    @Value("${labels.header.value}")
    private String labelsHeaderValue;

    @Bean
    public static InjectionUtil injectionUtil() {
        return new InjectionUtil();
    }
}
