package co.edu.unicauca.project_microservice.service.evaluacion;

import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import co.edu.unicauca.project_microservice.entity.estados.FormatoARechazadoState;
import co.edu.unicauca.project_microservice.entity.estados.RechazadoDefinitivoState;
import co.edu.unicauca.project_microservice.infra.dto.ProyectoEvaluadoEvent;
import org.springframework.stereotype.Component;

@Component
public class EvaluadorRechazo extends EvaluadorProyecto {

    @Override
    protected void aplicarEvaluacion(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        proyecto.setObservacionesEvaluacion(observaciones);

        int intento = proyecto.getNumeroIntento();

        if (intento >= 3) {               // tercer rechazo
            proyecto.setEstado(new RechazadoDefinitivoState());
        } else {                          // primer o segundo rechazo
            proyecto.setEstado(new FormatoARechazadoState());
        }
    }

    @Override
    protected void enviarNotificacion(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        ProyectoEvaluadoEvent event = new ProyectoEvaluadoEvent();
        event.setIdProyecto(proyecto.getId());
        event.setAprobado(false);
        event.setObservaciones(observaciones);
        event.setDestinatarios(new String[]{
            proyecto.getDirectorEmail(),
            proyecto.getEstudiante1Email()
        });
        notificationClient.notificarEvaluacion(event);
    }
}