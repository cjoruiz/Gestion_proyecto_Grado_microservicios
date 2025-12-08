// File: project-microservice/src/main/java/co/edu/unicauca/project_microservice/service/ProjectServiceFacade.java
package co.edu.unicauca.project_microservice.service;

import co.edu.unicauca.project_microservice.entity.EvaluadoresAnteproyecto;
// REMOVIDO: import co.edu.unicauca.project_microservice.Client.UserClient; // <-- COMENTAR/ELIMINAR ESTA LINEA
import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import co.edu.unicauca.project_microservice.entity.estados.AnteproyectoEnviadoState;
import co.edu.unicauca.project_microservice.entity.estados.EnPrimeraEvaluacionState;
import co.edu.unicauca.project_microservice.infra.dto.*;
import co.edu.unicauca.project_microservice.service.evaluacion.EvaluadorAprobacion;
import co.edu.unicauca.project_microservice.service.evaluacion.EvaluadorRechazo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
// REMOVIDO: import java.util.Map; // <-- COMENTAR/ELIMINAR ESTA LINEA SI YA NO SE USA

@Service
public class ProjectServiceFacade implements IProjectServiceFacade {

    @Autowired private IProyectoService proyectoService;
    @Autowired private INotificationServiceClient notificationClient;
    @Autowired private EnPrimeraEvaluacionState enPrimeraEvaluacionState;
    @Autowired private EvaluadorAprobacion evaluadorAprobacion;
    @Autowired private EvaluadorRechazo evaluadorRechazo;
    @Autowired private EvaluadoresService evaluadoresService;
    // @Autowired private UserClient userClient; // <-- COMENTAR/ELIMINAR ESTA LINEA

    // --- COMENTAR/ELIMINAR ESTE MÉTODO ---
    /*
    private void validarUsuario(String email, String rolEsperado) {
        try {
            Map<String, Object> respuesta = userClient.validarUsuario(email); //  Usa Feign
            Boolean existe = (Boolean) respuesta.get("existe");
            String rol = (String) respuesta.get("rol");

            if (!Boolean.TRUE.equals(existe)) {
                throw new RuntimeException("El usuario con email " + email + " no existe.");
            }
            if (!rol.equals(rolEsperado)) {
                throw new RuntimeException("El usuario con email " + email + " no es un " + rolEsperado + ".");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al validar el usuario " + email + ": " + e.getMessage());
        }
    }
    */
    // --- FIN COMENTARIO ---

     @Override
    public ProyectoGrado crearProyecto(ProyectoGrado proyecto) {
        // OBTENER INFORMACIÓN DEL USUARIO AUTENTICADO DESDE EL TOKEN JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
             throw new RuntimeException("Usuario no autenticado o token inválido.");
        }

        String emailAutenticado = jwtAuth.getToken().getClaim("email");
        if (emailAutenticado == null) {
            throw new RuntimeException("El token JWT no contiene el claim 'email'.");
        }
        // --- FIN CORRECCIÓN ---

        // Validar que el usuario autenticado sea el director (o codirector si aplica)
        // Esta validación asume que el docente logueado es el director del proyecto que está creando.
        if (!emailAutenticado.equals(proyecto.getDirectorEmail())) {
            // Opcional: Permitir codirector también
            // if (!emailAutenticado.equals(proyecto.getCodirectorEmail())) {
                throw new RuntimeException("El usuario autenticado no es el director del proyecto.");
            // }
        }

        // Opcional: Aquí podrías validar los emails de codirector, estudiantes, etc.
        // Si los microservicios confían plenamente en el Gateway, podrías omitir
        // validaciones de existencia aquí, o hacerlas internamente si es crítico
        // y no se pueden delegar al Gateway. Por ahora, las omitiremos por simplicidad
        // y confianza en el frontend/Gate.
        // Por ejemplo, si confías en que el frontend validó los emails antes de enviarlos,
        // y el Gateway validó el token, no necesitas volver a llamar a otro servicio para validar existencia.

        proyecto.setEstado(enPrimeraEvaluacionState);
        ProyectoGrado guardado = proyectoService.crear(proyecto);

        // Notificar Formato A subido
        FormatoASubidoEvent event = new FormatoASubidoEvent();
        event.setIdProyecto(guardado.getId());
        event.setTitulo(guardado.getTitulo());
        event.setCoordinadorEmail("coordinador.sistemas@unicauca.edu.co"); // Hardcodeado por simplicidad
        notificationClient.notificarFormatoASubido(event);

