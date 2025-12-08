package co.edu.unicauca.notification_microservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unicauca.notification_microservice.entity.AsignacionEvaluadoresNotificacion;

public interface AsignacionEvaluadoresNotificacionRepository 
    extends JpaRepository<AsignacionEvaluadoresNotificacion, Long> {
}