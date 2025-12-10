// domain/model/EvaluadoresAnteproyecto.java
package co.edu.unicauca.project_microservice.domain.model;

import java.time.LocalDateTime;

/**
 * Modelo de dominio puro
 */
public class EvaluadoresAnteproyecto {
    
    private Long id;
    private Long idProyecto;
    private String evaluador1Email;
    private String evaluador2Email;
    private LocalDateTime fechaAsignacion;
    private String jefeDepartamentoEmail;
    private String observacionesEvaluador1;
    private String observacionesEvaluador2;
    private Boolean aprobadoPorEvaluador1;
    private Boolean aprobadoPorEvaluador2;
    private LocalDateTime fechaEvaluacion1;
    private LocalDateTime fechaEvaluacion2;
    
    public EvaluadoresAnteproyecto() {
    }
    
    public EvaluadoresAnteproyecto(Long idProyecto, String evaluador1Email, 
                                   String evaluador2Email, String jefeEmail) {
        this.idProyecto = idProyecto;
        this.evaluador1Email = evaluador1Email;
        this.evaluador2Email = evaluador2Email;
        this.jefeDepartamentoEmail = jefeEmail;
        this.fechaAsignacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Long idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getEvaluador1Email() {
        return evaluador1Email;
    }

    public void setEvaluador1Email(String evaluador1Email) {
        this.evaluador1Email = evaluador1Email;
    }

    public String getEvaluador2Email() {
        return evaluador2Email;
    }

    public void setEvaluador2Email(String evaluador2Email) {
        this.evaluador2Email = evaluador2Email;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public String getJefeDepartamentoEmail() {
        return jefeDepartamentoEmail;
    }

    public void setJefeDepartamentoEmail(String jefeDepartamentoEmail) {
        this.jefeDepartamentoEmail = jefeDepartamentoEmail;
    }

    public String getObservacionesEvaluador1() {
        return observacionesEvaluador1;
    }

    public void setObservacionesEvaluador1(String observacionesEvaluador1) {
        this.observacionesEvaluador1 = observacionesEvaluador1;
    }

    public String getObservacionesEvaluador2() {
        return observacionesEvaluador2;
    }

    public void setObservacionesEvaluador2(String observacionesEvaluador2) {
        this.observacionesEvaluador2 = observacionesEvaluador2;
    }

    public Boolean getAprobadoPorEvaluador1() {
        return aprobadoPorEvaluador1;
    }

    public void setAprobadoPorEvaluador1(Boolean aprobadoPorEvaluador1) {
        this.aprobadoPorEvaluador1 = aprobadoPorEvaluador1;
    }

    public Boolean getAprobadoPorEvaluador2() {
        return aprobadoPorEvaluador2;
    }

    public void setAprobadoPorEvaluador2(Boolean aprobadoPorEvaluador2) {
        this.aprobadoPorEvaluador2 = aprobadoPorEvaluador2;
    }

    public LocalDateTime getFechaEvaluacion1() {
        return fechaEvaluacion1;
    }

    public void setFechaEvaluacion1(LocalDateTime fechaEvaluacion1) {
        this.fechaEvaluacion1 = fechaEvaluacion1;
    }

    public LocalDateTime getFechaEvaluacion2() {
        return fechaEvaluacion2;
    }

    public void setFechaEvaluacion2(LocalDateTime fechaEvaluacion2) {
        this.fechaEvaluacion2 = fechaEvaluacion2;
    }
    
    // Métodos de negocio del dominio
    public boolean evaluacionCompleta() {
        return aprobadoPorEvaluador1 != null && aprobadoPorEvaluador2 != null;
    }
    
    public boolean evaluacionAprobada() {
        return Boolean.TRUE.equals(aprobadoPorEvaluador1) && 
               Boolean.TRUE.equals(aprobadoPorEvaluador2);
    }
}