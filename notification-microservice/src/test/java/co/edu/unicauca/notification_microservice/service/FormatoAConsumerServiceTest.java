package co.edu.unicauca.notification_microservice.service;

import co.edu.unicauca.notification_microservice.entity.FormatoANotificacion;
import co.edu.unicauca.notification_microservice.repository.FormatoANotificacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormatoAConsumerServiceTest {

    @InjectMocks
    private FormatoAConsumerService consumerService;

    @Mock
    private FormatoANotificacionRepository repository;

    @Test
    void debeGuardarYLoggearNotificacionDeFormatoA() {
        FormatoANotificacion notificacion = new FormatoANotificacion();
        notificacion.setIdProyecto(1L);
        notificacion.setTitulo("Sistema de Gestión");
        notificacion.setCoordinadorEmail("coordinador@unicauca.edu.co");

        consumerService.handle(notificacion);

        verify(repository, times(1)).save(notificacion);
    }
}