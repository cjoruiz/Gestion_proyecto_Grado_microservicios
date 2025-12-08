package co.edu.unicauca.user_microservice.infra.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String adminClientId;

    // Opcional: si se usa client credentials
    @Value("${keycloak.admin.client-secret:}")
    private String clientSecret;

    // Opcional: si se usa password grant (solo para desarrollo)
    @Value("${keycloak.admin.username:}")
    private String adminUsername;

    @Value("${keycloak.admin.password:}")
    private String adminPassword;

    @Value("${keycloak.admin.grant-type:client_credentials}") // ← por defecto client_credentials
    private String grantType;

    @Bean
    public Keycloak keycloakAdminClient() {
        var builder = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(adminClientId);

        if ("client_credentials".equalsIgnoreCase(grantType)) {
            if (clientSecret == null || clientSecret.trim().isEmpty()) {
                throw new IllegalStateException("client_credentials grant type requires 'keycloak.admin.client-secret' to be set");
            }
            builder.grantType("client_credentials")
                    .clientSecret(clientSecret);
        } else if ("password".equalsIgnoreCase(grantType)) {
            if (adminUsername == null || adminUsername.trim().isEmpty() ||
                    adminPassword == null || adminPassword.trim().isEmpty()) {
                throw new IllegalStateException("password grant type requires 'keycloak.admin.username' and 'keycloak.admin.password'");
            }
            builder.username(adminUsername)
                    .password(adminPassword);
        } else {
            throw new IllegalArgumentException("Unsupported grant type: " + grantType);
        }

        return builder.build();
    }
}