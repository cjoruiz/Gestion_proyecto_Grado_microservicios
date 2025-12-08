package co.edu.unicauca.project_microservice.service;

import co.edu.unicauca.project_microservice.entity.EvaluadoresAnteproyecto;
import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import co.edu.unicauca.project_microservice.entity.decorator.ProyectoGradoDecorator;
import co.edu.unicauca.project_microservice.repository.EvaluadoresAnteproyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluadoresService implements IEvaluadoresService {

    @Autowired
    private EvaluadoresAnteproyectoRepository evaluadoresRepository;
    
    @Autowired
    private IProyectoService proyectoService;

    /**
     * Asigna evaluadores a un anteproyecto
     */
     @Override
    @Transactional
    public EvaluadoresAnteproyecto asignarEvaluadores(Long idProyecto, 
                                                       String evaluador1Email, 
                                                       String evaluador2Email,
                                                       String jefeEmail) {
        
        // Validar que el proyecto existe
        ProyectoGrado proyecto = proyectoService.obtenerPorId(idProyecto);
        
        // Crear decorator para validaciones
        ProyectoGradoDecorator decorator = new ProyectoGradoDecorator(proyecto);
        
        // Validaciones usando el decorator
        if (!decorator.puedeAsignarEvaluadores()) {
            throw new IllegalStateException(
                "Solo se pueden asignar evaluadores a anteproyectos enviados sin evaluadores previos"
            );
        }
        
        if (evaluador1Email.equals(evaluador2Email)) {
            throw new IllegalArgumentException("Los evaluadores deben ser diferentes");
        }
        
        if (!decorator.esEvaluadorValido(evaluador1Email) || 
            !decorator.esEvaluadorValido(evaluador2Email)) {
            throw new IllegalArgumentException(
                "Los evaluadores no pueden ser el director ni codirector del proyecto"
            );
        }
        
        // Verificar que no existan evaluadores previos
        if (evaluadoresRepository.existsByIdProyecto(idProyecto)) {
            throw new IllegalStateException("Este proyecto ya tiene evaluadores asignados");
        }
        
        // Crear y guardar la asignación
        EvaluadoresAnteproyecto evaluadores = new EvaluadoresAnteproyecto(
            idProyecto, evaluador1Email, evaluador2Email, jefeEmail
        );
        
        return evaluadoresRepository.save(evaluadores);
    }
    
    /**
     * Obtiene los evaluadores de un proyecto
     */
     @Override
    public Optional<EvaluadoresAnteproyecto> obtenerEvaluadores(Long idProyecto) {
        return evaluadoresRepository.findByIdProyecto(idProyecto);
    }
    
    /**
     * Obtiene todos los anteproyectos asignados a un evaluador
     */
     @Override
    public List<EvaluadoresAnteproyecto> obtenerProyectosComoEvaluador(String emailEvaluador) {
        return evaluadoresRepository.findByEvaluador1EmailOrEvaluador2Email(
            emailEvaluador, emailEvaluador
        );
    }
    
    /**
     * Registra la evaluación de un evaluador
     */
     @Override
    @Transactional
    public void registrarEvaluacion(Long idProyecto, String emailEvaluador, 
                                     boolean aprobado, String observaciones) {
        
        EvaluadoresAnteproyecto evaluadores = evaluadoresRepository.findByIdProyecto(idProyecto)
            .orElseThrow(() -> new RuntimeException("No se encontraron evaluadores para este proyecto"));
        
        if (evaluadores.getEvaluador1Email().equals(emailEvaluador)) {
            evaluadores.setAprobadoPorEvaluador1(aprobado);
            evaluadores.setObservacionesEvaluador1(observaciones);
            evaluadores.setFechaEvaluacion1(java.time.LocalDateTime.now());
        } else if (evaluadores.getEvaluador2Email().equals(emailEvaluador)) {
            evaluadores.setAprobadoPorEvaluador2(aprobado);
            evaluadores.setObservacionesEvaluador2(observaciones);
            evaluadores.setFechaEvaluacion2(java.time.LocalDateTime.now());
        } else {
            throw new IllegalArgumentException("El usuario no es evaluador de este proyecto");
        }
        
        evaluadoresRepository.save(evaluadores);
    }
    
    /**
     * Verifica si un proyecto tiene evaluación completa
     */
     @Override
    public boolean tieneEvaluacionCompleta(Long idProyecto) {
        return evaluadoresRepository.findByIdProyecto(idProyecto)
            .map(EvaluadoresAnteproyecto::evaluacionCompleta)
            .orElse(false);
    }
}