package co.edu.unicauca.project_microservice.entity;

import co.edu.unicauca.project_microservice.entity.estados.EnPrimeraEvaluacionState;
import co.edu.unicauca.project_microservice.entity.estados.EnSegundaEvaluacionState;
import co.edu.unicauca.project_microservice.entity.estados.EnTerceraEvaluacionState;
import co.edu.unicauca.project_microservice.entity.estados.FormatoAAprobadoState;
import co.edu.unicauca.project_microservice.entity.estados.FormatoARechazadoState;
import co.edu.unicauca.project_microservice.entity.estados.RechazadoDefinitivoState;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proyectos")
@Data
@NoArgsConstructor
public class ProyectoGrado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String modalidad;
    private String directorEmail;
    private String codirectorEmail;
    private String estudiante1Email;
    private String estudiante2Email;
    private String objetivoGeneral;
    private String objetivosEspecificos;
    private String observacionesEvaluacion;
    private int numeroIntento = 1;
    private String estadoActual; // Persistente

    @Transient
    private EstadoProyecto estado;

    public void setEstado(EstadoProyecto estado) {
        this.estado = estado;
        if (estado != null) {
            this.estadoActual = estado.getNombreEstado();
        }
    }

    public void inicializarEstado(EnPrimeraEvaluacionState enPrimera,
            EnSegundaEvaluacionState enSegunda,
            EnTerceraEvaluacionState enTercera,
            FormatoAAprobadoState aprobado,
            FormatoARechazadoState rechazado,
            RechazadoDefinitivoState definitivo) {
        switch (this.estadoActual) {
            case "EN_PRIMERA_EVALUACION_FORMATO_A":
                this.estado = enPrimera;
                break;
            case "EN_SEGUNDA_EVALUACION_FORMATO_A":
                this.estado = enSegunda;
                break;
            case "EN_TERCERA_EVALUACION_FORMATO_A":
                this.estado = enTercera;
                break;
            case "FORMATO_A_APROBADO":
                this.estado = aprobado;
                break;
            case "FORMATO_A_RECHAZADO":
                this.estado = rechazado;
                break;
            case "RECHAZADO_DEFINITIVO":
                this.estado = definitivo;
                break;
            default:
                throw new IllegalStateException("Estado desconocido: " + this.estadoActual);
        }
    }

    public String getEstadoActual() {
        return estadoActual != null ? estadoActual : "DESCONOCIDO";
    }

    public void evaluar(boolean aprobado, String observaciones) {
        if (estado == null)
            throw new IllegalStateException("Estado no inicializado.");
        estado.evaluar(this, aprobado, observaciones);
    }

    public void reintentar() {
        if (estado == null)
            throw new IllegalStateException("Estado no inicializado.");
        estado.reintentar(this);
    }
}