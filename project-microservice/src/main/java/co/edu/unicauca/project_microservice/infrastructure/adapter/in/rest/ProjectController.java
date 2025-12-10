// infrastructure/adapter/in/rest/ProjectController.java

package co.edu.unicauca.project_microservice.infrastructure.adapter.in.rest;

import co.edu.unicauca.project_microservice.application.dto.CrearProyectoRequest;
import co.edu.unicauca.project_microservice.application.mapper.ProyectoDTOMapper;
import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;
import co.edu.unicauca.project_microservice.domain.port.in.ProyectoUseCasePort;
import co.edu.unicauca.project_microservice.utilities.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proyectos")
@Tag(name = "Gestión de Proyectos de Grado", description = "API para crear, evaluar y consultar proyectos de grado")
public class ProjectController {

    private final ProyectoUseCasePort proyectoUseCase;
    private final ProyectoDTOMapper mapper;

    public ProjectController(ProyectoUseCasePort proyectoUseCase, ProyectoDTOMapper mapper) {
        this.proyectoUseCase = proyectoUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "Crear un nuevo proyecto de grado")
    @PostMapping
    public ResponseEntity<?> crearProyecto(@RequestBody CrearProyectoRequest request,
                                          @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String emailAutenticado = JwtUtil.extractEmailFromToken(authHeader);
            if (emailAutenticado == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token inválido o no proporcionado"));
            }

            ProyectoGrado proyecto = mapper.toDomain(request);
            ProyectoGrado resultado = proyectoUseCase.crearProyecto(proyecto, emailAutenticado);
            return ResponseEntity.ok(mapper.toDTO(resultado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @Operation(summary = "Evaluar un proyecto de grado (Formato A)")
    @PostMapping("/{id}/evaluar")
    public ResponseEntity<?> evaluarProyecto(@PathVariable Long id,
                                            @RequestParam boolean aprobado,
                                            @RequestParam String observaciones,
                                            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String emailAutenticado = JwtUtil.extractEmailFromToken(authHeader);
        if (emailAutenticado == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        //  Confiamos en que el Gateway ya validó que es COORDINADOR
        try {
            proyectoUseCase.evaluarProyecto(id, aprobado, observaciones);
            return ResponseEntity.ok(Map.of("mensaje", "Evaluación registrada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Reintentar un proyecto de grado")
    @PostMapping("/{id}/reintentar")
    public ResponseEntity<?> reintentarProyecto(@PathVariable Long id,
                                               @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String emailAutenticado = JwtUtil.extractEmailFromToken(authHeader);
        if (emailAutenticado == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        //  Confiamos en que el Gateway ya validó que es DOCENTE
        try {
            proyectoUseCase.reintentarProyecto(id);
            return ResponseEntity.ok(Map.of("mensaje", "Reintento registrado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Subir anteproyecto")
    @PostMapping("/{id}/anteproyecto")
    public ResponseEntity<?> subirAnteproyecto(@PathVariable Long id,
                                               @RequestParam String jefeDepartamentoEmail,
                                               @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String emailAutenticado = JwtUtil.extractEmailFromToken(authHeader);
        if (emailAutenticado == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        //  Confiamos en que el Gateway ya validó que es DOCENTE
        try {
            proyectoUseCase.subirAnteproyecto(id, jefeDepartamentoEmail);
            return ResponseEntity.ok(Map.of("mensaje", "Anteproyecto subido correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener todos los proyectos (para coordinador)")
    @GetMapping("/todos")
    public ResponseEntity<?> obtenerTodosProyectos(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String emailAutenticado = JwtUtil.extractEmailFromToken(authHeader);
        if (emailAutenticado == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        //  Confiamos en que el Gateway ya validó que es COORDINADOR
        try {
            List<ProyectoGrado> proyectos = proyectoUseCase.obtenerTodosProyectos();
            return ResponseEntity.ok(proyectos.stream().map(mapper::toDTO).toList());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener proyectos por estudiante")
    @GetMapping("/estudiante/{email}")
    public ResponseEntity<?> obtenerPorEstudiante(@PathVariable String email,
                                                  @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String emailAutenticado = JwtUtil.extractEmailFromToken(authHeader);
        if (emailAutenticado == null || !emailAutenticado.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "No autorizado"));
        }
        try {
            List<ProyectoGrado> proyectos = proyectoUseCase.obtenerProyectosPorEstudiante(email);
            return ResponseEntity.ok(proyectos.stream().map(mapper::toDTO).toList());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener proyectos por director")
    @GetMapping("/director/{email}")
    public ResponseEntity<?> obtenerPorDirector(@PathVariable String email,
                                                @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String emailAutenticado = JwtUtil.extractEmailFromToken(authHeader);
        if (emailAutenticado == null || !emailAutenticado.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "No autorizado"));
        }
        try {
            List<ProyectoGrado> proyectos = proyectoUseCase.obtenerProyectosPorDirector(email);
            return ResponseEntity.ok(proyectos.stream().map(mapper::toDTO).toList());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener proyectos donde el docente es director o codirector")
    @GetMapping("/docente/{email}")
    public ResponseEntity<?> obtenerProyectosDocente(@PathVariable String email,
                                                     @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String emailAutenticado = JwtUtil.extractEmailFromToken(authHeader);
        if (emailAutenticado == null || !emailAutenticado.equals(email)) {
            return ResponseEntity.status(403).body(Map.of("error", "No autorizado"));
        }
        try {
            List<ProyectoGrado> proyectos = proyectoUseCase.obtenerProyectosPorDocente(email);
            return ResponseEntity.ok(proyectos.stream().map(mapper::toDTO).toList());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener proyectos pendientes para coordinador")
    @GetMapping("/coordinador/{emailCoordinador}")
    public ResponseEntity<?> obtenerProyectosParaCoordinador(@PathVariable String emailCoordinador,
                                                             @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String emailAutenticado = JwtUtil.extractEmailFromToken(authHeader);
        if (!emailAutenticado.equals(emailCoordinador)) {
            return ResponseEntity.status(403).body(Map.of("error", "No autorizado"));
        }
        try {
            List<ProyectoGrado> proyectos = proyectoUseCase.obtenerProyectosPendientesCoordinador();
            return ResponseEntity.ok(proyectos.stream().map(mapper::toDTO).toList());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener anteproyectos por jefe de departamento")
    @GetMapping("/anteproyectos/jefe/{emailJefe}")
    public ResponseEntity<?> obtenerAnteproyectosPorJefe(@PathVariable String emailJefe,
                                                         @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String emailAutenticado = JwtUtil.extractEmailFromToken(authHeader);
        if (!emailAutenticado.equals(emailJefe)) {
            return ResponseEntity.status(403).body(Map.of("error", "No autorizado"));
        }
        try {
            List<ProyectoGrado> proyectos = proyectoUseCase.obtenerAnteproyectosPorJefe(emailJefe);
            return ResponseEntity.ok(proyectos.stream().map(mapper::toDTO).toList());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Asignar evaluadores a un anteproyecto")
    @PostMapping("/{id}/asignar-evaluadores")
    public ResponseEntity<?> asignarEvaluadores(@PathVariable Long id,
                                               @RequestParam String evaluador1Email,
                                               @RequestParam String evaluador2Email,
                                               @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String emailJefe = JwtUtil.extractEmailFromToken(authHeader);
        if (emailJefe == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        try {
            proyectoUseCase.asignarEvaluadores(id, evaluador1Email, evaluador2Email, emailJefe);
            return ResponseEntity.ok(Map.of("mensaje", "Evaluadores asignados y notificados correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Evaluar anteproyecto como evaluador")
    @PostMapping("/{id}/evaluar-anteproyecto")
    public ResponseEntity<?> evaluarAnteproyecto(@PathVariable Long id,
                                                 @RequestParam boolean aprobado,
                                                 @RequestParam String observaciones,
                                                 @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String emailEvaluador = JwtUtil.extractEmailFromToken(authHeader);
        if (emailEvaluador == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        try {
            proyectoUseCase.registrarEvaluacionAnteproyecto(id, emailEvaluador, aprobado, observaciones);
            return ResponseEntity.ok(Map.of("mensaje", "Evaluación registrada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener proyectos donde soy evaluador")
    @GetMapping("/mis-evaluaciones")
    public ResponseEntity<?> obtenerMisEvaluaciones(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = JwtUtil.extractEmailFromToken(authHeader);
        if (email == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        try {
            List<Map<String, Object>> resultado = proyectoUseCase.obtenerMisEvaluaciones(email);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener evaluadores de un proyecto")
    @GetMapping("/{id}/evaluadores")
    public ResponseEntity<?> obtenerEvaluadores(@PathVariable Long id,
                                                @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = JwtUtil.extractEmailFromToken(authHeader);
        if (email == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }
        //  Confiamos en que el Gateway ya validó el rol
        try {
            return proyectoUseCase.obtenerEvaluadores(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}