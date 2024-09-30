package com.luxottica.testautomation.security.config;

import com.luxottica.testautomation.configuration.Config;
import com.luxottica.testautomation.security.BFFClient;
import com.luxottica.testautomation.security.BFFClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Autowired
    private Config config;

    @Bean
    public BFFClient bffResponse() {
        return new BFFClientImpl(config.getBaseUrl());
    }
}
