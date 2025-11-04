package co.edu.unicauca.project_microservice.service;

import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import co.edu.unicauca.project_microservice.repository.ProyectoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProyectoServiceTest {

    @InjectMocks
    private ProyectoService proyectoService;

    @Mock
    private ProyectoRepository proyectoRepository;

    @Test
    void debeRetornarProyectoPorId() {
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setId(1L);
        // IMPORTANTE: Establecer un estadoActual válido para evitar NullPointerException
        proyecto.setEstadoActual("EN_PRIMERA_EVALUACION_FORMATO_A");

        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));

        ProyectoGrado resultado = proyectoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(proyectoRepository, times(1)).findById(1L);
    }

    @Test
    void debeLanzarExcepcionSiNoExiste() {
        when(proyectoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> proyectoService.obtenerPorId(999L));
        verify(proyectoRepository, times(1)).findById(999L);
    }
}