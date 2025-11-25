package pe.edu.upeu.gestorfinanciero.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nombre;
    private double objetivo;

    @Column(name = "total_aportado", nullable = false)
    private double aportado = 0.0;

    private LocalDate fechaRegistro;
    private LocalDate fechaLimite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public double getProgreso() {
        if (objetivo == 0) return 0.0;
        return Math.min(1.0, aportado / objetivo);
    }
}

