// domain/model/ProyectoGrado.java
package co.edu.unicauca.project_microservice.domain.model;

import co.edu.unicauca.project_microservice.domain.model.estados.EstadoProyecto;
import java.time.LocalDateTime;

public class ProyectoGrado {
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
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAnteproyecto;
    private EstadoProyecto estado;

    public ProyectoGrado() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters básicos...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }
    
    public String getDirectorEmail() { return directorEmail; }
    public void setDirectorEmail(String directorEmail) { this.directorEmail = directorEmail; }
    
    public String getCodirectorEmail() { return codirectorEmail; }
    public void setCodirectorEmail(String codirectorEmail) { this.codirectorEmail = codirectorEmail; }
    
    public String getEstudiante1Email() { return estudiante1Email; }
    public void setEstudiante1Email(String estudiante1Email) { this.estudiante1Email = estudiante1Email; }
    
    public String getEstudiante2Email() { return estudiante2Email; }
    public void setEstudiante2Email(String estudiante2Email) { this.estudiante2Email = estudiante2Email; }
    
    public String getObjetivoGeneral() { return objetivoGeneral; }
    public void setObjetivoGeneral(String objetivoGeneral) { this.objetivoGeneral = objetivoGeneral; }
    
    public String getObjetivosEspecificos() { return objetivosEspecificos; }
    public void setObjetivosEspecificos(String objetivosEspecificos) { this.objetivosEspecificos = objetivosEspecificos; }
    
    public String getObservacionesEvaluacion() { return observacionesEvaluacion; }
    public void setObservacionesEvaluacion(String observacionesEvaluacion) { this.observacionesEvaluacion = observacionesEvaluacion; }
    
    public int getNumeroIntento() { return numeroIntento; }
    public void setNumeroIntento(int numeroIntento) { this.numeroIntento = numeroIntento; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaAnteproyecto() { return fechaAnteproyecto; }
    public void setFechaAnteproyecto(LocalDateTime fechaAnteproyecto) { this.fechaAnteproyecto = fechaAnteproyecto; }
    
    public EstadoProyecto getEstado() { return estado; }
    public void setEstado(EstadoProyecto estado) { this.estado = estado; }

    // ============ MÉTODOS DE NEGOCIO DEL DOMINIO ============
    
    /**
     * Obtiene el nombre del estado actual
     */
    public String getEstadoActual() {
        return estado != null ? estado.getNombreEstado() : "DESCONOCIDO";
    }

    /**
     * NUEVO: Establece el estado actual por nombre
     * Se usa solo para persistencia/reconstrucción
     */
    public void setEstadoActual(String nombreEstado) {
        // Este método se usa solo para sincronizar con la base de datos
        // El estado real se establece con setEstado(EstadoProyecto)
    }

    /**
     * Evalúa el proyecto según las reglas de negocio
     */
    public void evaluar(boolean aprobado, String observaciones) {
        if (estado == null) {
            throw new IllegalStateException("Estado no inicializado.");
        }
        estado.evaluar(this, aprobado, observaciones);
    }

    /**
     * Intenta reenviar el proyecto según las reglas de negocio
     */
    public void reintentar() {
        if (estado == null) {
            throw new IllegalStateException("Estado no inicializado.");
        }
        estado.reintentar(this);
    }

    /**
     * Valida si un email puede ser evaluador
     */
    public boolean esEvaluadorValido(String email) {
        if (email == null) return false;
        return !email.equals(this.directorEmail) && !email.equals(this.codirectorEmail);
    }

    /**
     * Valida si se pueden asignar evaluadores al proyecto
     */
    public boolean puedeAsignarEvaluadores() {
        return "ANTEPROYECTO_ENVIADO".equals(getEstadoActual());
    }

    /**
     * Valida si el usuario es el director del proyecto
     */
    public boolean esDirector(String email) {
        return this.directorEmail != null && this.directorEmail.equals(email);
    }
}