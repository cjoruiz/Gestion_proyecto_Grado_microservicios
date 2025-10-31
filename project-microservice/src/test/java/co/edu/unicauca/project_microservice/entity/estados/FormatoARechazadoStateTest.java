package co.edu.unicauca.project_microservice.entity.estados;

import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormatoARechazadoStateTest {

    private final FormatoARechazadoState estado = new FormatoARechazadoState();

    @Test
    void debeCambiarASegundaEvaluacionAlReintentar() {
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setEstado(estado);
        proyecto.setNumeroIntento(1);

        estado.reintentar(proyecto);

        assertEquals("EN_SEGUNDA_EVALUACION_FORMATO_A", proyecto.getEstadoActual());
        assertEquals(2, proyecto.getNumeroIntento());
    }

    @Test
    void debeLanzarExcepcionSiSeEvaluaDirectamente() {
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setEstado(estado);

        assertThrows(IllegalStateException.class,
                () -> estado.evaluar(proyecto, true, "No se puede evaluar sin reintentar"));
    }
}