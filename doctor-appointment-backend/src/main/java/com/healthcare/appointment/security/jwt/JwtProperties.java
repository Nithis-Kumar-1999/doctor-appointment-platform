package com.healthcare.appointment.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Maps the {@code application.jwt} properties from the YAML configuration
 * files to a strongly typed Java object.
 *
 * <p>By using {@code @ConfigurationProperties}, we avoid scattering
 * {@code @Value} annotations throughout the codebase. This centralizes
 * configuration management and allows Spring to perform property binding.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.jwt")
public class JwtProperties {

    /**
     * The secret key used to sign the JWT. Must be a base64 encoded string
     * of at least 256 bits (32 bytes) for HS256 algorithm.
     */
    private String secretKey;

    /**
     * The lifespan of an Access Token in milliseconds.
     */
    private long expirationMs;

    /**
     * The lifespan of a Refresh Token in milliseconds.
     * (Placeholder for future refresh token implementation).
     */
    private long refreshTokenExpirationMs;
}
