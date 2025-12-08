package co.edu.unicauca.project_microservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import co.edu.unicauca.project_microservice.entity.EvaluadoresAnteproyecto;
import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import co.edu.unicauca.project_microservice.infra.dto.ProyectoRequest;
import co.edu.unicauca.project_microservice.service.IProjectServiceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import co.edu.unicauca.project_microservice.entity.EvaluadoresAnteproyecto;
import co.edu.unicauca.project_microservice.service.EvaluadoresService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proyectos")
@Tag(name = "Gestión de Proyectos de Grado", description = "API para crear, evaluar y consultar proyectos de grado")
public class ProjectController {

    @Autowired
    private IProjectServiceFacade facade;
    @Autowired
    private EvaluadoresService evaluadoresService;
    // ⬅ MÉTODO HELPER PARA EXTRAER EMAIL DEL JWT
    private String extractEmailFromJwt(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String email = jwt.getClaimAsString("email");
            if (email == null || email.isEmpty()) {
                email = jwt.getClaimAsString("preferred_username");
            }
            return email;
        }
        return null;
    }

    @Operation(summary = "Crear un nuevo proyecto de grado")
    @PostMapping
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<?> crearProyecto(@RequestBody ProyectoRequest request, Authentication authentication) {
        try {
            String emailAutenticado = extractEmailFromJwt(authentication);
            System.out.println("[DEBUG] Email autenticado: " + emailAutenticado); // Debug

            ProyectoGrado proyecto = new ProyectoGrado();
            proyecto.setTitulo(request.getTitulo());
            proyecto.setModalidad(request.getModalidad());
            proyecto.setDirectorEmail(request.getDirectorEmail());
            proyecto.setCodirectorEmail(request.getCodirectorEmail());
            proyecto.setEstudiante1Email(request.getEstudiante1Email());
            proyecto.setEstudiante2Email(request.getEstudiante2Email());
            proyecto.setObjetivoGeneral(request.getObjetivoGeneral());
            proyecto.setObjetivosEspecificos(request.getObjetivosEspecificos());

            ProyectoGrado resultado = facade.crearProyecto(proyecto);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Error interno: " + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Evaluar un proyecto de grado")
    @PostMapping("/{id}/evaluar")
    @PreAuthorize("hasRole('COORDINADOR')")
    public ResponseEntity<?> evaluarProyecto(@PathVariable Long id, @RequestParam boolean aprobado,
            @RequestParam String observaciones) {
        try {
            facade.evaluarProyecto(id, aprobado, observaciones);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Subir anteproyecto")
    @PostMapping("/{id}/anteproyecto")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<?> subirAnteproyecto(
            @PathVariable Long id,
            @RequestParam String jefeDepartamentoEmail) {
        try {
            facade.subirAnteproyecto(id, jefeDepartamentoEmail);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Obtener todos los proyectos (para coordinador)")
    @GetMapping("/todos")
    @PreAuthorize("hasRole('COORDINADOR')")
    public ResponseEntity<?> obtenerTodosProyectos() {
        try {
            List<ProyectoGrado> proyectos = facade.obtenerTodosProyectos();
            return ResponseEntity.ok(proyectos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Obtener proyectos por estudiante")
    @GetMapping("/estudiante/{email}")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<?> obtenerPorEstudiante(@PathVariable String email, Authentication authentication) {
        String emailAutenticado = extractEmailFromJwt(authentication);
        System.out.println("[DEBUG] Estudiante - Email autenticado: " + emailAutenticado + ", Email solicitado: " + email);
        
        if (!emailAutenticado.equals(email)) {
             return ResponseEntity.status(403).body("{\"error\": \"No autorizado para ver proyectos de otro usuario\"}");
        }
        try {
            List<ProyectoGrado> proyectos = facade.obtenerProyectosPorEstudiante(email);
            return ResponseEntity.ok(proyectos);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Obtener proyectos por director (docente)")
    @GetMapping("/director/{email}")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<?> obtenerPorDirector(@PathVariable String email, Authentication authentication) {
        String emailAutenticado = extractEmailFromJwt(authentication);
        System.out.println("[DEBUG] Docente - Email autenticado: " + emailAutenticado + ", Email solicitado: " + email);
        
        if (emailAutenticado == null || !emailAutenticado.equals(email)) {
             return ResponseEntity.status(403).body("{\"error\": \"No autorizado para ver proyectos de otro docente\"}");
        }
        try {
            List<ProyectoGrado> proyectos = facade.obtenerProyectosPorDirector(email);
            return ResponseEntity.ok(proyectos);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Obtener proyectos pendientes para coordinador")
    @GetMapping("/coordinador/{emailCoordinador}")
    @PreAuthorize("hasRole('COORDINADOR')")
    public ResponseEntity<?> obtenerProyectosParaCoordinador(@PathVariable String emailCoordinador, Authentication authentication) {
        String emailAutenticado = extractEmailFromJwt(authentication);
        System.out.println("[DEBUG] Coordinador - Email autenticado: " + emailAutenticado + ", Email solicitado: " + emailCoordinador);
        
        if (!emailAutenticado.equals(emailCoordinador)) {
             return ResponseEntity.status(403).body("{\"error\": \"No autorizado para ver proyectos de otro coordinador\"}");
        }
        try {
            List<ProyectoGrado> proyectos = facade.obtenerProyectosPendientesCoordinador();
            return ResponseEntity.ok(proyectos);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Obtener anteproyectos por jefe de departamento")
    @GetMapping("/anteproyectos/jefe/{emailJefe}")
    @PreAuthorize("hasRole('JEFE_DEPARTAMENTO')")
    public ResponseEntity<?> obtenerAnteproyectosPorJefe(@PathVariable String emailJefe, Authentication authentication) {
        String emailAutenticado = extractEmailFromJwt(authentication);
        System.out.println("[DEBUG] Jefe - Email autenticado: " + emailAutenticado + ", Email solicitado: " + emailJefe);
        
        if (!emailAutenticado.equals(emailJefe)) {
             return ResponseEntity.status(403).body("{\"error\": \"No autorizado para ver anteproyectos de otro jefe\"}");
        }
        try {
            List<ProyectoGrado> proyectos = facade.obtenerAnteproyectosPorJefe(emailJefe);
            return ResponseEntity.ok(proyectos);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Reintentar un proyecto de grado")
    @PostMapping("/{id}/reintentar")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<?> reintentarProyecto(@PathVariable Long id) {
        try {
            facade.reintentarProyecto(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // co.edu.unicauca.project_microservice.controller.ProjectController

@Operation(summary = "Obtener proyectos donde el docente es director o codirector")
@GetMapping("/docente/{email}")
@PreAuthorize("hasRole('DOCENTE')")
public ResponseEntity<?> obtenerProyectosDocente(@PathVariable String email, Authentication authentication) {
    String emailAutenticado = extractEmailFromJwt(authentication);
    if (emailAutenticado == null || !emailAutenticado.equals(email)) {
        return ResponseEntity.status(403).body("{\"error\": \"No autorizado\"}");
    }
    try {
        List<ProyectoGrado> proyectos = facade.obtenerProyectosPorDocente(email);
        return ResponseEntity.ok(proyectos);
    } catch (Exception e) {
        return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
    }
}

     @Operation(summary = "Asignar evaluadores a un anteproyecto (RF8)")
    @PostMapping("/{id}/asignar-evaluadores")
    @PreAuthorize("hasRole('JEFE_DEPARTAMENTO')")
    public ResponseEntity<?> asignarEvaluadores(
            @PathVariable Long id,
            @RequestParam String evaluador1Email,
            @RequestParam String evaluador2Email,
            Authentication authentication) {
        
        try {
            String emailJefe = extractEmailFromJwt(authentication);
            
            EvaluadoresAnteproyecto evaluadores = evaluadoresService.asignarEvaluadores(
                id, evaluador1Email, evaluador2Email, emailJefe
            );
            
            // Notificar a los evaluadores
            facade.notificarAsignacionEvaluadores(id, evaluador1Email, evaluador2Email);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Evaluadores asignados correctamente",
                "idAsignacion", evaluadores.getId()
            ));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @Operation(summary = "Obtener evaluadores de un proyecto")
    @GetMapping("/{id}/evaluadores")
    @PreAuthorize("hasRole('JEFE_DEPARTAMENTO') or hasRole('DOCENTE') or hasRole('COORDINADOR')")
    public ResponseEntity<?> obtenerEvaluadores(@PathVariable Long id) {
        try {
            return evaluadoresService.obtenerEvaluadores(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener proyectos donde soy evaluador")
    @GetMapping("/mis-evaluaciones")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<?> obtenerMisEvaluaciones(Authentication authentication) {
        try {
            String email = extractEmailFromJwt(authentication);
            List<EvaluadoresAnteproyecto> evaluaciones = 
                evaluadoresService.obtenerProyectosComoEvaluador(email);
            
            // Enriquecer con información del proyecto
            List<Map<String, Object>> resultado = new ArrayList<>();
            for (EvaluadoresAnteproyecto eval : evaluaciones) {
                ProyectoGrado proyecto = facade.obtenerProyectoPorId(eval.getIdProyecto());
                Map<String, Object> item = new HashMap<>();
                item.put("evaluacion", eval);
                item.put("proyecto", proyecto);
                resultado.add(item);
            }
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Evaluar anteproyecto como evaluador")
    @PostMapping("/{id}/evaluar-anteproyecto")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<?> evaluarAnteproyecto(
            @PathVariable Long id,
            @RequestParam boolean aprobado,
            @RequestParam String observaciones,
            Authentication authentication) {
        
        try {
            String emailEvaluador = extractEmailFromJwt(authentication);
            
            evaluadoresService.registrarEvaluacion(id, emailEvaluador, aprobado, observaciones);
            
            // Verificar si ambos evaluadores ya evaluaron
            if (evaluadoresService.tieneEvaluacionCompleta(id)) {
                facade.procesarEvaluacionCompleta(id);
            }
            
            return ResponseEntity.ok(Map.of("mensaje", "Evaluación registrada correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

}