package co.edu.unicauca.messaging_microservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unicauca.messaging_microservice.entity.MensajeInterno;
import co.edu.unicauca.messaging_microservice.infra.dto.MensajeInternoRequest;
import co.edu.unicauca.messaging_microservice.infra.dto.MensajeResponse;
import co.edu.unicauca.messaging_microservice.service.IMensajeInternoService;
import co.edu.unicauca.messaging_microservice.utilities.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@Tag(name = "Mensajería Interna", description = "Gestión de mensajes entre estudiantes y docentes")
public class MessagingController {

    @Autowired
    private IMensajeInternoService mensajeService;

    @PostMapping("/enviar")
    public ResponseEntity<MensajeResponse> enviarMensaje(
            @RequestParam String remitenteEmail,
            @RequestParam String destinatariosEmail,
            @RequestParam String asunto,
            @RequestParam String cuerpo,
            @RequestParam(required = false) MultipartFile documentoAdjunto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Extraer email del token
        String emailUsuarioAutenticado = JwtUtil.extractEmailFromToken(authHeader);
        
        if (emailUsuarioAutenticado == null) {
            return ResponseEntity.status(401)
                .body(new MensajeResponse("No autorizado: Token inválido", "ERROR"));
        }
        
        // Validar que el remitente coincida con el usuario autenticado
        if (!emailUsuarioAutenticado.equals(remitenteEmail)) {
            return ResponseEntity.badRequest()
                .body(new MensajeResponse("No autorizado: El remitente no coincide con el usuario autenticado.", "ERROR"));
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

    @Operation(summary = "Obtener mensajes enviados por un estudiante")
    @GetMapping("/enviados/{email}")
    public ResponseEntity<List<MensajeInterno>> getMensajesEnviados(
            @PathVariable String email,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String emailUsuarioAutenticado = JwtUtil.extractEmailFromToken(authHeader);
        
        System.out.println("[DEBUG] Mensajes enviados - Email autenticado: " + emailUsuarioAutenticado + ", Email solicitado: " + email);
        
        if (emailUsuarioAutenticado == null || !emailUsuarioAutenticado.equals(email)) {
            return ResponseEntity.status(403).build();
        }
        
        List<MensajeInterno> mensajes = mensajeService.obtenerMensajesEnviadosPorEstudiante(email);
        return ResponseEntity.ok(mensajes);
    }

    @Operation(summary = "Obtener mensajes recibidos por un docente")
    @GetMapping("/recibidos/{email}")
    public ResponseEntity<List<MensajeInterno>> getMensajesRecibidos(
            @PathVariable String email,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String emailUsuarioAutenticado = JwtUtil.extractEmailFromToken(authHeader);
        
        System.out.println("[DEBUG] Mensajes recibidos - Email autenticado: " + emailUsuarioAutenticado + ", Email solicitado: " + email);
        
        if (emailUsuarioAutenticado == null || !emailUsuarioAutenticado.equals(email)) {
            return ResponseEntity.status(403).build();
        }
        
        List<MensajeInterno> mensajes = mensajeService.obtenerMensajesRecibidosPorDocente(email);
        return ResponseEntity.ok(mensajes);
    }
    
    @Operation(summary = "Descargar archivo adjunto de un mensaje")
    @GetMapping("/archivo/{idMensaje}")
    public ResponseEntity<ByteArrayResource> descargarArchivoAdjunto(
            @PathVariable Long idMensaje,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String emailUsuarioAutenticado = JwtUtil.extractEmailFromToken(authHeader);
        
        if (emailUsuarioAutenticado == null) {
            return ResponseEntity.status(401).build();
        }

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