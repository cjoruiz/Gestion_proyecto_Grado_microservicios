package co.edu.unicauca.messaging_microservice.controller;

import co.edu.unicauca.messaging_microservice.entity.MensajeInterno;
import co.edu.unicauca.messaging_microservice.service.IMensajeInternoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessagingController.class)
class MessagingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IMensajeInternoService mensajeService;

    @Test
    void debeEnviarMensajeYRetornar200() throws Exception {
        when(mensajeService.enviarMensaje(any())).thenReturn(1L);

        mockMvc.perform(multipart("/api/mensajes/enviar")
                        .param("remitenteEmail", "estudiante@unicauca.edu.co")
                        .param("destinatariosEmail", "docente@unicauca.edu.co")
                        .param("asunto", "Asunto")
                        .param("cuerpo", "Cuerpo del mensaje"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("SUCCESS"));
    }

    @Test
    void debeObtenerMensajesEnviados() throws Exception {
        MensajeInterno mensaje = new MensajeInterno();
        mensaje.setRemitenteEmail("estudiante@unicauca.edu.co");

        when(mensajeService.obtenerMensajesEnviadosPorEstudiante("estudiante@unicauca.edu.co"))
                .thenReturn(List.of(mensaje));

        mockMvc.perform(get("/api/mensajes/enviados/estudiante@unicauca.edu.co"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].remitenteEmail").value("estudiante@unicauca.edu.co"));
    }

    @Test
    void debeObtenerMensajesRecibidos() throws Exception {
        MensajeInterno mensaje = new MensajeInterno();
        mensaje.setDestinatariosEmail("docente@unicauca.edu.co");

        when(mensajeService.obtenerMensajesRecibidosPorDocente("docente@unicauca.edu.co"))
                .thenReturn(List.of(mensaje));

        mockMvc.perform(get("/api/mensajes/recibidos/docente@unicauca.edu.co"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].destinatariosEmail").value("docente@unicauca.edu.co"));
    }
}