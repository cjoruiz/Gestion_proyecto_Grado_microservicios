// application/ProyectoUseCase.java
package co.edu.unicauca.project_microservice.application;

import co.edu.unicauca.project_microservice.domain.model.EvaluadoresAnteproyecto;
import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;
import co.edu.unicauca.project_microservice.domain.model.estados.EnPrimeraEvaluacionState;
import co.edu.unicauca.project_microservice.domain.port.in.ProyectoUseCasePort;
import co.edu.unicauca.project_microservice.domain.port.out.EvaluadoresRepositoryPort;
import co.edu.unicauca.project_microservice.domain.port.out.NotificationPort;
import co.edu.unicauca.project_microservice.domain.port.out.ProyectoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación de los casos de uso - Capa de aplicación
 * Orquesta el dominio y los puertos de salida
 */
@Service
public class ProyectoUseCase implements ProyectoUseCasePort {

    private final ProyectoRepositoryPort proyectoRepository;
    private final EvaluadoresRepositoryPort evaluadoresRepository;
    private final NotificationPort notificationPort;

    public ProyectoUseCase(
            ProyectoRepositoryPort proyectoRepository,
            EvaluadoresRepositoryPort evaluadoresRepository,
            NotificationPort notificationPort) {
        this.proyectoRepository = proyectoRepository;
        this.evaluadoresRepository = evaluadoresRepository;
        this.notificationPort = notificationPort;
    }

    // === CASO DE USO: Crear proyecto ===
    @Override
    public ProyectoGrado crearProyecto(ProyectoGrado proyecto, String emailAutenticado) {
        // Validación de regla de negocio
        if (!proyecto.esDirector(emailAutenticado)) {
            throw new IllegalArgumentException("El usuario autenticado no es el director del proyecto");
        }
        
        // Establecer estado inicial
        proyecto.setEstado(new EnPrimeraEvaluacionState());
        
        // Persistir
        ProyectoGrado guardado = proyectoRepository.crear(proyecto);
        
        // Notificar
        notificationPort.notificarFormatoASubido(guardado);
        
        return guardado;
    }

    // === CASO DE USO: Evaluar Formato A ===
    @Override
    public void evaluarProyecto(Long id, boolean aprobado, String observaciones) {
        ProyectoGrado p = proyectoRepository.obtenerPorId(id);
        
        // Aplicar reglas de negocio del dominio
        p.evaluar(aprobado, observaciones);
        
        // Persistir cambios
        proyectoRepository.guardar(p);
        
        // Notificar resultado
        notificationPort.notificarEvaluacion(p, aprobado, observaciones);
    }

    @Override
    public void reintentarProyecto(Long id) {
        ProyectoGrado p = proyectoRepository.obtenerPorId(id);
        
        // Aplicar regla de negocio del dominio
        p.reintentar();
        
        // Persistir
        proyectoRepository.guardar(p);
    }

    @Override
    public void subirAnteproyecto(Long id, String jefeEmail) {
        ProyectoGrado p = proyectoRepository.obtenerPorId(id);
        
        // Validar estado según regla de negocio
        if (!"FORMATO_A_APROBADO".equals(p.getEstadoActual())) {
            throw new IllegalStateException("Solo se puede subir anteproyecto si Formato A está aprobado");
        }
        
        // Cambiar estado
        p.setEstado(new co.edu.unicauca.project_microservice.domain.model.estados.AnteproyectoEnviadoState());
        proyectoRepository.guardar(p);
        
        // Notificar
        notificationPort.notificarAnteproyectoSubido(p, jefeEmail);
    }

    // === CONSULTAS ===
    @Override
    public List<ProyectoGrado> obtenerTodosProyectos() {
        return proyectoRepository.obtenerTodos();
    }

    @Override
    public List<ProyectoGrado> obtenerProyectosPorEstudiante(String email) {
        return proyectoRepository.obtenerProyectosPorEstudiante(email);
    }

    @Override
    public List<ProyectoGrado> obtenerProyectosPorDirector(String email) {
        return proyectoRepository.findByDirectorEmail(email);
    }

