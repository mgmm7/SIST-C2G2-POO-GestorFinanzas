package pe.edu.upeu.gestorfinanciero.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "categoria")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nombre;
    private double presupuesto;
    private double limite;
    private double saldoDisponible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Categoria() {}

    public Categoria(String nombre, Usuario usuario) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.presupuesto = 0;
        this.limite = 0;
        this.saldoDisponible = 0;
    }
}
