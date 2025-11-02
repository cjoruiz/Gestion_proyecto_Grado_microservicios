# messaging-microservice

Microservicio para la mensajería interna entre estudiantes y docentes (heredado del monolítico).

## Funcionalidades
- Envío de mensajes con archivo adjunto (ej: Formato A inicial).
- Consulta de mensajes enviados por un estudiante.
- Consulta de mensajes recibidos por un docente.
- Persistencia de mensajes en H2.

## Tecnologías
- Spring Boot 3
- Spring Data JPA + H2 (modo archivo, persistente)

## Endpoints
- `POST /api/mensajes/enviar`
- `GET /api/mensajes/enviados/{email}`
- `GET /api/mensajes/recibidos/{email}`

## Puerto
- `8085`