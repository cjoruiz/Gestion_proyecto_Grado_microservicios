package co.edu.unicauca.user_microservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
public abstract class Usuario {
    @Id
    // Asumiendo que el email es el identificador único y proviene de Keycloak
    @Column(unique = true, nullable = false)
    private String email;

    // Datos auxiliares que no son credenciales ni roles primarios
    private String nombres;
    private String apellidos;
    private String celular;
    private String programa;
    private String password; // Por ejemplo, INGENIERIA_SISTEMAS

    // Constructor con email (el identificador principal)
    public Usuario(String email) {
        this.email = email;
    }
}