    @Override
    public List<ProyectoGrado> obtenerProyectosPorDocente(String email) {
        return proyectoRepository.findByDirectorEmailOrCodirectorEmail(email);
    }

    @Override
    public List<ProyectoGrado> obtenerAnteproyectosPorJefe(String emailJefe) {
        return proyectoRepository.obtenerTodos().stream()
                .filter(p -> "ANTEPROYECTO_ENVIADO".equals(p.getEstadoActual()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProyectoGrado> obtenerProyectosPendientesCoordinador() {
        return proyectoRepository.obtenerTodos().stream()
                .filter(p -> p.getEstadoActual().matches("EN_.*_EVALUACION_FORMATO_A"))
                .collect(Collectors.toList());
    }

    @Override
    public ProyectoGrado obtenerProyectoPorId(Long id) {
        return proyectoRepository.obtenerPorId(id);
    }

    // === CASO DE USO: Asignar evaluadores ===
    @Override
    public void asignarEvaluadores(Long id, String e1, String e2, String jefe) {
        // Asignar evaluadores usando el puerto
        EvaluadoresAnteproyecto ev = evaluadoresRepository.asignarEvaluadores(id, e1, e2, jefe);
        
        // Obtener proyecto para notificar
        ProyectoGrado p = proyectoRepository.obtenerPorId(id);
        
        // Notificar a los evaluadores
        notificationPort.notificarAsignacionEvaluadores(p, e1, e2);
    }

    // === CASO DE USO: Registrar evaluación ===
    @Override
    public void registrarEvaluacionAnteproyecto(Long id, String emailEvaluador, boolean aprobado, String obs) {
        evaluadoresRepository.registrarEvaluacion(id, emailEvaluador, aprobado, obs);
        
        // Si ambos evaluadores completaron, procesar resultado
        if (evaluadoresRepository.tieneEvaluacionCompleta(id)) {
            procesarEvaluacionCompleta(id);
        }
    }

    // === CASO DE USO: Procesar evaluación completa ===
    @Override
    public void procesarEvaluacionCompleta(Long id) {
        EvaluadoresAnteproyecto ev = evaluadoresRepository.obtenerEvaluadores(id)
                .orElseThrow(() -> new RuntimeException("Evaluadores no encontrados"));
        
        ProyectoGrado p = proyectoRepository.obtenerPorId(id);

        //  Aplicar regla de negocio usando estados
        if (ev.evaluacionAprobada()) {
            p.setEstado(new co.edu.unicauca.project_microservice.domain.model.estados.FormatoAAprobadoState());
        } else {
            p.setEstado(new co.edu.unicauca.project_microservice.domain.model.estados.FormatoARechazadoState());
        }
        
        proyectoRepository.guardar(p);

        // Preparar observaciones
        String obs = ev.evaluacionAprobada()
                ? "Anteproyecto aprobado por ambos evaluadores"
                : String.format("Evaluador 1: %s\nEvaluador 2: %s", 
                        ev.getObservacionesEvaluador1(), ev.getObservacionesEvaluador2());
        
        // Notificar resultado
        notificationPort.notificarResultadoEvaluacion(p, ev.evaluacionAprobada(), obs);
    }

    // === CONSULTAS AVANZADAS ===
    @Override
    public Optional<EvaluadoresAnteproyecto> obtenerEvaluadores(Long id) {
        return evaluadoresRepository.obtenerEvaluadores(id);
    }

    @Override
    public List<Map<String, Object>> obtenerMisEvaluaciones(String email) {
        List<EvaluadoresAnteproyecto> evaluaciones = 
                evaluadoresRepository.obtenerProyectosComoEvaluador(email);
        
        return evaluaciones.stream().map(ev -> {
            Map<String, Object> item = new HashMap<>();
            item.put("evaluacion", ev);
            item.put("proyecto", proyectoRepository.obtenerPorId(ev.getIdProyecto()));
            return item;
        }).collect(Collectors.toList());
    }
}