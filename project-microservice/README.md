# project-microservice

Microservicio que gestiona el ciclo de vida de los proyectos de grado, incluyendo estados, evaluaciones y anteproyectos.

## Funcionalidades
- Creación de proyectos de grado.
- Evaluación de Formato A (aprobar/rechazar) con notificación asíncrona.
- Reintento de Formato A (hasta 3 intentos) con notificación.
- Subida de anteproyecto tras aprobación del Formato A.
- Consulta de proyectos por estudiante.
- Consulta de anteproyectos por jefe de departamento.
- Validación de usuarios mediante Feign (comunicación con `user-microservice`).

## Patrones de diseño
- **State**: Modela los estados del proyecto.
- **Template Method**: Estandariza el flujo de evaluación.
- **Facade**: Simplifica la interacción del frontend.
- **Adapter**: Feign Client para consumir `user-microservice`.

## Tecnologías
- Spring Boot 3
- Spring Cloud OpenFeign
- Spring AMQP + RabbitMQ
- H2 (modo archivo, persistente)

## Endpoints
- `POST /api/proyectos`
- `POST /api/proyectos/{id}/evaluar`
- `POST /api/proyectos/{id}/reintentar`
- `POST /api/proyectos/{id}/anteproyecto`
- `GET /api/proyectos/estudiante/{email}`
- `GET /api/proyectos/anteproyectos/jefe/{email}`

## Puerto
- `8082`