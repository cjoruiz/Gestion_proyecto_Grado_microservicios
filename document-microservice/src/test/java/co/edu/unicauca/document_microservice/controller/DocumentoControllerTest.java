package co.edu.unicauca.document_microservice.controller;

import co.edu.unicauca.document_microservice.entity.Documento;
import co.edu.unicauca.document_microservice.service.IDocumentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentoController.class)
class DocumentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDocumentoService documentoService;

    @Test
    void debeSubirDocumentoYRetornar200() throws Exception {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo", "test.pdf", "application/pdf", "contenido".getBytes()
        );

        Documento documento = new Documento();
        documento.setId(1L);
        documento.setTipoDocumento("FORMATO_A");

        when(documentoService.subirDocumento(any())).thenReturn(documento);

        mockMvc.perform(multipart("/api/documentos/subir")
                        .file(archivo)
                        .param("idProyecto", "1")
                        .param("tipoDocumento", "FORMATO_A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoDocumento").value("FORMATO_A"));
    }
}