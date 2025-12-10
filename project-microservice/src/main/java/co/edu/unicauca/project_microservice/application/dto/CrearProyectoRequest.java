package co.edu.unicauca.project_microservice.application.dto;

public class CrearProyectoRequest {
    private String titulo;
    private String modalidad;
    private String directorEmail;
    private String codirectorEmail;
    private String estudiante1Email;
    private String estudiante2Email;
    private String objetivoGeneral;
    private String objetivosEspecificos;

    // Getters y Setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public String getDirectorEmail() {
        return directorEmail;
    }

    public void setDirectorEmail(String directorEmail) {
        this.directorEmail = directorEmail;
    }

    public String getCodirectorEmail() {
        return codirectorEmail;
    }

    public void setCodirectorEmail(String codirectorEmail) {
        this.codirectorEmail = codirectorEmail;
    }

    public String getEstudiante1Email() {
        return estudiante1Email;
    }

    public void setEstudiante1Email(String estudiante1Email) {
        this.estudiante1Email = estudiante1Email;
    }

    public String getEstudiante2Email() {
        return estudiante2Email;
    }

    public void setEstudiante2Email(String estudiante2Email) {
        this.estudiante2Email = estudiante2Email;
    }

    public String getObjetivoGeneral() {
        return objetivoGeneral;
    }

    public void setObjetivoGeneral(String objetivoGeneral) {
        this.objetivoGeneral = objetivoGeneral;
    }

    public String getObjetivosEspecificos() {
        return objetivosEspecificos;
    }

    public void setObjetivosEspecificos(String objetivosEspecificos) {
        this.objetivosEspecificos = objetivosEspecificos;
    }
}