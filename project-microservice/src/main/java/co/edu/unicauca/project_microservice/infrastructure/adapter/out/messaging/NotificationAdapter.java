// infrastructure/adapter/out/messaging/NotificationAdapter.java
package co.edu.unicauca.project_microservice.infrastructure.adapter.out.messaging;

import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;
import co.edu.unicauca.project_microservice.domain.port.out.NotificationPort;
import co.edu.unicauca.project_microservice.infrastructure.dto.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adaptador que implementa el puerto de salida de notificaciones
 * Utiliza RabbitMQ para publicar eventos
 */
@Component
public class NotificationAdapter implements NotificationPort {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKeyFormatoA;
    private final String routingKeyEvaluado;
    private final String routingKeyAnteproyecto;
    private final String routingKeyAsignacionEvaluadores;

    public NotificationAdapter(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.exchange}") String exchange,
            @Value("${app.rabbitmq.routingkey.formato_a_subido}") String routingKeyFormatoA,
            @Value("${app.rabbitmq.routingkey.evaluado}") String routingKeyEvaluado,
            @Value("${app.rabbitmq.routingkey.anteproyecto}") String routingKeyAnteproyecto,
            @Value("${app.rabbitmq.routingkey.asignacion.evaluadores}") String routingKeyAsignacionEvaluadores) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKeyFormatoA = routingKeyFormatoA;
        this.routingKeyEvaluado = routingKeyEvaluado;
        this.routingKeyAnteproyecto = routingKeyAnteproyecto;
        this.routingKeyAsignacionEvaluadores = routingKeyAsignacionEvaluadores;
    }

    @Override
    public void notificarFormatoASubido(ProyectoGrado p) {
        FormatoASubidoEvent event = new FormatoASubidoEvent();
        event.setIdProyecto(p.getId());
        event.setTitulo(p.getTitulo());
        event.setCoordinadorEmail("coordinador.sistemas@unicauca.edu.co");
        
        rabbitTemplate.convertAndSend(exchange, routingKeyFormatoA, event);
    }

    @Override
    public void notificarEvaluacion(ProyectoGrado p, boolean aprobado, String observaciones) {
        ProyectoEvaluadoEvent event = new ProyectoEvaluadoEvent();
        event.setIdProyecto(p.getId());
        event.setAprobado(aprobado);
        event.setObservaciones(observaciones);
        event.setDestinatarios(new String[]{
            p.getDirectorEmail(), 
            p.getEstudiante1Email()
        });
        
        rabbitTemplate.convertAndSend(exchange, routingKeyEvaluado, event);
    }

    @Override
    public void notificarAnteproyectoSubido(ProyectoGrado p, String jefeEmail) {
        AnteproyectoSubidoEvent event = new AnteproyectoSubidoEvent();
        event.setIdProyecto(p.getId());
        event.setTitulo(p.getTitulo());
        event.setJefeDepartamentoEmail(jefeEmail);
        event.setEstudianteEmail(p.getEstudiante1Email());
        event.setTutor1Email(p.getDirectorEmail());
        event.setTutor2Email(p.getCodirectorEmail());
        
        rabbitTemplate.convertAndSend(exchange, routingKeyAnteproyecto, event);
    }

    @Override
    public void notificarAsignacionEvaluadores(ProyectoGrado p, String e1, String e2) {
        AsignacionEvaluadoresEvent event = new AsignacionEvaluadoresEvent();
        event.setIdProyecto(p.getId());
        event.setTituloProyecto(p.getTitulo());
        event.setEvaluador1Email(e1);
        event.setEvaluador2Email(e2);
        event.setEstudianteEmail(p.getEstudiante1Email());
        
        rabbitTemplate.convertAndSend(exchange, routingKeyAsignacionEvaluadores, event);
    }

    @Override
    public void notificarResultadoEvaluacion(ProyectoGrado p, boolean aprobado, String observaciones) {
        // Reutiliza el método de evaluación general
        notificarEvaluacion(p, aprobado, observaciones);
    }
}