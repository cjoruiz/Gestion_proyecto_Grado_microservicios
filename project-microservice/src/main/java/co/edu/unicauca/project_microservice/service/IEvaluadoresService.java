package co.edu.unicauca.project_microservice.service;

import co.edu.unicauca.project_microservice.entity.EvaluadoresAnteproyecto;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el servicio de gestión de evaluadores de anteproyectos
 */
public interface IEvaluadoresService {

    /**
     * Asigna evaluadores a un anteproyecto
     * 
     * @param idProyecto ID del proyecto al que se asignarán los evaluadores
     * @param evaluador1Email Email del primer evaluador
     * @param evaluador2Email Email del segundo evaluador
     * @param jefeEmail Email del jefe de departamento
     * @return La entidad EvaluadoresAnteproyecto creada
     * @throws IllegalStateException Si no se pueden asignar evaluadores al proyecto
     * @throws IllegalArgumentException Si los evaluadores no son válidos
     */
    EvaluadoresAnteproyecto asignarEvaluadores(Long idProyecto, 
                                              String evaluador1Email, 
                                              String evaluador2Email,
                                              String jefeEmail);

    /**
     * Obtiene los evaluadores asignados a un proyecto
     * 
     * @param idProyecto ID del proyecto
     * @return Optional con los evaluadores si existen
     */
    Optional<EvaluadoresAnteproyecto> obtenerEvaluadores(Long idProyecto);

    /**
     * Obtiene todos los anteproyectos asignados a un evaluador
     * 
     * @param emailEvaluador Email del evaluador
     * @return Lista de asignaciones donde el evaluador participa
     */
    List<EvaluadoresAnteproyecto> obtenerProyectosComoEvaluador(String emailEvaluador);

    /**
     * Registra la evaluación de un evaluador para un proyecto
     * 
     * @param idProyecto ID del proyecto evaluado
     * @param emailEvaluador Email del evaluador que registra la evaluación
     * @param aprobado Indica si el proyecto fue aprobado
     * @param observaciones Observaciones del evaluador
     * @throws RuntimeException Si no se encuentran evaluadores para el proyecto
     * @throws IllegalArgumentException Si el usuario no es evaluador del proyecto
     */
    void registrarEvaluacion(Long idProyecto, String emailEvaluador, 
                            boolean aprobado, String observaciones);

    /**
     * Verifica si un proyecto tiene evaluación completa de ambos evaluadores
     * 
     * @param idProyecto ID del proyecto a verificar
     * @return true si ambos evaluadores han registrado su evaluación, false en caso contrario
     */
    boolean tieneEvaluacionCompleta(Long idProyecto);
}