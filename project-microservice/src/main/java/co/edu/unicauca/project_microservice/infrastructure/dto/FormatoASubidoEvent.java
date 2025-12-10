package co.edu.unicauca.project_microservice.infrastructure.dto;

import lombok.Data;

@Data
public class FormatoASubidoEvent {
    private Long idProyecto;
    private String titulo;
    private String coordinadorEmail;
}
