// infrastructure/adapter/driven/persistence/entity/ProyectoGradoEntity.java
package co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA - Solo para persistencia
 */
@Entity
@Table(name = "proyectos")
public class ProyectoGradoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String modalidad;
    private String directorEmail;
    private String codirectorEmail;
    private String estudiante1Email;
    private String estudiante2Email;
    private String objetivoGeneral;
    private String objetivosEspecificos;
    private String observacionesEvaluacion;
    private int numeroIntento = 1;
    private String estadoActual;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_anteproyecto")
    private LocalDateTime fechaAnteproyecto;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public String getDirectorEmail() {
        return directorEmail;
    }

    public void setDirectorEmail(String directorEmail) {
        this.directorEmail = directorEmail;
    }

    public String getCodirectorEmail() {
        return codirectorEmail;
    }

    public void setCodirectorEmail(String codirectorEmail) {
        this.codirectorEmail = codirectorEmail;
    }

    public String getEstudiante1Email() {
        return estudiante1Email;
    }

    public void setEstudiante1Email(String estudiante1Email) {
        this.estudiante1Email = estudiante1Email;
    }

    public String getEstudiante2Email() {
        return estudiante2Email;
    }

    public void setEstudiante2Email(String estudiante2Email) {
        this.estudiante2Email = estudiante2Email;
    }

    public String getObjetivoGeneral() {
        return objetivoGeneral;
    }

    public void setObjetivoGeneral(String objetivoGeneral) {
        this.objetivoGeneral = objetivoGeneral;
    }

    public String getObjetivosEspecificos() {
        return objetivosEspecificos;
    }

    public void setObjetivosEspecificos(String objetivosEspecificos) {
        this.objetivosEspecificos = objetivosEspecificos;
    }

    public String getObservacionesEvaluacion() {
        return observacionesEvaluacion;
    }

    public void setObservacionesEvaluacion(String observacionesEvaluacion) {
        this.observacionesEvaluacion = observacionesEvaluacion;
    }

    public int getNumeroIntento() {
        return numeroIntento;
    }

    public void setNumeroIntento(int numeroIntento) {
        this.numeroIntento = numeroIntento;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaAnteproyecto() {
        return fechaAnteproyecto;
    }

    public void setFechaAnteproyecto(LocalDateTime fechaAnteproyecto) {
        this.fechaAnteproyecto = fechaAnteproyecto;
    }
}