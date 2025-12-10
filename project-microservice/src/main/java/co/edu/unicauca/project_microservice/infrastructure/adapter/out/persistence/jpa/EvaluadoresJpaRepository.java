package co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.entity.EvaluadoresAnteproyectoEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluadoresJpaRepository extends JpaRepository<EvaluadoresAnteproyectoEntity, Long> {
    
    Optional<EvaluadoresAnteproyectoEntity> findByIdProyecto(Long idProyecto);
    
    List<EvaluadoresAnteproyectoEntity> findByEvaluador1EmailOrEvaluador2Email(String evaluador1, String evaluador2);
    
    List<EvaluadoresAnteproyectoEntity> findByJefeDepartamentoEmail(String jefeEmail);
    
    boolean existsByIdProyecto(Long idProyecto);
}