package co.edu.unicauca.messaging_microservice.utilities;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class JwtUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String extractEmailFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        try {
            String token = authHeader.substring(7);
            String[] parts = token.split("\\.");
            
            if (parts.length < 2) {
                return null;
            }

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            JsonNode json = mapper.readTree(payload);
            
            // Intentar obtener email
            if (json.has("email")) {
                return json.get("email").asText();
            }
            
            // Fallback a preferred_username
            if (json.has("preferred_username")) {
                return json.get("preferred_username").asText();
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}