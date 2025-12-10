// infrastructure/adapter/out/persistence/EvaluadoresRepositoryAdapter.java
package co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence;

import co.edu.unicauca.project_microservice.domain.model.EvaluadoresAnteproyecto;
import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;
import co.edu.unicauca.project_microservice.domain.port.out.EvaluadoresRepositoryPort;
import co.edu.unicauca.project_microservice.domain.port.out.ProyectoRepositoryPort;
import co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.entity.EvaluadoresAnteproyectoEntity;
import co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.jpa.EvaluadoresJpaRepository;
import co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.mapper.EvaluadoresMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador para el repositorio de evaluadores
 */
@Component
public class EvaluadoresRepositoryAdapter implements EvaluadoresRepositoryPort {

    private final EvaluadoresJpaRepository jpaRepository;
    private final ProyectoRepositoryPort proyectoRepository;
    private final EvaluadoresMapper mapper;

    public EvaluadoresRepositoryAdapter(
            EvaluadoresJpaRepository jpaRepository,
            ProyectoRepositoryPort proyectoRepository,
            EvaluadoresMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.proyectoRepository = proyectoRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public EvaluadoresAnteproyecto asignarEvaluadores(Long idProyecto, String e1, String e2, String jefe) {
        ProyectoGrado proyecto = proyectoRepository.obtenerPorId(idProyecto);

        // Validaciones de reglas de negocio
        if (!proyecto.puedeAsignarEvaluadores()) {
            throw new IllegalStateException(
                    "Solo se pueden asignar evaluadores a anteproyectos enviados sin evaluadores previos"
            );
        }

        if (e1.equals(e2)) {
            throw new IllegalArgumentException("Los evaluadores deben ser diferentes");
        }

        if (!proyecto.esEvaluadorValido(e1) || !proyecto.esEvaluadorValido(e2)) {
            throw new IllegalArgumentException(
                    "Los evaluadores no pueden ser el director ni codirector del proyecto"
            );
        }

        if (jpaRepository.existsByIdProyecto(idProyecto)) {
            throw new IllegalStateException("Este proyecto ya tiene evaluadores asignados");
        }

        // Crear y persistir
        EvaluadoresAnteproyecto evaluadores = new EvaluadoresAnteproyecto(idProyecto, e1, e2, jefe);
        EvaluadoresAnteproyectoEntity entity = mapper.toEntity(evaluadores);
        EvaluadoresAnteproyectoEntity saved = jpaRepository.save(entity);
        
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<EvaluadoresAnteproyecto> obtenerEvaluadores(Long idProyecto) {
        return jpaRepository.findByIdProyecto(idProyecto)
                .map(mapper::toDomain);
    }

    @Override
    public List<EvaluadoresAnteproyecto> obtenerProyectosComoEvaluador(String email) {
        return jpaRepository.findByEvaluador1EmailOrEvaluador2Email(email, email)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void registrarEvaluacion(Long idProyecto, String emailEvaluador, boolean aprobado, String obs) {
        EvaluadoresAnteproyectoEntity entity = jpaRepository.findByIdProyecto(idProyecto)
                .orElseThrow(() -> new RuntimeException("No se encontraron evaluadores para este proyecto"));

        if (entity.getEvaluador1Email().equals(emailEvaluador)) {
            entity.setAprobadoPorEvaluador1(aprobado);
            entity.setObservacionesEvaluador1(obs);
            entity.setFechaEvaluacion1(java.time.LocalDateTime.now());
        } else if (entity.getEvaluador2Email().equals(emailEvaluador)) {
            entity.setAprobadoPorEvaluador2(aprobado);
            entity.setObservacionesEvaluador2(obs);
            entity.setFechaEvaluacion2(java.time.LocalDateTime.now());
        } else {
            throw new IllegalArgumentException("El usuario no es evaluador de este proyecto");
        }

        jpaRepository.save(entity);
    }

    @Override
    public boolean tieneEvaluacionCompleta(Long idProyecto) {
        return jpaRepository.findByIdProyecto(idProyecto)
                .map(entity -> entity.getAprobadoPorEvaluador1() != null && 
                              entity.getAprobadoPorEvaluador2() != null)
                .orElse(false);
    }
}