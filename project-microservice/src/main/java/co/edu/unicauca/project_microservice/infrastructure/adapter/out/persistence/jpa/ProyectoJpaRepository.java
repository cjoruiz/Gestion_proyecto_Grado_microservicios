package co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.edu.unicauca.project_microservice.infrastructure.adapter.out.persistence.entity.ProyectoGradoEntity;

import java.util.List;

@Repository
public interface ProyectoJpaRepository extends JpaRepository<ProyectoGradoEntity, Long> {
    List<ProyectoGradoEntity> findByEstudiante1Email(String email);
    List<ProyectoGradoEntity> findByDirectorEmail(String email);
    List<ProyectoGradoEntity> findByDirectorEmailOrCodirectorEmail(String directorEmail, String codirectorEmail);

    @Query("SELECT p FROM ProyectoGradoEntity p WHERE p.estudiante1Email = :email OR p.estudiante2Email = :email")
    List<ProyectoGradoEntity> findByEstudiante1EmailOrEstudiante2Email(String email);
}