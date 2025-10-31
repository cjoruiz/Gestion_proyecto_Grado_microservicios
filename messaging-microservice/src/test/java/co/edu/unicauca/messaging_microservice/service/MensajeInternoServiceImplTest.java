package co.edu.unicauca.messaging_microservice.service;

import co.edu.unicauca.messaging_microservice.entity.MensajeInterno;
import co.edu.unicauca.messaging_microservice.infra.dto.MensajeInternoRequest;
import co.edu.unicauca.messaging_microservice.repository.MensajeInternoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensajeInternoServiceImplTest {

    @InjectMocks
    private MensajeInternoServiceImpl mensajeService;

    @Mock
    private MensajeInternoRepository mensajeRepository;

    @Test
    void debeEnviarMensajeSinAdjunto() throws Exception {
        MensajeInternoRequest request = new MensajeInternoRequest();
        request.setRemitenteEmail("estudiante@unicauca.edu.co");
        request.setDestinatariosEmail("docente@unicauca.edu.co");
        request.setAsunto("Propuesta");
        request.setCuerpo("Adjunto mi propuesta");

        MensajeInterno guardado = new MensajeInterno();
        guardado.setId(1L);

        when(mensajeRepository.save(any(MensajeInterno.class))).thenReturn(guardado);

        Long id = mensajeService.enviarMensaje(request);

        assertEquals(1L, id);
        verify(mensajeRepository, times(1)).save(any(MensajeInterno.class));
    }

    @Test
    void debeEnviarMensajeConAdjunto() throws Exception {
        MockMultipartFile adjunto = new MockMultipartFile(
                "archivo", "propuesta.pdf", "application/pdf", "pdf content".getBytes()
        );

        MensajeInternoRequest request = new MensajeInternoRequest();
        request.setRemitenteEmail("estudiante@unicauca.edu.co");
        request.setDestinatariosEmail("docente@unicauca.edu.co");
        request.setAsunto("Propuesta con PDF");
        request.setCuerpo("Adjunto mi propuesta en PDF");
        request.setDocumentoAdjunto(adjunto);

        MensajeInterno guardado = new MensajeInterno();
        guardado.setId(2L);

        when(mensajeRepository.save(any(MensajeInterno.class))).thenReturn(guardado);

        Long id = mensajeService.enviarMensaje(request);

        assertEquals(2L, id);
        verify(mensajeRepository, times(1)).save(any(MensajeInterno.class));
    }

    @Test
    void debeLanzarExcepcionSiFaltanCampos() {
        MensajeInternoRequest request = new MensajeInternoRequest();
        request.setRemitenteEmail(null); //falta
        request.setDestinatariosEmail("docente@unicauca.edu.co");
        request.setAsunto("Sin remitente");
        request.setCuerpo("Cuerpo");

        assertThrows(IllegalArgumentException.class,
                () -> mensajeService.enviarMensaje(request));
    }

    @Test
    void debeObtenerMensajesEnviadosPorEstudiante() {
        MensajeInterno mensaje = new MensajeInterno();
        mensaje.setRemitenteEmail("estudiante@unicauca.edu.co");

        when(mensajeRepository.findByRemitenteEmail("estudiante@unicauca.edu.co"))
                .thenReturn(List.of(mensaje));

        List<MensajeInterno> resultado = mensajeService.obtenerMensajesEnviadosPorEstudiante("estudiante@unicauca.edu.co");

        assertEquals(1, resultado.size());
        assertEquals("estudiante@unicauca.edu.co", resultado.get(0).getRemitenteEmail());
    }

    @Test
    void debeObtenerMensajesRecibidosPorDocente() {
        MensajeInterno mensaje = new MensajeInterno();
        mensaje.setDestinatariosEmail("docente@unicauca.edu.co");

        when(mensajeRepository.findByDestinatariosEmailContaining("docente@unicauca.edu.co"))
                .thenReturn(List.of(mensaje));

        List<MensajeInterno> resultado = mensajeService.obtenerMensajesRecibidosPorDocente("docente@unicauca.edu.co");

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getDestinatariosEmail().contains("docente@unicauca.edu.co"));
    }
}