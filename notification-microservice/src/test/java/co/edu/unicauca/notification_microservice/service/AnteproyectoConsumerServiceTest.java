package co.edu.unicauca.notification_microservice.service;

import co.edu.unicauca.notification_microservice.entity.AnteproyectoNotificacion;
import co.edu.unicauca.notification_microservice.repository.AnteproyectoNotificacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnteproyectoConsumerServiceTest {

    @InjectMocks
    private AnteproyectoConsumerService consumerService;

    @Mock
    private AnteproyectoNotificacionRepository repository;

    @Test
    void debeGuardarYLoggearNotificacionDeAnteproyecto() {
        AnteproyectoNotificacion notificacion = new AnteproyectoNotificacion();
        notificacion.setIdProyecto(1L);
        notificacion.setTitulo("Sistema de Gestión");
        notificacion.setJefeDepartamentoEmail("jefe@unicauca.edu.co");
        notificacion.setEstudianteEmail("estudiante@unicauca.edu.co");
        notificacion.setTutor1Email("docente@unicauca.edu.co");

        consumerService.handle(notificacion);

        verify(repository, times(1)).save(notificacion);
    }
}