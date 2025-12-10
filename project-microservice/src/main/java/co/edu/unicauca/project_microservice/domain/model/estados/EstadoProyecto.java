package co.edu.unicauca.project_microservice.domain.model.estados;

import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;

public interface EstadoProyecto {
    void evaluar(ProyectoGrado proyecto, boolean aprobado, String observaciones);
    void reintentar(ProyectoGrado proyecto);
    String getNombreEstado();
}