package co.edu.unicauca.document_microservice.utilities.validator;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class PdfValidatorTest {

    private final PdfValidator validator = new PdfValidator();

    @Test
    void debeAceptarArchivoPDF() {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo", "test.pdf", "application/pdf", "fake".getBytes()
        );

        assertDoesNotThrow(() -> validator.validar(archivo));
    }

    @Test
    void debeRechazarArchivoNoPDF() {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo", "test.txt", "text/plain", "fake".getBytes()
        );

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validar(archivo));
        assertTrue(ex.getMessage().contains("debe ser un PDF"));
    }
}