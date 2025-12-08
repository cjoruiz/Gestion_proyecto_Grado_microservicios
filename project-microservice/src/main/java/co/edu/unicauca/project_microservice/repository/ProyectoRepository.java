package co.edu.unicauca.project_microservice.repository;

import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import feign.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<ProyectoGrado, Long> {
    List<ProyectoGrado> findByEstudiante1Email(String email);
    List<ProyectoGrado> findByDirectorEmail(String email);
    List<ProyectoGrado> findByDirectorEmailOrCodirectorEmail(String directorEmail, String codirectorEmail);
    @Query("SELECT p FROM ProyectoGrado p WHERE p.estudiante1Email = :email OR p.estudiante2Email = :email")
    List<ProyectoGrado> findByEstudiante1EmailOrEstudiante2Email(@Param("email") String email);
}