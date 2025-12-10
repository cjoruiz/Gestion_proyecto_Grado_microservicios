package co.edu.unicauca.project_microservice.domain.port.out;

import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;
import java.util.List;

/**
 * Puerto de salida - Para persistencia de proyectos
 */
public interface ProyectoRepositoryPort {
    ProyectoGrado crear(ProyectoGrado p);
    ProyectoGrado obtenerPorId(Long id);
    List<ProyectoGrado> obtenerTodos();
    List<ProyectoGrado> obtenerProyectosPorEstudiante(String email);
    List<ProyectoGrado> findByDirectorEmail(String email);
    List<ProyectoGrado> findByDirectorEmailOrCodirectorEmail(String email);
    ProyectoGrado guardar(ProyectoGrado p);
}