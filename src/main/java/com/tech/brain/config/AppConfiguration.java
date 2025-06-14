package com.tech.brain.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 1. Don't fail on unknown fields in incoming JSON
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 2. Pretty print JSON output
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // 3. Include non-null fields only
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 4. Support Java 8 date/time types (LocalDate, LocalDateTime)
        mapper.registerModule(new JavaTimeModule());
        // 5. Prevent timestamps for dates (write ISO strings instead)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 6. Optional: Change naming strategy (e.g., camelCase → snake_case) user_name maps to userName (because of SNAKE_CASE).
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return mapper;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Product Command System")
                        .description("API for product create, update, delete and publish event to downstream")
                        .version("1.0"));
    }
}
