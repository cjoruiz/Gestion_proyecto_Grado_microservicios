package co.edu.unicauca.project_microservice.service.evaluacion;

import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import co.edu.unicauca.project_microservice.entity.estados.EnPrimeraEvaluacionState;
import co.edu.unicauca.project_microservice.infra.dto.ProyectoEvaluadoEvent;
import co.edu.unicauca.project_microservice.service.INotificationServiceClient;
import co.edu.unicauca.project_microservice.service.IProyectoService;
import co.edu.unicauca.project_microservice.service.evaluacion.EvaluadorAprobacion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluadorAprobacionTest {

    @InjectMocks
    private EvaluadorAprobacion evaluador;

    @Mock
    private IProyectoService proyectoService;

    @Mock
    private INotificationServiceClient notificationClient;

    @Test
    void debeAprobarProyectoYNotificar() {
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setId(1L);
        proyecto.setEstado(new EnPrimeraEvaluacionState());
        proyecto.setDirectorEmail("docente@unicauca.edu.co");
        proyecto.setEstudiante1Email("estudiante@unicauca.edu.co");

        when(proyectoService.obtenerPorId(1L)).thenReturn(proyecto);

        evaluador.evaluarProyecto(1L, true, "Proyecto aprobado");

        verify(proyectoService).guardar(proyecto);
        verify(notificationClient).notificarEvaluacion(any(ProyectoEvaluadoEvent.class));
    }
}