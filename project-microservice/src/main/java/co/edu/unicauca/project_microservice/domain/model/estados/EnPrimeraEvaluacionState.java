package co.edu.unicauca.project_microservice.domain.model.estados;

import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;

public class EnPrimeraEvaluacionState implements EstadoProyecto {
    
    @Override
    public void evaluar(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        proyecto.setObservacionesEvaluacion(observaciones);
        if (aprobado) {
            proyecto.setEstado(new FormatoAAprobadoState());
        } else {
            proyecto.setEstado(new FormatoARechazadoState());
        }
    }

    @Override
    public void reintentar(ProyectoGrado proyecto) {
        throw new IllegalStateException("No se puede reintentar en primera evaluación.");
    }

    @Override
    public String getNombreEstado() {
        return "EN_PRIMERA_EVALUACION_FORMATO_A";
    }
}