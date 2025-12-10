Sistema de Gestión de Proyectos de Grado - Microservicio de Proyectos
Descripción del Proyecto
Microservicio encargado de la gestión del ciclo de vida completo de proyectos de grado, desde la creación del Formato A hasta la evaluación de anteproyectos. Implementa patrones de diseño y arquitectura hexagonal para garantizar escalabilidad y mantenibilidad.
Tecnologías Utilizadas

Java 17
Spring Boot 3.x
Spring Security con OAuth2/JWT
Spring Data JPA
H2 Database (desarrollo) / MySQL (producción)
RabbitMQ para mensajería asíncrona
Keycloak para autenticación y autorización
OpenFeign para comunicación entre microservicios
Swagger/OpenAPI para documentación de API
Lombok para reducción de código boilerplate
Maven como gestor de dependencias

Arquitectura
Patrón Hexagonal (Ports & Adapters)
El proyecto sigue una arquitectura hexagonal que separa claramente las responsabilidades:
project_microservice/
├── domain/                    # Núcleo del negocio
│   ├── model/                # Entidades de dominio
│   │   ├── ProyectoGrado.java
│   │   ├── EvaluadoresAnteproyecto.java
│   │   └── estados/          # Patrón State
│   │       ├── EstadoProyecto.java
│   │       ├── EnPrimeraEvaluacionState.java
│   │       ├── EnSegundaEvaluacionState.java
│   │       ├── EnTerceraEvaluacionState.java
│   │       ├── FormatoAAprobadoState.java
│   │       ├── FormatoARechazadoState.java
│   │       ├── RechazadoDefinitivoState.java
│   │       └── AnteproyectoEnviadoState.java
│   ├── port/
│   │   ├── in/               # Casos de uso
│   │   │   └── ProyectoUseCasePort.java
│   │   └── out/              # Interfaces de salida
│   │       ├── ProyectoRepositoryPort.java
│   │       ├── EvaluadoresRepositoryPort.java
│   │       └── NotificationPort.java
│   └── exception/            # Excepciones del dominio
│
├── application/              # Capa de aplicación
│   ├── ProyectoUseCase.java # Orquestación de casos de uso
│   ├── dto/                  # DTOs de comunicación
│   │   ├── CrearProyectoRequest.java
│   │   ├── ProyectoGradoDTO.java
│   │   └── EvaluadoresDTO.java
│   └── mapper/               # Mapeo entre capas
│       └── ProyectoDTOMapper.java
│
└── infrastructure/           # Adaptadores de infraestructura
    ├── adapter/
    │   ├── in/              # Adaptadores de entrada
    │   │   └── rest/
    │   │       └── ProjectController.java
    │   └── out/             # Adaptadores de salida
    │       ├── persistence/
    │       │   ├── ProyectoRepositoryAdapter.java
    │       │   ├── EvaluadoresRepositoryAdapter.java
    │       │   ├── entity/
    │       │   ├── jpa/
    │       │   ├── mapper/
    │       │   └── factory/
    │       │       └── EstadoProyectoFactory.java
    │       └── messaging/
    │           └── NotificationAdapter.java
    ├── config/              # Configuración
    │   ├── SecurityConfig.java
    │   └── RabbitMQConfig.java
    └── dto/                 # DTOs de eventos
Patrones de Diseño Implementados
1. State Pattern
Gestiona las transiciones de estado de un proyecto durante su ciclo de evaluación:

EnPrimeraEvaluacionState: Estado inicial tras creación
EnSegundaEvaluacionState: Después del primer rechazo
EnTerceraEvaluacionState: Después del segundo rechazo
FormatoAAprobadoState: Estado terminal de aprobación
FormatoARechazadoState: Estado de rechazo con posibilidad de reintento
RechazadoDefinitivoState: Estado terminal de rechazo
AnteproyectoEnviadoState: Estado tras aprobación y envío de anteproyecto

