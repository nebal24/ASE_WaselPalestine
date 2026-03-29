package com.wasel.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * Configuration class for HTTP client and JSON processing
 * Provides beans for making external API calls
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates a RestTemplate bean with custom timeout and UTF-8 support
     * Used for making HTTP requests to external APIs
     *
     * Only defines the bean if no other RestTemplate bean is present to avoid conflicts.
     *
     * @return Configured RestTemplate instance
     */
    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(15000);

        RestTemplate restTemplate = new RestTemplate(factory);

        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        return restTemplate;
    }

    /**
     * Creates an ObjectMapper bean for JSON parsing
     * Used to parse responses from external APIs
     *
     * @return ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}