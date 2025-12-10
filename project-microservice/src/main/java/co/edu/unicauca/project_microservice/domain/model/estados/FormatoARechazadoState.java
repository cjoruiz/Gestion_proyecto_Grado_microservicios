package co.edu.unicauca.project_microservice.domain.model.estados;

import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;

public class FormatoARechazadoState implements EstadoProyecto {
    
    @Override
    public void evaluar(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        throw new IllegalStateException("El proyecto ya fue rechazado. Debe reintentar primero.");
    }

    @Override
    public void reintentar(ProyectoGrado proyecto) {
        int nuevoIntento = proyecto.getNumeroIntento() + 1;

        if (nuevoIntento == 2) {
            proyecto.setEstado(new EnSegundaEvaluacionState());
        } else if (nuevoIntento == 3) {
            proyecto.setEstado(new EnTerceraEvaluacionState());
        } else if (nuevoIntento > 3) {
            proyecto.setEstado(new RechazadoDefinitivoState());
        }

        proyecto.setNumeroIntento(nuevoIntento);
    }

    @Override
    public String getNombreEstado() {
        return "FORMATO_A_RECHAZADO";
    }
}