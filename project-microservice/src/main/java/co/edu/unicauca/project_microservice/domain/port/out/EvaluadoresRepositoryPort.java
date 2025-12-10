package co.edu.unicauca.project_microservice.domain.port.out;

import co.edu.unicauca.project_microservice.domain.model.EvaluadoresAnteproyecto;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida - Para persistencia de evaluadores
 */
public interface EvaluadoresRepositoryPort {
    EvaluadoresAnteproyecto asignarEvaluadores(Long id, String e1, String e2, String jefe);
    Optional<EvaluadoresAnteproyecto> obtenerEvaluadores(Long id);
    List<EvaluadoresAnteproyecto> obtenerProyectosComoEvaluador(String email);
    void registrarEvaluacion(Long id, String email, boolean aprobado, String obs);
    boolean tieneEvaluacionCompleta(Long id);
}