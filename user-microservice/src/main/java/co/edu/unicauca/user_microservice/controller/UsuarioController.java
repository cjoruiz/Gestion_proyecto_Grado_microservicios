package co.edu.unicauca.user_microservice.controller;

import co.edu.unicauca.user_microservice.entity.*;
import co.edu.unicauca.user_microservice.entity.enums.TipoDocente;
import co.edu.unicauca.user_microservice.infra.dto.*;
import co.edu.unicauca.user_microservice.service.IUsuarioService;
import co.edu.unicauca.user_microservice.utilities.exception.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Gestión de Usuarios", description = "API para registrar y consultar usuarios del sistema")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    // ========== ENDPOINTS ESPECÍFICOS (DEBEN IR PRIMERO) ==========
    
    @Operation(summary = "Validar existencia y rol del usuario autenticado")
    @GetMapping("/validar")
    public ResponseEntity<Map<String, Object>> validarUsuario(Authentication authentication) {
        try {
            if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
                return ResponseEntity.status(400).body(Map.of("error", "Token inválido"));
            }

            String email = jwt.getClaimAsString("email");
            if (email == null) {
                return ResponseEntity.status(400).body(Map.of("existe", false, "rol", "DESCONOCIDO", "error", "Email no encontrado en el token"));
            }

            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            String rol = "DESCONOCIDO";
            if (resourceAccess != null && resourceAccess.containsKey("sistema-desktop")) {
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("sistema-desktop");
                List<String> roles = (List<String>) clientAccess.get("roles");
                if (roles != null && !roles.isEmpty()) {
                    for (String role : roles) {
                        if ("ESTUDIANTE".equals(role)) { rol = "ESTUDIANTE"; break; }
                        if ("DOCENTE".equals(role)) { rol = "DOCENTE"; break; }
                        if ("COORDINADOR".equals(role)) { rol = "COORDINADOR"; break; }
                        if ("JEFE_DEPARTAMENTO".equals(role)) { rol = "JEFE_DEPARTAMENTO"; break; }
                    }
                }
            }

            Map<String, Object> respuesta = Map.of(
                "existe", true,
                "rol", rol,
                "email", email
            );
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Obtener docentes por programa (para jefes de departamento)")
    @GetMapping("/docentes-por-programa")
    @PreAuthorize("hasRole('JEFE_DEPARTAMENTO')")
    public ResponseEntity<?> obtenerDocentesPorPrograma(
            @RequestParam(required = false) String programa,
            Authentication authentication) {

        System.out.println("[DEBUG CONTROLLER] Endpoint /docentes-por-programa llamado");
        System.out.println("[DEBUG CONTROLLER] Programa recibido: " + programa);

        // 1. Validar que el usuario esté autenticado con JWT
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            return ResponseEntity.status(401).body(Map.of("error", "Token inválido o no proporcionado"));
        }

        // 2. Extraer email y roles del token
        String email = jwt.getClaimAsString("email");
        if (email == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Email no encontrado en el token"));
        }

        String rol = "DESCONOCIDO";
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey("sistema-desktop")) {
            Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("sistema-desktop");
            List<String> roles = (List<String>) clientAccess.get("roles");
            if (roles != null) {
                for (String r : roles) {
                    if ("JEFE_DEPARTAMENTO".equals(r)) {
                        rol = "JEFE_DEPARTAMENTO";
                        break;
                    }
                }
            }
        }

        // 3. Si no se envía 'programa', devolver error
        if (programa == null || programa.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El parámetro 'programa' es obligatorio"));
        }

        // 4. Validar que sea JEFE_DEPARTAMENTO
        if (!"JEFE_DEPARTAMENTO".equals(rol)) {
            return ResponseEntity.status(403).body(Map.of("error", "Acceso denegado: se requiere rol JEFE_DEPARTAMENTO"));
        }

        // 5. Delegar al servicio
        try {
            System.out.println("[DEBUG CONTROLLER] Llamando al servicio con programa: " + programa);
            List<UsuarioDetalladoDto> docentes = usuarioService.obtenerDocentesPorPrograma(programa);
            System.out.println("[DEBUG CONTROLLER] Docentes encontrados: " + docentes.size());
            return ResponseEntity.ok(docentes);
        } catch (Exception e) {
            System.err.println("[ERROR CONTROLLER] Error al obtener docentes: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error interno al obtener docentes: " + e.getMessage()));
        }
    }

    // ========== ENDPOINTS DE REGISTRO (PÚBLICOS) ==========

    @Operation(summary = "Registrar un nuevo estudiante")
    @PostMapping("/estudiantes")
    public ResponseEntity<?> registrarEstudiante(@RequestBody EstudianteRequest request) {
        try {
            Estudiante estudiante = new Estudiante();
            estudiante.setEmail(request.getEmail());
            estudiante.setPassword(request.getPassword());
            estudiante.setNombres(request.getNombres());
            estudiante.setApellidos(request.getApellidos());
            estudiante.setCelular(request.getCelular());
            estudiante.setPrograma(request.getPrograma());

            Usuario usuarioCreado = usuarioService.registrarEstudiante(estudiante);
            return ResponseEntity.status(201).body(usuarioCreado);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        } catch (InvalidUserDataException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @Operation(summary = "Registrar un nuevo docente")
    @PostMapping("/docentes")
    public ResponseEntity<?> registrarDocente(@RequestBody DocenteRequest request) {
        try {
            Docente docente = new Docente();
            docente.setEmail(request.getEmail());
            docente.setPassword(request.getPassword());
            docente.setNombres(request.getNombres());
            docente.setApellidos(request.getApellidos());
            docente.setCelular(request.getCelular());
            docente.setPrograma(request.getPrograma());
            docente.setTipoDocente(request.getTipoDocente() != null ? request.getTipoDocente() : TipoDocente.PLANTA);

            Usuario usuarioCreado = usuarioService.registrarDocente(docente);
            return ResponseEntity.status(201).body(usuarioCreado);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        } catch (InvalidUserDataException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @Operation(summary = "Registrar un nuevo coordinador")
    @PostMapping("/coordinadores")
    public ResponseEntity<?> registrarCoordinador(@RequestBody CoordinadorRequest request) {
        try {
            Coordinador coordinador = new Coordinador();
            coordinador.setEmail(request.getEmail());
            coordinador.setPassword(request.getPassword());
            coordinador.setNombres(request.getNombres());
            coordinador.setApellidos(request.getApellidos());
            coordinador.setCelular(request.getCelular());
            coordinador.setPrograma(request.getPrograma());

            Usuario usuarioCreado = usuarioService.registrarCoordinador(coordinador);
            return ResponseEntity.status(201).body(usuarioCreado);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        } catch (InvalidUserDataException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
        }
    }

    @Operation(summary = "Registrar un nuevo jefe de departamento")
    @PostMapping("/jefes-departamento")
    public ResponseEntity<?> registrarJefeDepartamento(@RequestBody JefeDepartamentoRequest request) {
        try {
            JefeDepartamento jefe = new JefeDepartamento();
            jefe.setEmail(request.getEmail());
            jefe.setPassword(request.getPassword());
            jefe.setNombres(request.getNombres());
            jefe.setApellidos(request.getApellidos());
            jefe.setCelular(request.getCelular());
            jefe.setPrograma(request.getPrograma());

            Usuario usuarioCreado = usuarioService.registrarJefeDepartamento(jefe);
            return ResponseEntity.status(201).body(usuarioCreado);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        } catch (InvalidUserDataException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
        }
    }

    // ========== ENDPOINT GENÉRICO (DEBE IR AL FINAL) ==========

    @Operation(summary = "Obtener un usuario por email")
    @GetMapping("/{email:.+}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable String email) {
        try {
            System.out.println("[DEBUG CONTROLLER] Endpoint /{email} llamado con: " + email);
            
            Usuario usuarioJPA = usuarioService.obtenerPorEmail(email);

            if (usuarioJPA != null) {
                UsuarioDetalladoDto usuarioDto = new UsuarioDetalladoDto();
                usuarioDto.setEmail(usuarioJPA.getEmail());
                usuarioDto.setNombres(usuarioJPA.getNombres());
                usuarioDto.setApellidos(usuarioJPA.getApellidos());
                usuarioDto.setCelular(usuarioJPA.getCelular());
                usuarioDto.setPrograma(usuarioJPA.getPrograma());
                
                String rol = usuarioService.obtenerRol(email);
                usuarioDto.setRol(rol);

                return ResponseEntity.ok(usuarioDto);
            } else {
                return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo usuario: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}