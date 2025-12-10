package co.edu.unicauca.project_microservice.domain.model.estados;

import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;

public class FormatoAAprobadoState implements EstadoProyecto {
    
    @Override
    public void evaluar(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        throw new IllegalStateException("El proyecto ya fue aprobado.");
    }

    @Override
    public void reintentar(ProyectoGrado proyecto) {
        throw new IllegalStateException("No se puede reintentar un proyecto aprobado.");
    }

    @Override
    public String getNombreEstado() {
        return "FORMATO_A_APROBADO";
    }
}