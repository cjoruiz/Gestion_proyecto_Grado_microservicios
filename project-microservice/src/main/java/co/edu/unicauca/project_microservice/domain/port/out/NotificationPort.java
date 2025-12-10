package co.edu.unicauca.project_microservice.domain.port.out;

import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;

/**
 * Puerto de salida - Para notificaciones
 */
public interface NotificationPort {
    void notificarFormatoASubido(ProyectoGrado p);
    void notificarEvaluacion(ProyectoGrado p, boolean aprobado, String observaciones);
    void notificarAnteproyectoSubido(ProyectoGrado p, String jefeEmail);
    void notificarAsignacionEvaluadores(ProyectoGrado p, String evaluador1Email, String evaluador2Email);
    void notificarResultadoEvaluacion(ProyectoGrado p, boolean aprobado, String observaciones);
}
