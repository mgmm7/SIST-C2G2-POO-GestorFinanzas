package pe.edu.upeu.gestorfinanciero.model;

import jakarta.persistence.*;
import javafx.beans.property.*;
import java.time.LocalDate;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import pe.edu.upeu.gestorfinanciero.model.Usuario;


@Entity
@Table(name = "ingreso")
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fecha;
    private String descripcion;
    private double monto;
    private double saldo;

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
    }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

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

    // Propiedades JavaFX
    public StringProperty fechaProperty() { return new SimpleStringProperty(fecha); }
    public StringProperty descripcionProperty() { return new SimpleStringProperty(descripcion); }
    public DoubleProperty montoProperty() { return new SimpleDoubleProperty(monto); }
    public DoubleProperty saldoProperty() { return new SimpleDoubleProperty(saldo); }
}