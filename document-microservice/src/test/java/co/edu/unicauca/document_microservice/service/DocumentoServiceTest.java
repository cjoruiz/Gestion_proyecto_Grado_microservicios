package co.edu.unicauca.document_microservice.service;

import co.edu.unicauca.document_microservice.entity.Documento;
import co.edu.unicauca.document_microservice.infra.dto.DocumentoRequest;
import co.edu.unicauca.document_microservice.repository.DocumentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentoServiceTest {

    @InjectMocks
    private DocumentoService documentoService;

    @Mock
    private DocumentoRepository documentoRepository;

    @Test
    void debeGuardarDocumentoCorrectamente() throws Exception {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo", "test.pdf", "application/pdf", "contenido".getBytes()
        );

        DocumentoRequest request = new DocumentoRequest();
        request.setIdProyecto(1L);
        request.setTipoDocumento("FORMATO_A");
        request.setArchivo(archivo);

        Documento documentoGuardado = new Documento();
        documentoGuardado.setId(1L);
        documentoGuardado.setTipoDocumento("FORMATO_A");

        when(documentoRepository.save(any(Documento.class))).thenReturn(documentoGuardado);

        Documento resultado = documentoService.subirDocumento(request);

        assertNotNull(resultado);
        assertEquals("FORMATO_A", resultado.getTipoDocumento());
        verify(documentoRepository, times(1)).save(any(Documento.class));
    }
}