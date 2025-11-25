package pe.edu.upeu.gestorfinanciero.model;

import jakarta.persistence.*;
import javafx.beans.property.*;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "ingreso")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String fecha;
    private String descripcion;
    private double monto;
    private double saldo;

    // NUEVO: tipo de ingreso (normal, asignación, meta…)
    private String tipo = "Ingreso";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Ingreso() {}

    public Ingreso(String fecha, String descripcion, double monto, double saldo, Usuario usuario) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.monto = monto;
        this.saldo = saldo;
        this.usuario = usuario;
        this.tipo = "Ingreso"; // por defecto
    }

    // ==== GETTERS & SETTERS ====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    // ==== PROPIEDADES JAVAFX ====
    public StringProperty fechaProperty() { return new SimpleStringProperty(fecha); }
    public StringProperty descripcionProperty() { return new SimpleStringProperty(descripcion); }
    public DoubleProperty montoProperty() { return new SimpleDoubleProperty(monto); }
    public DoubleProperty saldoProperty() { return new SimpleDoubleProperty(saldo); }
    public StringProperty tipoProperty() { return new SimpleStringProperty(tipo); }
}
