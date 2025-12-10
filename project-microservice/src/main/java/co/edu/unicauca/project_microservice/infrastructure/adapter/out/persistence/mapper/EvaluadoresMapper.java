package co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.mapper;

import co.edu.unicauca.project_microservice.domain.model.EvaluadoresAnteproyecto;
import co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.entity.EvaluadoresAnteproyectoEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre modelo de dominio y entidad JPA de evaluadores
 */
@Component
public class EvaluadoresMapper {

    public EvaluadoresAnteproyectoEntity toEntity(EvaluadoresAnteproyecto domain) {
        if (domain == null) return null;
        
        EvaluadoresAnteproyectoEntity entity = new EvaluadoresAnteproyectoEntity();
        entity.setId(domain.getId());
        entity.setIdProyecto(domain.getIdProyecto());
        entity.setEvaluador1Email(domain.getEvaluador1Email());
        entity.setEvaluador2Email(domain.getEvaluador2Email());
        entity.setFechaAsignacion(domain.getFechaAsignacion());
        entity.setJefeDepartamentoEmail(domain.getJefeDepartamentoEmail());
        entity.setObservacionesEvaluador1(domain.getObservacionesEvaluador1());
        entity.setObservacionesEvaluador2(domain.getObservacionesEvaluador2());
        entity.setAprobadoPorEvaluador1(domain.getAprobadoPorEvaluador1());
        entity.setAprobadoPorEvaluador2(domain.getAprobadoPorEvaluador2());
        entity.setFechaEvaluacion1(domain.getFechaEvaluacion1());
        entity.setFechaEvaluacion2(domain.getFechaEvaluacion2());
        
        return entity;
    }

    public EvaluadoresAnteproyecto toDomain(EvaluadoresAnteproyectoEntity entity) {
        if (entity == null) return null;
        
        EvaluadoresAnteproyecto domain = new EvaluadoresAnteproyecto();
        domain.setId(entity.getId());
        domain.setIdProyecto(entity.getIdProyecto());
        domain.setEvaluador1Email(entity.getEvaluador1Email());
        domain.setEvaluador2Email(entity.getEvaluador2Email());
        domain.setFechaAsignacion(entity.getFechaAsignacion());
        domain.setJefeDepartamentoEmail(entity.getJefeDepartamentoEmail());
        domain.setObservacionesEvaluador1(entity.getObservacionesEvaluador1());
        domain.setObservacionesEvaluador2(entity.getObservacionesEvaluador2());
        domain.setAprobadoPorEvaluador1(entity.getAprobadoPorEvaluador1());
        domain.setAprobadoPorEvaluador2(entity.getAprobadoPorEvaluador2());
        domain.setFechaEvaluacion1(entity.getFechaEvaluacion1());
        domain.setFechaEvaluacion2(entity.getFechaEvaluacion2());
        
        return domain;
    }
}