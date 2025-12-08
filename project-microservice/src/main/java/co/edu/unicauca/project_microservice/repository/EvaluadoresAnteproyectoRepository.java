package co.edu.unicauca.project_microservice.repository;

import co.edu.unicauca.project_microservice.entity.EvaluadoresAnteproyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluadoresAnteproyectoRepository extends JpaRepository<EvaluadoresAnteproyecto, Long> {
    
    Optional<EvaluadoresAnteproyecto> findByIdProyecto(Long idProyecto);
    
    List<EvaluadoresAnteproyecto> findByEvaluador1EmailOrEvaluador2Email(String evaluador1, String evaluador2);
    
    List<EvaluadoresAnteproyecto> findByJefeDepartamentoEmail(String jefeEmail);
    
    boolean existsByIdProyecto(Long idProyecto);
}