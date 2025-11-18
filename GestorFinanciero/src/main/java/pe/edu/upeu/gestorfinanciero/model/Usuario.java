package pe.edu.upeu.gestorfinanciero.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_usuario", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")
})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // BCrypt hash

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}