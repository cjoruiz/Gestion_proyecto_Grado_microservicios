package co.edu.unicauca.project_microservice.entity.estados;

import co.edu.unicauca.project_microservice.entity.EstadoProyecto;
import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import org.springframework.stereotype.Component;

@Component
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