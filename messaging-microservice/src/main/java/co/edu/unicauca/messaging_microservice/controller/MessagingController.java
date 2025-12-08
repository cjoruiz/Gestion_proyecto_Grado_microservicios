package co.edu.unicauca.messaging_microservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unicauca.messaging_microservice.entity.MensajeInterno;
import co.edu.unicauca.messaging_microservice.infra.dto.MensajeInternoRequest;
import co.edu.unicauca.messaging_microservice.infra.dto.MensajeResponse;
import co.edu.unicauca.messaging_microservice.service.IMensajeInternoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@Tag(name = "Mensajería Interna", description = "Gestión de mensajes entre estudiantes y docentes")
public class MessagingController {

    @Autowired
    private IMensajeInternoService mensajeService;

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
    
    @PostMapping("/enviar")
    @PreAuthorize("hasRole('ESTUDIANTE') or hasRole('DOCENTE')")
    public ResponseEntity<MensajeResponse> enviarMensaje(@RequestParam String remitenteEmail,
                                                         @RequestParam String destinatariosEmail,
                                                         @RequestParam String asunto,
                                                         @RequestParam String cuerpo,
                                                         @RequestParam(required = false) MultipartFile documentoAdjunto,
                                                         Authentication authentication) { // ⬅️ Agregar parámetro
        
        String emailUsuarioAutenticado = extractEmailFromJwt(authentication);
        
        if (emailUsuarioAutenticado == null || !emailUsuarioAutenticado.equals(remitenteEmail)) {
            return ResponseEntity.badRequest().body(new MensajeResponse("No autorizado: El remitente no coincide con el usuario autenticado.", "ERROR"));
        }

        try {
            MensajeInternoRequest request = new MensajeInternoRequest();
            request.setRemitenteEmail(remitenteEmail);
            request.setDestinatariosEmail(destinatariosEmail);
            request.setAsunto(asunto);
            request.setCuerpo(cuerpo);
            request.setDocumentoAdjunto(documentoAdjunto);

            Long idMensaje = mensajeService.enviarMensaje(request);
            return ResponseEntity.ok(new MensajeResponse("Mensaje enviado con ID: " + idMensaje, "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensajeResponse("Error: " + e.getMessage(), "ERROR"));
        }
    }

    @Operation(
        summary = "Obtener mensajes enviados por un estudiante",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "email", description = "Email del estudiante", example = "estudiante@unicauca.edu.co")
        }
    )
    @GetMapping("/enviados/{email}")
    @PreAuthorize("hasRole('ESTUDIANTE') or hasRole('DOCENTE')")
    public ResponseEntity<List<MensajeInterno>> getMensajesEnviados(@PathVariable String email, Authentication authentication) {
        String emailUsuarioAutenticado = extractEmailFromJwt(authentication);
        
        System.out.println("[DEBUG] Mensajes enviados - Email autenticado: " + emailUsuarioAutenticado + ", Email solicitado: " + email);
        
        if (emailUsuarioAutenticado == null || !emailUsuarioAutenticado.equals(email)) {
            return ResponseEntity.status(403).build();
        }
        
        List<MensajeInterno> mensajes = mensajeService.obtenerMensajesEnviadosPorEstudiante(email);
        return ResponseEntity.ok(mensajes);
    }

    @Operation(
        summary = "Obtener mensajes recibidos por un docente",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "email", description = "Email del docente", example = "docente@unicauca.edu.co")
        }
    )
    @GetMapping("/recibidos/{email}")
    @PreAuthorize("hasRole('ESTUDIANTE') or hasRole('DOCENTE')")
    public ResponseEntity<List<MensajeInterno>> getMensajesRecibidos(@PathVariable String email, Authentication authentication) {
        String emailUsuarioAutenticado = extractEmailFromJwt(authentication);
        
        System.out.println("[DEBUG] Mensajes recibidos - Email autenticado: " + emailUsuarioAutenticado + ", Email solicitado: " + email);
        
        if (emailUsuarioAutenticado == null || !emailUsuarioAutenticado.equals(email)) {
            return ResponseEntity.status(403).build();
        }
        
        List<MensajeInterno> mensajes = mensajeService.obtenerMensajesRecibidosPorDocente(email);
        return ResponseEntity.ok(mensajes);
    }
    
    @Operation(summary = "Descargar archivo adjunto de un mensaje")
    @GetMapping("/archivo/{idMensaje}")
    @PreAuthorize("hasRole('ESTUDIANTE') or hasRole('DOCENTE') or hasRole('COORDINADOR') or hasRole('JEFE_DEPARTAMENTO')")
    public ResponseEntity<ByteArrayResource> descargarArchivoAdjunto(@PathVariable Long idMensaje) {
        try {
            MensajeInterno mensaje = mensajeService.obtenerPorId(idMensaje);
            if (mensaje == null || mensaje.getDocumentoAdjunto() == null) {
                return ResponseEntity.notFound().build();
            }
            String nombre = mensaje.getNombreArchivo() != null ? mensaje.getNombreArchivo() : "adjunto.pdf";
            ByteArrayResource resource = new ByteArrayResource(mensaje.getDocumentoAdjunto());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM) 
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}