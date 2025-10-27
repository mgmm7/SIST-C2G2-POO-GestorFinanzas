package pe.edu.upeu.gestorfinanciero.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="particpante")
public class Participante {
    @Id
    private String usuario;
    private String contrasenia;
    private Boolean estado;

}

