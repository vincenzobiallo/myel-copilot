package com.luxottica.testautomation.configuration;

import com.luxottica.testautomation.utils.InjectionUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SearchConfig {

    @Value("${search.baseUrl}")
    private String baseUrl;
    @Value("${search.endpoints.findProductsByCategoryCarousel}")
    private String findProductsByCategoryCarousel;

}