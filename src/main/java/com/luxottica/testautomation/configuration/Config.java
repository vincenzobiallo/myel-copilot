package com.luxottica.testautomation.configuration;

import com.luxottica.testautomation.utils.InjectionUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class Config {

    @Value("${myel.baseUrl}")
    private String baseUrl;

    @Bean
    public static InjectionUtil injectionUtil() {
        return new InjectionUtil();
    }
}
