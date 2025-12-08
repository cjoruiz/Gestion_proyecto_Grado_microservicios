package co.edu.unicauca.project_microservice.infra.dto;

import lombok.Data;

@Data
public class AsignacionEvaluadoresEvent {
    private Long idProyecto;
    private String tituloProyecto;
    private String evaluador1Email;
    private String evaluador2Email;
    private String estudianteEmail;
}