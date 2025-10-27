package co.edu.unicauca.project_microservice.service;

import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import co.edu.unicauca.project_microservice.entity.estados.EnPrimeraEvaluacionState;
import co.edu.unicauca.project_microservice.entity.estados.EnSegundaEvaluacionState;
import co.edu.unicauca.project_microservice.entity.estados.EnTerceraEvaluacionState;
import co.edu.unicauca.project_microservice.entity.estados.FormatoAAprobadoState;
import co.edu.unicauca.project_microservice.entity.estados.FormatoARechazadoState;
import co.edu.unicauca.project_microservice.entity.estados.RechazadoDefinitivoState;
import co.edu.unicauca.project_microservice.repository.ProyectoRepository;
import co.edu.unicauca.project_microservice.utilities.exception.ProyectoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProyectoService implements IProyectoService {

        @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private EnPrimeraEvaluacionState enPrimera; 
    @Autowired
    private EnSegundaEvaluacionState enSegunda;
    @Autowired
    private EnTerceraEvaluacionState enTercera;
    @Autowired
    private FormatoAAprobadoState aprobado;
    @Autowired
    private FormatoARechazadoState rechazado;
    @Autowired
    private RechazadoDefinitivoState definitivo;


    @Override
    public ProyectoGrado crear(ProyectoGrado proyecto) {
        return proyectoRepository.save(proyecto);
    }

     @Override
    public ProyectoGrado obtenerPorId(Long id) {
        ProyectoGrado p = proyectoRepository.findById(id)
                .orElseThrow(() -> new ProyectoNoEncontradoException("Proyecto no encontrado con ID: " + id));

        p.inicializarEstado(enPrimera, enSegunda, enTercera, aprobado, rechazado, definitivo);

        return p;
    }

    @Override
    public List<ProyectoGrado> findByEstudiante1Email(String email) {
        return proyectoRepository.findByEstudiante1Email(email);
    }

    @Override
    public List<ProyectoGrado> findByDirectorEmail(String email) {
        return proyectoRepository.findByDirectorEmail(email);
    }

    @Override
    public List<ProyectoGrado> obtenerTodos() {
        return proyectoRepository.findAll();
    }

    @Override
    public ProyectoGrado guardar(ProyectoGrado proyecto) {
        return proyectoRepository.save(proyecto);
    }
}
