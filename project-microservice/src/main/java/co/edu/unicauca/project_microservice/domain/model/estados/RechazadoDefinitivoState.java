package co.edu.unicauca.project_microservice.domain.model.estados;

import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;

public class RechazadoDefinitivoState implements EstadoProyecto {
    
    @Override
    public void evaluar(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        throw new IllegalStateException("El proyecto fue rechazado definitivamente.");
    }

    @Override
    public void reintentar(ProyectoGrado proyecto) {
        throw new IllegalStateException("No se pueden hacer más reintentos.");
    }

    @Override
    public String getNombreEstado() {
        return "RECHAZADO_DEFINITIVO";
    }
}