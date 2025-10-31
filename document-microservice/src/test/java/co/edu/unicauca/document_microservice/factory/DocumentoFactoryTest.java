package co.edu.unicauca.document_microservice.factory;

import co.edu.unicauca.document_microservice.utilities.validator.DocumentoValidator;
import co.edu.unicauca.document_microservice.utilities.validator.PdfValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentoFactoryTest {

    @Test
    void debeRetornarPdfValidatorParaFormatoA() {
        DocumentoFactory factory = DocumentoFactory.getInstance();
        DocumentoValidator validator = factory.crearValidator("FORMATO_A");
        assertInstanceOf(PdfValidator.class, validator);
    }

    @Test
    void debeLanzarExcepcionParaTipoNoSoportado() {
        DocumentoFactory factory = DocumentoFactory.getInstance();
        assertThrows(IllegalArgumentException.class, () -> factory.crearValidator("WORD"));
    }
}