javapublic interface EstadoProyecto {
    void evaluar(ProyectoGrado proyecto, boolean aprobado, String observaciones);
    void reintentar(ProyectoGrado proyecto);
    String getNombreEstado();
}
2. Factory Method Pattern
Creación de instancias de estados basada en el nombre del estado persistido:
java@Component
public class EstadoProyectoFactory {
    public EstadoProyecto crear(String nombreEstado) {
        return switch (nombreEstado) {
            case "EN_PRIMERA_EVALUACION_FORMATO_A" -> new EnPrimeraEvaluacionState();
            case "EN_SEGUNDA_EVALUACION_FORMATO_A" -> new EnSegundaEvaluacionState();
            case "EN_TERCERA_EVALUACION_FORMATO_A" -> new EnTerceraEvaluacionState();
            case "FORMATO_A_APROBADO" -> new FormatoAAprobadoState();
            case "FORMATO_A_RECHAZADO" -> new FormatoARechazadoState();
            case "RECHAZADO_DEFINITIVO" -> new RechazadoDefinitivoState();
            case "ANTEPROYECTO_ENVIADO" -> new AnteproyectoEnviadoState();
            default -> throw new IllegalStateException("Estado desconocido");
        };
    }
}
3. Repository Pattern
Abstracción de la persistencia mediante interfaces en el dominio e implementaciones en infraestructura.
4. Dependency Injection
Uso de Spring Framework para inversión de control y manejo de dependencias.
5. DTO Pattern
Separación entre modelos de dominio y objetos de transferencia de datos para comunicación con el exterior.
Requisitos Funcionales Implementados
RF01: Crear Proyecto de Grado

Registro de información básica del proyecto (título, modalidad, director, codirector, estudiantes)
Validación de que el usuario autenticado sea el director
Asignación automática del estado inicial "EN_PRIMERA_EVALUACION_FORMATO_A"
Notificación al coordinador

RF02: Evaluar Formato A

Evaluación por parte del coordinador
Registro de observaciones
Transiciones de estado según aprobación/rechazo
Control de intentos (máximo 3)
Notificación a director y estudiantes

RF03: Consultar Proyectos

Por estudiante (solo sus proyectos)
Por director (proyectos donde es director)
Por docente (proyectos donde es director o codirector)
Por coordinador (proyectos en evaluación)
Por jefe de departamento (anteproyectos enviados)

RF04: Reintentar Proyecto

Permite al director reintentar tras rechazo
Incrementa contador de intentos
Transición a siguiente estado de evaluación
Validación de límite de reintentos

RF05: Subir Anteproyecto

Cambio de estado tras aprobación del Formato A
Notificación a jefe de departamento, estudiante y tutores
Registro de fecha de envío

RF08: Asignar Evaluadores

Asignación de dos evaluadores por parte del jefe de departamento
Validaciones:

Los evaluadores no pueden ser director ni codirector
Los evaluadores deben ser diferentes
Solo se puede asignar en estado ANTEPROYECTO_ENVIADO
No permite reasignación


Notificación a evaluadores asignados

RF09: Evaluar Anteproyecto

Registro de evaluación individual por cada evaluador
Almacenamiento de observaciones y decisión (aprobado/rechazado)
Procesamiento automático al completarse ambas evaluaciones
Aprobación requiere consenso de ambos evaluadores
Notificación de resultado a estudiante y director

Reglas de Negocio

Límite de Reintentos: Un proyecto puede ser rechazado máximo 3 veces, después pasa a "RECHAZADO_DEFINITIVO"
Validación de Director: Solo el director autenticado puede crear el proyecto
Secuencia de Estados: Los estados deben seguir una secuencia lógica definida por el patrón State
Evaluadores Independientes: Los evaluadores no pueden ser director ni codirector del proyecto
Consenso de Evaluación: El anteproyecto requiere aprobación de ambos evaluadores
Autorización por Rol: Cada operación está protegida por roles específicos (DOCENTE, COORDINADOR, JEFE_DEPARTAMENTO, ESTUDIANTE)

