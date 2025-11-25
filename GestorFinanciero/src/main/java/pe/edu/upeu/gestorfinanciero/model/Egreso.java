package pe.edu.upeu.gestorfinanciero.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "egreso")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Egreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String fecha;
    private String descripcion;
    private double monto;
    private double saldo;    // cómo quedó el saldo tras el gasto
    private String categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Egreso() {}

    public Egreso(String fecha, String descripcion, double monto, double saldo, String categoria, Usuario usuario) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.monto = monto;
        this.saldo = saldo;
        this.categoria = categoria;
        this.usuario = usuario;
    }
}
