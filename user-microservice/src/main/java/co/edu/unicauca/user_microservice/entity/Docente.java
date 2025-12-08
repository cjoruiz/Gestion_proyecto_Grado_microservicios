package co.edu.unicauca.user_microservice.entity;

import co.edu.unicauca.user_microservice.entity.enums.TipoDocente;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Docente extends Usuario {
    
    @Enumerated(EnumType.STRING)
    private TipoDocente tipoDocente;

    public void setTipoDocente(TipoDocente tipoDocente) {
        this.tipoDocente = tipoDocente;
    }
    
    // Sobrecarga para compatibilidad con String
    public void setTipoDocente(String tipoDocente) {
        if (tipoDocente != null && !tipoDocente.isEmpty()) {
            this.tipoDocente = TipoDocente.valueOf(tipoDocente);
        }
    }
}