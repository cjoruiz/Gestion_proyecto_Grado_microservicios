package co.edu.unicauca.notification_microservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class AsignacionEvaluadoresNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idProyecto;
    private String tituloProyecto;
    private String evaluador1Email;
    private String evaluador2Email;
    private String estudianteEmail;
}