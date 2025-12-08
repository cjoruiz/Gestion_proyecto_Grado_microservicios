package co.edu.unicauca.user_microservice.repository;

import co.edu.unicauca.user_microservice.entity.Docente;
import co.edu.unicauca.user_microservice.entity.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    // 👇 Nuevo método para encontrar docentes por programa
    @Query("SELECT d FROM Docente d WHERE d.programa = :programa")
    List<Docente> findDocentesByPrograma(@Param("programa") String programa);
}