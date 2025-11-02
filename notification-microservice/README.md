# notification-microservice

Microservicio consumidor de eventos para el envío asíncrono de notificaciones.

## Funcionalidades
- Escucha eventos de RabbitMQ:
  - `formato-a.submitted` → notifica al coordinador.
  - `proyecto.evaluado` → notifica a docentes y estudiantes.
  - `anteproyecto.submitted` → notifica al jefe de departamento, estudiante y tutores.
- Simulación de envío de correos mediante `Logger`.
- Persistencia de eventos en base de datos local (H2).

## Patrones de diseño
- **Observer** (implícito en el consumo de eventos).
- **Singleton** (no implementado explícitamente, pero el servicio es singleton por Spring).

## Tecnologías
- Spring Boot 3
- Spring AMQP + RabbitMQ
- H2 (modo archivo, persistente)

## Puerto
- `8084`

> **Nota**: Este microservicio no expone endpoints REST.