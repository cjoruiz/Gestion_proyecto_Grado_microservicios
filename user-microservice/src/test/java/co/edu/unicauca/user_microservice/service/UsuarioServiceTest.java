package co.edu.unicauca.user_microservice.service;

import co.edu.unicauca.user_microservice.entity.Docente;
import co.edu.unicauca.user_microservice.entity.Estudiante;
import co.edu.unicauca.user_microservice.entity.Usuario;
import co.edu.unicauca.user_microservice.repository.UsuarioRepository;
import co.edu.unicauca.user_microservice.utilities.exception.InvalidUserDataException;
import co.edu.unicauca.user_microservice.utilities.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void debeRegistrarEstudianteCorrectamente() throws Exception {
        Estudiante estudiante = new Estudiante();
        estudiante.setEmail("estudiante@unicauca.edu.co");
        estudiante.setPassword("Stud123!");
        estudiante.setNombres("Ana");
        estudiante.setApellidos("López");
        estudiante.setPrograma("INGENIERIA_SISTEMAS");

        when(usuarioRepository.existsById("estudiante@unicauca.edu.co")).thenReturn(false);
        when(usuarioRepository.save(estudiante)).thenReturn(estudiante);

        Usuario resultado = usuarioService.registrarEstudiante(estudiante);

        assertNotNull(resultado);
        assertEquals("estudiante@unicauca.edu.co", resultado.getEmail());
        verify(usuarioRepository, times(1)).save(estudiante);
    }

    @Test
    void debeLanzarExcepcionSiEmailNoEsInstitucional() {
        Estudiante estudiante = new Estudiante();
        estudiante.setEmail("estudiante@gmail.com");
        estudiante.setPassword("Stud123!");

        assertThrows(InvalidUserDataException.class,
                () -> usuarioService.registrarEstudiante(estudiante));
    }

    @Test
    void debeLanzarExcepcionSiContrasenaEsDebil() {
        Estudiante estudiante = new Estudiante();
        estudiante.setEmail("estudiante@unicauca.edu.co");
        estudiante.setPassword("123");

        assertThrows(InvalidUserDataException.class,
                () -> usuarioService.registrarEstudiante(estudiante));
    }

    @Test
    void debeLanzarExcepcionSiUsuarioYaExiste() {
        Estudiante estudiante = new Estudiante();
        estudiante.setEmail("estudiante@unicauca.edu.co");
        estudiante.setPassword("Stud123!");

        when(usuarioRepository.existsById("estudiante@unicauca.edu.co")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> usuarioService.registrarEstudiante(estudiante));
    }

    @Test
    void debeObtenerRolDeDocente() {
        Docente docente = new Docente();
        docente.setEmail("docente@unicauca.edu.co");

        when(usuarioRepository.findById("docente@unicauca.edu.co")).thenReturn(Optional.of(docente));

        String rol = usuarioService.obtenerRol("docente@unicauca.edu.co");

        assertEquals("DOCENTE", rol);
    }

    @Test
    void debeObtenerRolDesconocidoSiNoExiste() {
        when(usuarioRepository.findById("noexiste@unicauca.edu.co")).thenReturn(Optional.empty());

        String rol = usuarioService.obtenerRol("noexiste@unicauca.edu.co");

        assertEquals("DESCONOCIDO", rol);
    }
}