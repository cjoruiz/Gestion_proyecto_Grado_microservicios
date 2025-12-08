package co.edu.unicauca.project_microservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad separada para almacenar información de evaluadores
 * sin modificar la tabla de proyectos existente.
 */
@Entity
@Table(name = "evaluadores_anteproyecto")
@Data
@NoArgsConstructor
public class EvaluadoresAnteproyecto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Long idProyecto;
    
    @Column(nullable = false)
    private String evaluador1Email;
    
    @Column(nullable = false)
    private String evaluador2Email;
    
    @Column(nullable = false)
    private LocalDateTime fechaAsignacion;
    
    private String jefeDepartamentoEmail;
    
    private String observacionesEvaluador1;
    private String observacionesEvaluador2;
    
    private Boolean aprobadoPorEvaluador1;
    private Boolean aprobadoPorEvaluador2;
    
    private LocalDateTime fechaEvaluacion1;
    private LocalDateTime fechaEvaluacion2;
    
    // Constructor con campos requeridos
    public EvaluadoresAnteproyecto(Long idProyecto, String evaluador1Email, 
                                   String evaluador2Email, String jefeEmail) {
        this.idProyecto = idProyecto;
        this.evaluador1Email = evaluador1Email;
        this.evaluador2Email = evaluador2Email;
        this.jefeDepartamentoEmail = jefeEmail;
        this.fechaAsignacion = LocalDateTime.now();
    }
    
    public boolean evaluacionCompleta() {
        return aprobadoPorEvaluador1 != null && aprobadoPorEvaluador2 != null;
    }
    
    public boolean evaluacionAprobada() {
        return Boolean.TRUE.equals(aprobadoPorEvaluador1) && 
               Boolean.TRUE.equals(aprobadoPorEvaluador2);
    }
}