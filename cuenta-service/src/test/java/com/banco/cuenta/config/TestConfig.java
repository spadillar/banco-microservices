package com.banco.cuenta.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de prueba para deshabilitar Resilience4j
 * 
 * Esta configuración excluye explícitamente las clases de autoconfiguración de Resilience4j
 * para evitar errores durante las pruebas cuando no se necesita esta funcionalidad.
 */
@Configuration
@EnableAutoConfiguration(exclude = {
    Resilience4JAutoConfiguration.class,
    SecurityAutoConfiguration.class,
    OAuth2ClientAutoConfiguration.class,
    SecurityFilterAutoConfiguration.class,
    UserDetailsServiceAutoConfiguration.class,
    OAuth2ResourceServerAutoConfiguration.class
})
public class TestConfig {
    // No se necesitan beans adicionales, solo la exclusión de la configuración automática
}
