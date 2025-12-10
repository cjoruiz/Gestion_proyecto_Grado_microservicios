package co.edu.unicauca.project_microservice.infrastructure.dto;

import lombok.Data;

@Data
public class ProyectoEvaluadoEvent {
    private Long idProyecto;
    private boolean aprobado;
    private String observaciones;
    private String[] destinatarios;
}