Seguridad
Autenticación y Autorización

OAuth2/JWT: Tokens JWT emitidos por Keycloak
Role-Based Access Control (RBAC): Control de acceso basado en roles del cliente "sistema-desktop"
Validación de Claims: Extracción y validación de email y roles desde el token
Protección de Endpoints: Anotaciones @PreAuthorize para control fino de acceso

Roles Definidos

DOCENTE: Crear proyectos, reintentar, subir anteproyectos, evaluar como evaluador
COORDINADOR: Evaluar Formato A, consultar todos los proyectos
ESTUDIANTE: Consultar sus propios proyectos
JEFE_DEPARTAMENTO: Consultar anteproyectos enviados, asignar evaluadores

Configuración
Variables de Entorno
properties# Base de Datos
SPRING_DATASOURCE_URL=jdbc:h2:file:./data/projectsdb
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# RabbitMQ
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest

# Keycloak
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=http://keycloak:8080/realms/sistema/protocol/openid-connect/certs

# User Service
USER_SERVICE_URL=http://localhost:8081
Instalación y Ejecución
Prerrequisitos

JDK 17 o superior
Maven 3.8+
RabbitMQ Server
Keycloak Server configurado con realm "sistema" y cliente "sistema-desktop"

Pasos de Instalación

Clonar el repositorio:

bashgit clone <repository-url>
cd project-microservice

Compilar el proyecto:

bashmvn clean install

Ejecutar el microservicio:

bashmvn spring-boot:run
El servicio estará disponible en http://localhost:8082
Documentación de API
Swagger UI disponible en: http://localhost:8082/swagger-ui.html
Endpoints Principales
Proyectos

POST /api/proyectos - Crear proyecto (DOCENTE)
POST /api/proyectos/{id}/evaluar - Evaluar Formato A (COORDINADOR)
POST /api/proyectos/{id}/reintentar - Reintentar proyecto (DOCENTE)
POST /api/proyectos/{id}/anteproyecto - Subir anteproyecto (DOCENTE)
GET /api/proyectos/estudiante/{email} - Consultar proyectos por estudiante (ESTUDIANTE)
GET /api/proyectos/director/{email} - Consultar proyectos por director (DOCENTE)
GET /api/proyectos/docente/{email} - Consultar proyectos por docente (DOCENTE)
GET /api/proyectos/coordinador/{emailCoordinador} - Proyectos pendientes (COORDINADOR)
GET /api/proyectos/anteproyectos/jefe/{emailJefe} - Anteproyectos enviados (JEFE_DEPARTAMENTO)

Evaluadores

POST /api/proyectos/{id}/asignar-evaluadores - Asignar evaluadores (JEFE_DEPARTAMENTO)
POST /api/proyectos/{id}/evaluar-anteproyecto - Evaluar anteproyecto (DOCENTE)
GET /api/proyectos/{id}/evaluadores - Consultar evaluadores (JEFE_DEPARTAMENTO, DOCENTE, COORDINADOR)
GET /api/proyectos/mis-evaluaciones - Mis proyectos como evaluador (DOCENTE)

Integración con Otros Microservicios
User Microservice

Validación de usuarios mediante Feign Client
Consulta de información de docentes, estudiantes y coordinadores

Notification Microservice

Publicación de eventos en RabbitMQ:

formato-a.submitted: Notifica creación de proyecto
proyecto.evaluado: Notifica resultado de evaluación
anteproyecto.submitted: Notifica envío de anteproyecto
asignacion.evaluadores: Notifica asignación de evaluadores



Testing
Estructura de Pruebas
bashmvn test
Cobertura
Las pruebas incluyen:

Pruebas unitarias de lógica de dominio
Pruebas de integración de repositorios
Pruebas de endpoints con seguridad