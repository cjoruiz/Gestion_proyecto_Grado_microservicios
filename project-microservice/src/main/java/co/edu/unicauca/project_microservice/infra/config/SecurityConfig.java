package co.edu.unicauca.project_microservice.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;
import java.util.stream.Collectors;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:http://keycloak:8080/realms/sistema/protocol/openid-connect/certs}")
    private String jwkSetUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/proyectos").hasRole("DOCENTE")
                        .requestMatchers(HttpMethod.POST, "/api/proyectos/{id}/evaluar").hasRole("COORDINADOR")
                        .requestMatchers(HttpMethod.POST, "/api/proyectos/{id}/anteproyecto").hasRole("DOCENTE")
                        .requestMatchers(HttpMethod.GET, "/api/proyectos/director/{email}").hasRole("DOCENTE")
                        .requestMatchers(HttpMethod.GET, "/api/proyectos/coordinador/{emailCoordinador}").hasRole("COORDINADOR")
                        .requestMatchers(HttpMethod.GET, "/api/proyectos/anteproyectos/jefe/{emailJefe}").hasRole("JEFE_DEPARTAMENTO")
                        .requestMatchers(HttpMethod.GET, "/api/proyectos/estudiante/{email}").hasRole("ESTUDIANTE")
                        .requestMatchers(HttpMethod.POST, "/api/proyectos/{id}/reintentar").hasRole("DOCENTE")
                        .requestMatchers(HttpMethod.GET, "/api/proyectos/director/**").hasRole("DOCENTE")
                        .requestMatchers(HttpMethod.GET, "/api/proyectos/estudiante/**").hasRole("ESTUDIANTE")
                        .requestMatchers(HttpMethod.GET, "/api/proyectos/todos").hasRole("COORDINADOR")
                        .requestMatchers(HttpMethod.GET, "/api/proyectos/coordinador/**").hasRole("COORDINADOR")
                        .requestMatchers(HttpMethod.GET, "/api/proyectos/anteproyectos/jefe/**").hasRole("JEFE_DEPARTAMENTO")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                // ✅ Corrección: frameOptions() ahora se configura así
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                // ✅ Corrección: csrf.ignoringRequestMatchers() sigue igual
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));

        return http.build();
    }

    // ✅ Corrección: JwtValidators -> JwtTimestampValidator
    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // Solo validar timestamp, NO issuer
        decoder.setJwtValidator(new JwtTimestampValidator());

        return decoder;
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractAuthoritiesFromJwt);
        return converter;
    }

    private Collection<GrantedAuthority> extractAuthoritiesFromJwt(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey("sistema-desktop")) {
            Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("sistema-desktop");
            List<String> roles = (List<String>) clientAccess.get("roles");
            if (roles != null && !roles.isEmpty()) {
                System.out.println("✅ Roles encontrados: " + roles);
                return roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
            }
        }

        // Fallback: realm_access.roles
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            List<String> roles = (List<String>) realmAccess.get("roles");
            if (roles != null && !roles.isEmpty()) {
                System.out.println("⚠️ Roles desde realm_access: " + roles);
                return roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
            }
        }

        System.out.println("❌ No se encontraron roles en resource_access.sistema-desktop ni en realm_access");
        return Collections.emptyList();
    }
}