package co.edu.unicauca.document_microservice.controller;

import co.edu.unicauca.document_microservice.entity.Documento;
import co.edu.unicauca.document_microservice.infra.dto.DocumentoRequest;
import co.edu.unicauca.document_microservice.service.IDocumentoService;
import co.edu.unicauca.document_microservice.utilities.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@Tag(name = "Gestión de Documentos", description = "API para subir y descargar documentos de proyectos de grado")
public class DocumentoController {

    @Autowired
    private IDocumentoService documentoService;

    @Operation(summary = "Subir un documento")
    @PostMapping("/subir")
    public ResponseEntity<Documento> subirDocumento(
            @RequestParam("idProyecto") Long idProyecto,
            @RequestParam("tipoDocumento") String tipoDocumento,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        System.out.println("[DEBUG DocumentoController] subirDocumento llamado.");
        System.out.println("[DEBUG DocumentoController] idProyecto recibido: " + idProyecto);
        System.out.println("[DEBUG DocumentoController] tipoDocumento recibido: " + tipoDocumento);
        System.out.println("[DEBUG DocumentoController] archivo recibido: " + (archivo != null ? archivo.getOriginalFilename() : "NULL"));

        try {
            // Extraer email del token JWT (sin validarlo, confiamos en el Gateway)
            String emailAutenticado = JwtUtil.extractEmailFromToken(authHeader);
            
            if (emailAutenticado == null) {
                System.err.println("[ERROR DocumentoController] No se pudo extraer email del token.");
                return ResponseEntity.status(401).build();
            }

            System.out.println("[DEBUG DocumentoController] Email autenticado: " + emailAutenticado);

            // Validaciones básicas
            if (idProyecto == null) {
                System.err.println("[ERROR DocumentoController] idProyecto es null.");
                return ResponseEntity.badRequest().build();
            }
            if (tipoDocumento == null || tipoDocumento.trim().isEmpty()) {
                System.err.println("[ERROR DocumentoController] tipoDocumento es null o vacío.");
                return ResponseEntity.badRequest().build();
            }
            if (archivo == null || archivo.isEmpty()) {
                System.err.println("[ERROR DocumentoController] archivo es null o vacío.");
                return ResponseEntity.badRequest().build();
            }

            // Crear el DTO para el servicio
            DocumentoRequest request = new DocumentoRequest();
            request.setIdProyecto(idProyecto);
            request.setTipoDocumento(tipoDocumento);
            request.setArchivo(archivo);

            Documento documento = documentoService.subirDocumento(request);
            return ResponseEntity.ok(documento);
            
        } catch (Exception e) {
            System.err.println("[ERROR DocumentoController] Excepción al subir documento: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "Descargar un documento")
    @GetMapping("/descargar/{id}")
    public ResponseEntity<Resource> descargarDocumento(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String emailAutenticado = JwtUtil.extractEmailFromToken(authHeader);
            
            if (emailAutenticado == null) {
                return ResponseEntity.status(401).build();
            }

            byte[] contenido = documentoService.descargarDocumento(id);
            ByteArrayResource resource = new ByteArrayResource(contenido);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=documento.pdf")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener documentos de un proyecto")
    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<List<Documento>> obtenerPorProyecto(
            @PathVariable Long idProyecto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String emailAutenticado = JwtUtil.extractEmailFromToken(authHeader);
        
        if (emailAutenticado == null) {
            return ResponseEntity.status(401).build();
        }

        List<Documento> documentos = documentoService.obtenerDocumentosPorProyecto(idProyecto);
        return ResponseEntity.ok(documentos);
    }

    @Operation(summary = "Descargar plantilla del Formato A")
    @GetMapping("/plantilla/formato-a")
    public ResponseEntity<Resource> descargarPlantillaFormatoA() {
        try {
            ClassPathResource resource = new ClassPathResource("static/formatoA.doc");
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"formatoA.doc\"")
                        .header(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-word")
                        .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                        .header(HttpHeaders.PRAGMA, "no-cache")
                        .header(HttpHeaders.EXPIRES, "0")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}