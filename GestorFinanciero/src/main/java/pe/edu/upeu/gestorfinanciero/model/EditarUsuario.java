package pe.edu.upeu.gestorfinanciero.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="upeu_usuario")
public class EditarUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario") // OBLIGATORIO
    private Long idUsuario;
    private String user;
    private String clave;
    @Column(name = "estado", length = 10)
    private String estado = "Activo"; // Valor por defecto
    @ManyToOne
    @JoinColumn(name = "id_perfil", nullable = false)
    private Perfil perfil;
}

