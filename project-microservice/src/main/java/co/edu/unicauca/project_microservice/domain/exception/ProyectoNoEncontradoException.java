package co.edu.unicauca.project_microservice.domain.exception;

public class ProyectoNoEncontradoException extends RuntimeException {
    public ProyectoNoEncontradoException(String message) {
        super(message);
    }
}
