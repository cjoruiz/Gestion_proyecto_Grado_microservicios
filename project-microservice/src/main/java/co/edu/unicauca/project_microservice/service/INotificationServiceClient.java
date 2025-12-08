package co.edu.unicauca.project_microservice.service;

import co.edu.unicauca.project_microservice.infra.dto.*;

public interface INotificationServiceClient {
    void notificarFormatoASubido(FormatoASubidoEvent event);
    void notificarEvaluacion(ProyectoEvaluadoEvent event);
    void notificarAnteproyectoSubido(AnteproyectoSubidoEvent event);
    void notificarAsignacionEvaluadores(AsignacionEvaluadoresEvent event);
}