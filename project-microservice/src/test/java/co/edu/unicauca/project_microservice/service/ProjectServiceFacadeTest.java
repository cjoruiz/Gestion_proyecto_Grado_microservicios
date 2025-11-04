package co.edu.unicauca.project_microservice.service;

import co.edu.unicauca.project_microservice.Client.UserClient;
import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import co.edu.unicauca.project_microservice.entity.estados.*;
import co.edu.unicauca.project_microservice.infra.dto.FormatoASubidoEvent;
import co.edu.unicauca.project_microservice.infra.dto.ProyectoEvaluadoEvent;
import co.edu.unicauca.project_microservice.service.evaluacion.EvaluadorAprobacion;
import co.edu.unicauca.project_microservice.service.evaluacion.EvaluadorRechazo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceFacadeTest {

    @InjectMocks
    private ProjectServiceFacade facade;

    @Mock
    private IProyectoService proyectoService;

    @Mock
    private UserClient userClient;

    @Mock
    private INotificationServiceClient notificationClient;

    @Mock
    private EnPrimeraEvaluacionState enPrimeraEvaluacionState;

    @Mock
    private EvaluadorAprobacion evaluadorAprobacion;

    @Mock
    private EvaluadorRechazo evaluadorRechazo;

    @Mock
    private FormatoARechazadoState formatoARechazadoState;

    // --- CASO: Estudiante no existe ---
    @Test
    void debeLanzarErrorSiEstudianteNoExiste() {
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setDirectorEmail("docente@unicauca.edu.co");
        proyecto.setEstudiante1Email("noexiste@unicauca.edu.co");

        when(userClient.validarUsuario("docente@unicauca.edu.co"))
                .thenReturn(Map.of("existe", true, "rol", "DOCENTE"));
        when(userClient.validarUsuario("noexiste@unicauca.edu.co"))
                .thenReturn(Map.of("existe", false));

        Exception ex = assertThrows(RuntimeException.class,
                () -> facade.crearProyecto(proyecto));

        assertTrue(ex.getMessage().contains("no existe"));
        verify(proyectoService, never()).crear(any());
    }

    // --- CASO: Rol incorrecto ---
    @Test
    void debeLanzarErrorSiRolNoCoincide() {
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setDirectorEmail("estudiante@unicauca.edu.co"); // estudiante como director

        when(userClient.validarUsuario("estudiante@unicauca.edu.co"))
                .thenReturn(Map.of("existe", true, "rol", "ESTUDIANTE"));

        Exception ex = assertThrows(RuntimeException.class,
                () -> facade.crearProyecto(proyecto));

        assertTrue(ex.getMessage().contains("no es un DOCENTE"));
        verify(proyectoService, never()).crear(any());
    }

    // --- CASO: Reintento válido ---
    @Test
    void debePermitirReintentoEnEstadoRechazado() {
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setId(1L);
        proyecto.setNumeroIntento(1);
        proyecto.setEstadoActual("FORMATO_A_RECHAZADO");
        
        // Inyectar el estado mock usando reflection para evitar la excepción
        ReflectionTestUtils.setField(proyecto, "estado", formatoARechazadoState);

        when(proyectoService.obtenerPorId(1L)).thenReturn(proyecto);
        
        // Configurar el comportamiento del mock para simular reintentar
        doAnswer(invocation -> {
            ProyectoGrado p = invocation.getArgument(0);
            p.setEstadoActual("EN_SEGUNDA_EVALUACION_FORMATO_A");
            p.setNumeroIntento(p.getNumeroIntento() + 1);
            return null;
        }).when(formatoARechazadoState).reintentar(proyecto);

        facade.reintentarProyecto(1L);

        assertEquals("EN_SEGUNDA_EVALUACION_FORMATO_A", proyecto.getEstadoActual());
        assertEquals(2, proyecto.getNumeroIntento());
        verify(proyectoService).guardar(proyecto);
    }

    // --- CASO: Evaluar con aprobado ---
    @Test
    void debeEvaluarProyectoComoAprobado() {
        facade.evaluarProyecto(1L, true, "Todo bien");

        verify(evaluadorAprobacion, times(1))
                .evaluarProyecto(1L, true, "Todo bien");
        verifyNoInteractions(evaluadorRechazo);
    }

    // --- CASO: Evaluar con rechazado ---
    @Test
    void debeEvaluarProyectoComoRechazado() {
        facade.evaluarProyecto(1L, false, "Faltan datos");

        verify(evaluadorRechazo, times(1))
                .evaluarProyecto(1L, false, "Faltan datos");
        verifyNoInteractions(evaluadorAprobacion);
    }
}