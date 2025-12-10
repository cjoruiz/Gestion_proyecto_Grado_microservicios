package co.edu.unicauca.api_gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:http://keycloak:8080/realms/sistema/protocol/openid-connect/certs}")
    private String jwkSetUri;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                // === Rutas públicas ===
                .pathMatchers(
                    "/usuarios/estudiantes",
                    "/usuarios/docentes",
                    "/usuarios/coordinadores",
                    "/usuarios/jefes-departamento",
                    "/documentos/plantilla/**"
                ).permitAll()

                // === PROYECTOS: Autorización por ruta ===
                
                // Crear proyecto (POST /proyectos)
                .pathMatchers("/proyectos").access(this::checkCreateProject)
                
                // Evaluar proyecto (POST /proyectos/{id}/evaluar)
                .pathMatchers("/proyectos/*/evaluar").access(this::checkCoordinador)
                
                // Subir anteproyecto (POST /proyectos/{id}/anteproyecto)
                .pathMatchers("/proyectos/*/anteproyecto").access(this::checkDocente)
                
                // Reintentar proyecto (POST /proyectos/{id}/reintentar)
                .pathMatchers("/proyectos/*/reintentar").access(this::checkDocente)
                
                // Asignar evaluadores (POST /proyectos/{id}/asignar-evaluadores)
                .pathMatchers("/proyectos/*/asignar-evaluadores").access(this::checkJefeDepartamento)
                
                // Evaluar anteproyecto (POST /proyectos/{id}/evaluar-anteproyecto)
                .pathMatchers("/proyectos/*/evaluar-anteproyecto").access(this::checkDocente)
                
                // Obtener evaluadores (GET /proyectos/{id}/evaluadores)
                .pathMatchers("/proyectos/*/evaluadores").access(this::checkDocenteCoordinadorJefe)
                
                // Mis evaluaciones (GET /proyectos/mis-evaluaciones)
                .pathMatchers("/proyectos/mis-evaluaciones").access(this::checkDocente)
                
                // Proyectos por estudiante (GET /proyectos/estudiante/{email})
                .pathMatchers("/proyectos/estudiante/**").access(this::checkEstudiante)
                
                // Proyectos por director (GET /proyectos/director/{email})
                .pathMatchers("/proyectos/director/**").access(this::checkDocente)
                
                // Proyectos por docente (GET /proyectos/docente/{email})
                .pathMatchers("/proyectos/docente/**").access(this::checkDocente)
                
                // Todos los proyectos (GET /proyectos/todos)
                .pathMatchers("/proyectos/todos").access(this::checkCoordinador)
                
                // Proyectos para coordinador (GET /proyectos/coordinador/{email})
                .pathMatchers("/proyectos/coordinador/**").access(this::checkCoordinador)
                
                // Anteproyectos para jefe (GET /proyectos/anteproyectos/jefe/{email})
                .pathMatchers("/proyectos/anteproyectos/jefe/**").access(this::checkJefeDepartamento)

                // === MENSAJES ===
                .pathMatchers("/mensajes/**").authenticated()

                // === DOCUMENTOS ===
                .pathMatchers("/documentos/**").authenticated()

                // === USUARIOS ===
                .pathMatchers("/usuarios/validar").authenticated()
                .pathMatchers("/usuarios/docentes-por-programa").access(this::checkJefeDepartamento)
                .pathMatchers("/usuarios/**").authenticated()

                // === Todo lo demás requiere estar autenticado ===
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
            );

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder
                .withJwkSetUri(jwkSetUri)
                .build();

        OAuth2TokenValidator<Jwt> validator = new JwtTimestampValidator();
        decoder.setJwtValidator(validator);

        return decoder;
    }

    // ========== MÉTODOS DE AUTORIZACIÓN ==========

    /**
     * Verifica si el usuario tiene rol ESTUDIANTE
     */
    private Mono<AuthorizationDecision> checkEstudiante(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
            .map(auth -> {
                List<String> roles = extractRoles(auth);
                System.out.println("[Gateway] Verificando ESTUDIANTE - Roles: " + roles + " | Path: " + context.getExchange().getRequest().getPath());
                return new AuthorizationDecision(roles.contains("ESTUDIANTE"));
            })
            .defaultIfEmpty(new AuthorizationDecision(false));
    }

    /**
     * Verifica si el usuario tiene rol DOCENTE
     */
    private Mono<AuthorizationDecision> checkDocente(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
            .map(auth -> {
                List<String> roles = extractRoles(auth);
                System.out.println("[Gateway] Verificando DOCENTE - Roles: " + roles + " | Path: " + context.getExchange().getRequest().getPath());
                return new AuthorizationDecision(roles.contains("DOCENTE"));
            })
            .defaultIfEmpty(new AuthorizationDecision(false));
    }

    /**
     * Verifica si el usuario tiene rol COORDINADOR
     */
    private Mono<AuthorizationDecision> checkCoordinador(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
            .map(auth -> {
                List<String> roles = extractRoles(auth);
                System.out.println("[Gateway] Verificando COORDINADOR - Roles: " + roles + " | Path: " + context.getExchange().getRequest().getPath());
                return new AuthorizationDecision(roles.contains("COORDINADOR"));
            })
            .defaultIfEmpty(new AuthorizationDecision(false));
    }

    /**
     * Verifica si el usuario tiene rol JEFE_DEPARTAMENTO
     */
    private Mono<AuthorizationDecision> checkJefeDepartamento(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
            .map(auth -> {
                List<String> roles = extractRoles(auth);
                System.out.println("[Gateway] Verificando JEFE_DEPARTAMENTO - Roles: " + roles + " | Path: " + context.getExchange().getRequest().getPath());
                return new AuthorizationDecision(roles.contains("JEFE_DEPARTAMENTO"));
            })
            .defaultIfEmpty(new AuthorizationDecision(false));
    }

    /**
     * Verifica si el usuario tiene rol DOCENTE, COORDINADOR o JEFE_DEPARTAMENTO
     */
    private Mono<AuthorizationDecision> checkDocenteCoordinadorJefe(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
            .map(auth -> {
                List<String> roles = extractRoles(auth);
                System.out.println("[Gateway] Verificando DOCENTE/COORDINADOR/JEFE - Roles: " + roles + " | Path: " + context.getExchange().getRequest().getPath());
                boolean hasRole = roles.contains("DOCENTE") || 
                                 roles.contains("COORDINADOR") || 
                                 roles.contains("JEFE_DEPARTAMENTO");
                return new AuthorizationDecision(hasRole);
            })
            .defaultIfEmpty(new AuthorizationDecision(false));
    }

    /**
     * Verifica para crear proyecto: Solo DOCENTE en POST
     */
    private Mono<AuthorizationDecision> checkCreateProject(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
            .map(auth -> {
                String method = context.getExchange().getRequest().getMethod().name();
                List<String> roles = extractRoles(auth);
                
                System.out.println("[Gateway] Verificando crear proyecto - Method: " + method + " | Roles: " + roles);
                
                // POST /proyectos solo para DOCENTE
                if ("POST".equals(method)) {
                    return new AuthorizationDecision(roles.contains("DOCENTE"));
                }
                
                // GET /proyectos puede ser cualquier autenticado
                return new AuthorizationDecision(true);
            })
            .defaultIfEmpty(new AuthorizationDecision(false));
    }

    /**
     * Extrae los roles del JWT
     */
    private List<String> extractRoles(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            
            // Intentar resource_access.sistema-desktop.roles
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null && resourceAccess.containsKey("sistema-desktop")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> client = (Map<String, Object>) resourceAccess.get("sistema-desktop");
                if (client != null && client.containsKey("roles")) {
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) client.get("roles");
                    return roles != null ? roles : Collections.emptyList();
                }
            }

            // Fallback: realm_access.roles
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) realmAccess.get("roles");
                return roles != null ? roles : Collections.emptyList();
            }
        }

        return Collections.emptyList();
    }
}