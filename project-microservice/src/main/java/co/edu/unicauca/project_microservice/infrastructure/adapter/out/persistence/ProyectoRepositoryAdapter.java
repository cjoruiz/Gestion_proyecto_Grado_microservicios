package co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence;

import co.edu.unicauca.project_microservice.domain.exception.ProyectoNoEncontradoException;
import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;
import co.edu.unicauca.project_microservice.domain.port.out.ProyectoRepositoryPort;
import co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.entity.ProyectoGradoEntity;
import co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.jpa.ProyectoJpaRepository;
import co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.mapper.ProyectoMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa el puerto de salida de persistencia
 * Convierte entre Domain Model y Entity
 */
@Component
public class ProyectoRepositoryAdapter implements ProyectoRepositoryPort {

    private final ProyectoJpaRepository jpaRepository;
    private final ProyectoMapper mapper;

    public ProyectoRepositoryAdapter(ProyectoJpaRepository jpaRepository, ProyectoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ProyectoGrado crear(ProyectoGrado proyecto) {
        ProyectoGradoEntity entity = mapper.toEntity(proyecto);
        ProyectoGradoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public ProyectoGrado obtenerPorId(Long id) {
        ProyectoGradoEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new ProyectoNoEncontradoException("Proyecto no encontrado con ID: " + id));
        return mapper.toDomain(entity);
    }

    @Override
    public List<ProyectoGrado> obtenerTodos() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProyectoGrado> obtenerProyectosPorEstudiante(String email) {
        return jpaRepository.findByEstudiante1EmailOrEstudiante2Email(email).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProyectoGrado> findByDirectorEmail(String email) {
        return jpaRepository.findByDirectorEmail(email).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProyectoGrado> findByDirectorEmailOrCodirectorEmail(String email) {
        return jpaRepository.findByDirectorEmailOrCodirectorEmail(email, email).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ProyectoGrado guardar(ProyectoGrado proyecto) {
        ProyectoGradoEntity entity = mapper.toEntity(proyecto);
        ProyectoGradoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}