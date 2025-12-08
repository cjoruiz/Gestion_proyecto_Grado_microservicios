package co.edu.unicauca.user_microservice.infra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO para representar la información detallada de un usuario, incluyendo su rol.
 * Usado para respuestas de consulta de usuario por email.
 */
@Data
@Schema(description = "Información detallada de un usuario, incluyendo su rol")
public class UsuarioDetalladoDto {

    @Schema(example = "juan.perez@unicauca.edu.co", description = "Email del usuario", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(example = "Juan Carlos", description = "Nombres del usuario", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombres;

    @Schema(example = "Pérez Gómez", description = "Apellidos del usuario", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apellidos;

    @Schema(example = "3101234567", description = "Celular del usuario (opcional)")
    private String celular;

    @Schema(example = "INGENIERIA_SISTEMAS", description = "Programa del usuario (opcional, dependiendo del tipo)")
    private String programa;

    @Schema(example = "DOCENTE", description = "Rol del usuario en el sistema", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rol; // Campo crucial para la validación en el frontend
}