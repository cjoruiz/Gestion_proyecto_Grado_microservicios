package co.edu.unicauca.notification_microservice.service;

import co.edu.unicauca.notification_microservice.entity.EvaluacionNotificacion;
import co.edu.unicauca.notification_microservice.repository.EvaluacionNotificacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluacionConsumerServiceTest {

    @InjectMocks
    private EvaluacionConsumerService consumerService;

    @Mock
    private EvaluacionNotificacionRepository repository;

    @Test
    void debeGuardarYLoggearNotificacionDeEvaluacion() {
        EvaluacionNotificacion notificacion = new EvaluacionNotificacion();
        notificacion.setIdProyecto(1L);
        notificacion.setAprobado(true);
        notificacion.setObservaciones("Todo correcto");
        notificacion.setDestinatarios(new String[]{"estudiante@unicauca.edu.co", "docente@unicauca.edu.co"});

        consumerService.handle(notificacion);

        verify(repository, times(1)).save(notificacion);
    }
}