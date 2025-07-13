package com.tech.brain.scope;

import com.tech.brain.exception.CommandException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ScopeChecker {

    @Before("@annotation(jwtScope)")
    public void validateScope(JoinPoint joinPoint, JWTScope jwtScope) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken token)) {
            throw new CommandException(new SecurityException("Invalid JWT token"));
        }

        Jwt jwt = token.getToken();

        String serviceName = jwt.getClaimAsString("service-name");
        String scope = jwt.getClaimAsString("scope");

        String required = jwtScope.value(); // from annotation
        String expectedScope = serviceName + "::" + required;

        String actualScope = serviceName + "::" + scope;

        if (!expectedScope.equalsIgnoreCase(actualScope)) {
            throw new CommandException(new SecurityException("JWT does not have required scope: " + expectedScope));
        }
    }
}
