package co.edu.unicauca.project_microservice.domain.port.in;

import co.edu.unicauca.project_microservice.domain.model.EvaluadoresAnteproyecto;
import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Puerto de entrada - Define los casos de uso del dominio
 */
public interface ProyectoUseCasePort {
    ProyectoGrado crearProyecto(ProyectoGrado proyecto, String emailAutenticado);
    void evaluarProyecto(Long id, boolean aprobado, String observaciones);
    void reintentarProyecto(Long id);
    void subirAnteproyecto(Long id, String jefeEmail);
    
    List<ProyectoGrado> obtenerTodosProyectos();
    List<ProyectoGrado> obtenerProyectosPorEstudiante(String email);
    List<ProyectoGrado> obtenerProyectosPorDirector(String email);
    List<ProyectoGrado> obtenerProyectosPorDocente(String email);
    List<ProyectoGrado> obtenerAnteproyectosPorJefe(String emailJefe);
    List<ProyectoGrado> obtenerProyectosPendientesCoordinador();
    ProyectoGrado obtenerProyectoPorId(Long id);
    
    void asignarEvaluadores(Long id, String e1, String e2, String jefe);
    void registrarEvaluacionAnteproyecto(Long id, String emailEvaluador, boolean aprobado, String obs);
    void procesarEvaluacionCompleta(Long id);
    Optional<EvaluadoresAnteproyecto> obtenerEvaluadores(Long id);
    List<Map<String, Object>> obtenerMisEvaluaciones(String email);
}