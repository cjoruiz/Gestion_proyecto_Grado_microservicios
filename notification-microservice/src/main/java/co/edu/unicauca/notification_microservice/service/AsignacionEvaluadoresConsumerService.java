package co.edu.unicauca.notification_microservice.service;

import co.edu.unicauca.notification_microservice.entity.AsignacionEvaluadoresNotificacion;
import co.edu.unicauca.notification_microservice.repository.AsignacionEvaluadoresNotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsignacionEvaluadoresConsumerService {

    private static final Logger log = LoggerFactory.getLogger(AsignacionEvaluadoresConsumerService.class);

    @Autowired
    private AsignacionEvaluadoresNotificacionRepository repo;

    @RabbitListener(queues = "asignacion.evaluadores")
    public void handleAsignacionEvaluadores(AsignacionEvaluadoresNotificacion event) {
        log.info("=== NOTIFICACIÓN DE ASIGNACIÓN DE EVALUADORES ===");
        log.info("Proyecto ID: {}", event.getIdProyecto());
        log.info("Título: '{}'", event.getTituloProyecto());
        log.info("Evaluador 1: {}", event.getEvaluador1Email());
        log.info("Evaluador 2: {}", event.getEvaluador2Email());
        log.info("Estudiante: {}", event.getEstudianteEmail());

        // Aquí puedes: enviar correos reales, persistir, etc.
        AsignacionEvaluadoresNotificacion notif = new AsignacionEvaluadoresNotificacion();
        notif.setIdProyecto(event.getIdProyecto());
        notif.setEvaluador1Email(event.getEvaluador1Email());
        notif.setEvaluador2Email(event.getEvaluador2Email());
        notif.setEstudianteEmail(event.getEstudianteEmail());
        repo.save(notif);

        // Opcional: enviar correos ficticios (solo logs por ahora)
        System.out.println("Notificación a " + event.getEvaluador1Email() + ": Asignado como evaluador del anteproyecto '" + event.getTituloProyecto() + "'");
        System.out.println("Notificación a " + event.getEvaluador2Email() + ": Asignado como evaluador del anteproyecto '" + event.getTituloProyecto() + "'");
    }
}