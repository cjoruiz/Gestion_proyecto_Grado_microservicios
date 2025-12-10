// infrastructure/adapter/out/persistence/mapper/ProyectoMapper.java
package co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.mapper;

import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;
import co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.entity.ProyectoGradoEntity;
import co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.factory.EstadoProyectoFactory;
import org.springframework.stereotype.Component;

/**
 * Mapper entre Domain Model y Entity JPA
 *  USA EL FACTORY PARA RECONSTRUIR ESTADOS
 */
@Component
public class ProyectoMapper {

    private final EstadoProyectoFactory estadoFactory;

    public ProyectoMapper(EstadoProyectoFactory estadoFactory) {
        this.estadoFactory = estadoFactory;
    }

    /**
     * Convierte del modelo de dominio a entidad JPA
     */
    public ProyectoGradoEntity toEntity(ProyectoGrado domain) {
        if (domain == null) return null;
        
        ProyectoGradoEntity entity = new ProyectoGradoEntity();
        entity.setId(domain.getId());
        entity.setTitulo(domain.getTitulo());
        entity.setModalidad(domain.getModalidad());
        entity.setDirectorEmail(domain.getDirectorEmail());
        entity.setCodirectorEmail(domain.getCodirectorEmail());
        entity.setEstudiante1Email(domain.getEstudiante1Email());
        entity.setEstudiante2Email(domain.getEstudiante2Email());
        entity.setObjetivoGeneral(domain.getObjetivoGeneral());
        entity.setObjetivosEspecificos(domain.getObjetivosEspecificos());
        entity.setObservacionesEvaluacion(domain.getObservacionesEvaluacion());
        entity.setNumeroIntento(domain.getNumeroIntento());
        entity.setEstadoActual(domain.getEstadoActual());
        entity.setFechaCreacion(domain.getFechaCreacion());
        entity.setFechaAnteproyecto(domain.getFechaAnteproyecto());
        
        return entity;
    }

    /**
     * Convierte de entidad JPA a modelo de dominio
     *  USA EL FACTORY PARA RECONSTRUIR EL ESTADO
     */
    public ProyectoGrado toDomain(ProyectoGradoEntity entity) {
        if (entity == null) return null;
        
        ProyectoGrado domain = new ProyectoGrado();
        domain.setId(entity.getId());
        domain.setTitulo(entity.getTitulo());
        domain.setModalidad(entity.getModalidad());
        domain.setDirectorEmail(entity.getDirectorEmail());
        domain.setCodirectorEmail(entity.getCodirectorEmail());
        domain.setEstudiante1Email(entity.getEstudiante1Email());
        domain.setEstudiante2Email(entity.getEstudiante2Email());
        domain.setObjetivoGeneral(entity.getObjetivoGeneral());
        domain.setObjetivosEspecificos(entity.getObjetivosEspecificos());
        domain.setObservacionesEvaluacion(entity.getObservacionesEvaluacion());
        domain.setNumeroIntento(entity.getNumeroIntento());
        domain.setFechaCreacion(entity.getFechaCreacion());
        domain.setFechaAnteproyecto(entity.getFechaAnteproyecto());
        
        //  USAR EL FACTORY para reconstruir el estado
        if (entity.getEstadoActual() != null) {
            domain.setEstado(estadoFactory.crear(entity.getEstadoActual()));
        }
        
        return domain;
    }
}