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
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
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

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("https://auth.tiwarytech.site/api/auth/.well-known/jwks.json")
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> customJwtAuthenticationConverter() {
        return jwt -> {
            String serviceName = jwt.getClaimAsString("service-name");
            String scope = jwt.getClaimAsString("scope");

            List<GrantedAuthority> authorities = new ArrayList<>();

            // Add "serviceName::scope" authority, e.g., "invoice-generator::create"
            if (serviceName != null && scope != null) {
                authorities.add(new SimpleGrantedAuthority(serviceName + "::" + scope));
            }

            // Also map roles (if needed)
            List<String> roles = jwt.getClaimAsStringList("authorities");
            if (roles != null) {
                for (String role : roles) {
                    authorities.add(new SimpleGrantedAuthority(role));
                }
            }

            return new JwtAuthenticationToken(jwt, authorities);
        };
    }
}
