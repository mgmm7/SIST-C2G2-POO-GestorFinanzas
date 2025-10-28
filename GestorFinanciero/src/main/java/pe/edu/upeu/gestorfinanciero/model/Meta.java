package pe.edu.upeu.gestorfinanciero.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.gestorfinanciero.enums.Estado;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "upeu_meta")
public class Meta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_meta")
    private long idmeta;
    @NotNull(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 120, message = "El nombre debe tener entre 2 y 120 caracteres")
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;
    @Positive(message = "El Monto Objetivo debe ser positivo")
    @Column(name = "m_objetivo", nullable = false, precision = 10, scale = 2)
    private BigDecimal mobjetivo;
    @PositiveOrZero(message = "El Monto Actual debe ser positivo o cero")
    @Column(name = "m_actual", nullable = false, precision = 10, scale = 2)
    private BigDecimal mactual;
    @NotNull(message = "La fecha del registro no puede estar vacío")
    @Column(name = "f_creacion")
    private LocalDate fcreacion;
    @NotNull(message = "La fecha límite no puede estar vacío")
    @Column(name = "f_limite", nullable = false)
    private LocalDate flimite;
    @Column(name = "f_actualizacion", nullable = false)
    private LocalDate factualizacion = LocalDate.now();
    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private Estado estado;
    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuario;

    @PreUpdate
    public void preUpdate() {
        this.factualizacion = LocalDate.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.factualizacion == null) {
            this.factualizacion = LocalDate.now();
        }
        if (this.fcreacion == null) {
            this.fcreacion = LocalDate.now();
        }
    }
}
