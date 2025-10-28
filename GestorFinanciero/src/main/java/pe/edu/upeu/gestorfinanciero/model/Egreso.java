package pe.edu.upeu.gestorfinanciero.model;

import jakarta.persistence.*;
import javafx.beans.property.*;
import java.time.LocalDate;

/**
 * Representa un egreso (gasto) dentro del sistema financiero.
 * Compatible con JavaFX y JPA.
 */
@Entity
@Table(name = "egreso")
public class Egreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campos reales persistidos
    private String fecha;
    private String descripcion;
    private double monto;
    private double saldo;
    private String categoria;

    // Constructor vacío para JPA
    public Egreso() {}

    // Constructor para uso en la interfaz
    public Egreso(String fecha, String descripcion, double monto, double saldo, String categoria) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.monto = monto;
        this.saldo = saldo;
        this.categoria = categoria;
    }

    // === Getters y Setters para JPA ===
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

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    // === Propiedades JavaFX ===
    public StringProperty fechaProperty() { return new SimpleStringProperty(fecha); }
    public StringProperty descripcionProperty() { return new SimpleStringProperty(descripcion); }
    public DoubleProperty montoProperty() { return new SimpleDoubleProperty(monto); }
    public DoubleProperty saldoProperty() { return new SimpleDoubleProperty(saldo); }
    public StringProperty categoriaProperty() { return new SimpleStringProperty(categoria); }
}