        return guardado;
    }

   @Override
    public void evaluarProyecto(Long id, boolean aprobado, String observaciones) {
        // Aquí también se confía en que el Controller haya autorizado la llamada
        // (por ejemplo, con @PreAuthorize("hasRole('COORDINADOR')"))
        if (aprobado) {
            evaluadorAprobacion.evaluarProyecto(id, true, observaciones);
        } else {
            evaluadorRechazo.evaluarProyecto(id, false, observaciones);
        }
    }

    @Override
    public void reintentarProyecto(Long id) {
        ProyectoGrado p = proyectoService.obtenerPorId(id);
        p.reintentar();
        proyectoService.guardar(p);
    }

     @Override
    public void subirAnteproyecto(Long idProyecto, String jefeDepartamentoEmail) {
        // Confía en que el Controller haya autorizado (docente logueado)
        ProyectoGrado p = proyectoService.obtenerPorId(idProyecto);
        if (!"FORMATO_A_APROBADO".equals(p.getEstadoActual())) {
            throw new RuntimeException("Solo se puede subir anteproyecto si el Formato A está aprobado.");
        }

        // Aquí, el docente logueado es el director del proyecto (validado por Controller)
        p.setEstado(new AnteproyectoEnviadoState());
        proyectoService.guardar(p);

        // Publicar evento de notificación
        AnteproyectoSubidoEvent event = new AnteproyectoSubidoEvent();
        event.setIdProyecto(p.getId());
        event.setTitulo(p.getTitulo());
        event.setJefeDepartamentoEmail(jefeDepartamentoEmail);
        event.setEstudianteEmail(p.getEstudiante1Email());
        event.setTutor1Email(p.getDirectorEmail());
        if (p.getCodirectorEmail() != null && !p.getCodirectorEmail().isEmpty()) {
            event.setTutor2Email(p.getCodirectorEmail());
        }
        notificationClient.notificarAnteproyectoSubido(event);
    }

    @Override
    public java.util.List<ProyectoGrado> obtenerTodosProyectos() {
        return proyectoService.obtenerTodos();
    }

    @Override
    public java.util.List<ProyectoGrado> obtenerProyectosPorEstudiante(String email) {
        return proyectoService.obtenerProyectosPorEstudiante(email);
    }

    @Override
    public java.util.List<ProyectoGrado> obtenerAnteproyectosPorJefe(String emailJefe) {
        // Filtrar solo proyectos en estado ANTEPROYECTO_ENVIADO
        return proyectoService.obtenerTodos().stream()
            .filter(p -> "ANTEPROYECTO_ENVIADO".equals(p.getEstadoActual()))
            .toList();
    }

    @Override
    public java.util.List<ProyectoGrado> obtenerProyectosPorDirector(String email) {
        return proyectoService.findByDirectorEmail(email);
    }

    @Override
    public java.util.List<ProyectoGrado> obtenerProyectosPendientesCoordinador() {
        // Requisito 3: solo proyectos en evaluación
        return proyectoService.obtenerTodos().stream()
            .filter(p -> p.getEstadoActual().startsWith("EN_") && p.getEstadoActual().endsWith("_EVALUACION_FORMATO_A"))
            .toList();
    }

    @Override
public void notificarAsignacionEvaluadores(Long idProyecto, String evaluador1Email, String evaluador2Email) {
    ProyectoGrado p = proyectoService.obtenerPorId(idProyecto);
    
    AsignacionEvaluadoresEvent event = new AsignacionEvaluadoresEvent();
    event.setIdProyecto(p.getId());
    event.setTituloProyecto(p.getTitulo());
    event.setEvaluador1Email(evaluador1Email);
    event.setEvaluador2Email(evaluador2Email);
    event.setEstudianteEmail(p.getEstudiante1Email());
    
    notificationClient.notificarAsignacionEvaluadores(event);
}

    @Override
    public ProyectoGrado obtenerProyectoPorId(Long idProyecto) {
        return proyectoService.obtenerPorId(idProyecto);
    }

    @Override
    public void procesarEvaluacionCompleta(Long idProyecto) {
        // Obtener evaluaciones
        EvaluadoresAnteproyecto evaluadores = evaluadoresService.obtenerEvaluadores(idProyecto)
            .orElseThrow(() -> new RuntimeException("No se encontraron evaluadores"));
        
        ProyectoGrado proyecto = proyectoService.obtenerPorId(idProyecto);
        
        // Si ambos aprobaron, pasar a siguiente fase
        if (evaluadores.evaluacionAprobada()) {
            // Aquí podrías cambiar el estado del proyecto
            // Por ejemplo, crear un nuevo estado ANTEPROYECTO_APROBADO
            proyecto.setEstadoActual("ANTEPROYECTO_APROBADO");
            proyectoService.guardar(proyecto);
            
            // Notificar al estudiante y director
            notificarResultadoEvaluacion(proyecto, true, 
                "Anteproyecto aprobado por ambos evaluadores");
        } else {
            // Si alguno rechazó, notificar para correcciones
            proyecto.setEstadoActual("ANTEPROYECTO_RECHAZADO");
            proyectoService.guardar(proyecto);
            
            String observaciones = String.format(
                "Evaluador 1: %s\nEvaluador 2: %s",
                evaluadores.getObservacionesEvaluador1(),
                evaluadores.getObservacionesEvaluador2()
            );
            
            notificarResultadoEvaluacion(proyecto, false, observaciones);
        }
    }

    private void notificarResultadoEvaluacion(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        ProyectoEvaluadoEvent event = new ProyectoEvaluadoEvent();
        event.setIdProyecto(proyecto.getId());
        event.setAprobado(aprobado);
        event.setObservaciones(observaciones);
        event.setDestinatarios(new String[]{
            proyecto.getDirectorEmail(),
            proyecto.getEstudiante1Email()
        });
        notificationClient.notificarEvaluacion(event);
    }
    
    @Override
    public List<ProyectoGrado> obtenerProyectosPorDocente(String email) {
        return proyectoService.findByDirectorEmailOrCodirectorEmail(email);
    }
}