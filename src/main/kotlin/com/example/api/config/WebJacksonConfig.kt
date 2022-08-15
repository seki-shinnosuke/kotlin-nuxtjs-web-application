package com.example.api.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@Configuration
class WebJacksonConfig {
    @Bean
    fun mappingJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter {
        val converter = MappingJackson2HttpMessageConverter()
        converter.objectMapper = apiDefaultObjectMapper()
        return converter
    }

    @Bean
    @Scope("prototype")
    fun apiDefaultObjectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.registerKotlinModule()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        return mapper
    }
}