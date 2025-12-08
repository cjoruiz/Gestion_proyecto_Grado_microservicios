package co.edu.unicauca.project_microservice.entity.decorator;

import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Decorator para ProyectoGrado que agrega funcionalidad de evaluadores
 * sin modificar la entidad base.
 */
@Data
public class ProyectoGradoDecorator {
    private ProyectoGrado proyectoBase;
    private String evaluador1Email;
    private String evaluador2Email;
    private LocalDateTime fechaAsignacionEvaluadores;
    private String jefeDepartamentoEmail;
    
    public ProyectoGradoDecorator(ProyectoGrado proyecto) {
        this.proyectoBase = proyecto;
    }
    
    // Métodos de delegación al proyecto base
    public Long getId() {
        return proyectoBase.getId();
    }
    
    public String getTitulo() {
        return proyectoBase.getTitulo();
    }
    
    public String getModalidad() {
        return proyectoBase.getModalidad();
    }
    
    public String getDirectorEmail() {
        return proyectoBase.getDirectorEmail();
    }
    
    public String getCodirectorEmail() {
        return proyectoBase.getCodirectorEmail();
    }
    
    public String getEstudiante1Email() {
        return proyectoBase.getEstudiante1Email();
    }
    
    public String getEstudiante2Email() {
        return proyectoBase.getEstudiante2Email();
    }
    
    public String getEstadoActual() {
        return proyectoBase.getEstadoActual();
    }
    
    public int getNumeroIntento() {
        return proyectoBase.getNumeroIntento();
    }
    
    // Métodos de validación para evaluadores
    public boolean tieneEvaluadoresAsignados() {
        return evaluador1Email != null && evaluador2Email != null;
    }
    
    public boolean esEvaluadorValido(String email) {
        if (email == null) return false;
        
        // No puede ser director ni codirector
        if (email.equals(proyectoBase.getDirectorEmail()) || 
            email.equals(proyectoBase.getCodirectorEmail())) {
            return false;
        }
        
        return true;
    }
    
    public boolean puedeAsignarEvaluadores() {
        return "ANTEPROYECTO_ENVIADO".equals(proyectoBase.getEstadoActual()) 
               && !tieneEvaluadoresAsignados();
    }
}