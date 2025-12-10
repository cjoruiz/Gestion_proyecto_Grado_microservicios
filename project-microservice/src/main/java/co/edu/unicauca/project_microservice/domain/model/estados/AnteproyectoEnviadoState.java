package co.edu.unicauca.project_microservice.domain.model.estados;

import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;

public class AnteproyectoEnviadoState implements EstadoProyecto {

    @Override
    public void evaluar(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        throw new IllegalStateException("No se puede evaluar el Formato A en estado de anteproyecto enviado.");
    }

    @Override
    public void reintentar(ProyectoGrado proyecto) {
        throw new IllegalStateException("No aplica reintento en estado de anteproyecto.");
    }

    @Override
    public String getNombreEstado() {
        return "ANTEPROYECTO_ENVIADO";
    }
}