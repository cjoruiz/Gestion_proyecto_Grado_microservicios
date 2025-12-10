package co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.factory;

import co.edu.unicauca.project_microservice.domain.model.estados.*;
import org.springframework.stereotype.Component;

/**
 * Factory para crear instancias de estados
 */
@Component
public class EstadoProyectoFactory {

    public EstadoProyecto crear(String nombreEstado) {
        if (nombreEstado == null) {
            throw new IllegalArgumentException("El nombre del estado no puede ser null");
        }
        
        return switch (nombreEstado) {
            case "EN_PRIMERA_EVALUACION_FORMATO_A" -> new EnPrimeraEvaluacionState();
            case "EN_SEGUNDA_EVALUACION_FORMATO_A" -> new EnSegundaEvaluacionState();
            case "EN_TERCERA_EVALUACION_FORMATO_A" -> new EnTerceraEvaluacionState();
            case "FORMATO_A_APROBADO" -> new FormatoAAprobadoState();
            case "FORMATO_A_RECHAZADO" -> new FormatoARechazadoState();
            case "RECHAZADO_DEFINITIVO" -> new RechazadoDefinitivoState();
            case "ANTEPROYECTO_ENVIADO" -> new AnteproyectoEnviadoState();
            default -> throw new IllegalStateException("Estado desconocido: " + nombreEstado);
        };
    }
}