package co.edu.unicauca.project_microservice.application.mapper;

import co.edu.unicauca.project_microservice.application.dto.CrearProyectoRequest;
import co.edu.unicauca.project_microservice.application.dto.ProyectoGradoDTO;
import co.edu.unicauca.project_microservice.domain.model.ProyectoGrado;
import org.springframework.stereotype.Component;

/**
 * Mapper entre DTOs de la capa de aplicación y el modelo de dominio
 */
@Component
public class ProyectoDTOMapper {

    /**
     * Convierte del DTO de request a modelo de dominio
     */
    public ProyectoGrado toDomain(CrearProyectoRequest request) {
        if (request == null) return null;
        
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setTitulo(request.getTitulo());
        proyecto.setModalidad(request.getModalidad());
        proyecto.setDirectorEmail(request.getDirectorEmail());
        proyecto.setCodirectorEmail(request.getCodirectorEmail());
        proyecto.setEstudiante1Email(request.getEstudiante1Email());
        proyecto.setEstudiante2Email(request.getEstudiante2Email());
        proyecto.setObjetivoGeneral(request.getObjetivoGeneral());
        proyecto.setObjetivosEspecificos(request.getObjetivosEspecificos());
        
        return proyecto;
    }

    /**
     * Convierte del modelo de dominio a DTO de respuesta
     */
    public ProyectoGradoDTO toDTO(ProyectoGrado domain) {
        if (domain == null) return null;
        
        ProyectoGradoDTO dto = new ProyectoGradoDTO();
        dto.setId(domain.getId());
        dto.setTitulo(domain.getTitulo());
        dto.setModalidad(domain.getModalidad());
        dto.setDirectorEmail(domain.getDirectorEmail());
        dto.setCodirectorEmail(domain.getCodirectorEmail());
        dto.setEstudiante1Email(domain.getEstudiante1Email());
        dto.setEstudiante2Email(domain.getEstudiante2Email());
        dto.setObjetivoGeneral(domain.getObjetivoGeneral());
        dto.setObjetivosEspecificos(domain.getObjetivosEspecificos());
        dto.setObservacionesEvaluacion(domain.getObservacionesEvaluacion());
        dto.setNumeroIntento(domain.getNumeroIntento());
        dto.setEstadoActual(domain.getEstadoActual());
        dto.setFechaCreacion(domain.getFechaCreacion());
        dto.setFechaAnteproyecto(domain.getFechaAnteproyecto());
        
        return dto;
    }
}