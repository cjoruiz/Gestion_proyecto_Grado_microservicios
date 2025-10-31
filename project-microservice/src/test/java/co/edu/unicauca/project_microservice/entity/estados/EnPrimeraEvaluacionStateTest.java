package co.edu.unicauca.project_microservice.entity.estados;

import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnPrimeraEvaluacionStateTest {

    private final EnPrimeraEvaluacionState estado = new EnPrimeraEvaluacionState();

    @Test
    void debeCambiarAAprobadoSiSeAprueba() {
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setEstado(estado);

        estado.evaluar(proyecto, true, "Bien hecho");

        assertEquals("FORMATO_A_APROBADO", proyecto.getEstadoActual());
        assertEquals("Bien hecho", proyecto.getObservacionesEvaluacion());
    }

    @Test
    void debeCambiarARechazadoSiSeRechaza() {
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setEstado(estado);

        estado.evaluar(proyecto, false, "Faltan datos");

        assertEquals("FORMATO_A_RECHAZADO", proyecto.getEstadoActual());
        assertEquals("Faltan datos", proyecto.getObservacionesEvaluacion());
    }

    @Test
    void debeLanzarExcepcionSiSeReintenta() {
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setEstado(estado);

        assertThrows(IllegalStateException.class, () -> estado.reintentar(proyecto));
    }
}