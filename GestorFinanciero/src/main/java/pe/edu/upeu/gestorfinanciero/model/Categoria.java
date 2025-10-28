package pe.edu.upeu.gestorfinanciero.model;

import jakarta.persistence.*;
import javafx.beans.property.*;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private double presupuesto;
    private double limite;
    private double saldoDisponible;

    public Categoria() {}

    public Categoria(String nombre, double presupuesto, double limite, double saldoDisponible) {
        this.nombre = nombre;
        this.presupuesto = presupuesto;
        this.limite = limite;
        this.saldoDisponible = saldoDisponible;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPresupuesto() { return presupuesto; }
    public void setPresupuesto(double presupuesto) { this.presupuesto = presupuesto; }

    public double getLimite() { return limite; }
    public void setLimite(double limite) { this.limite = limite; }

    public double getSaldoDisponible() { return saldoDisponible; }
    public void setSaldoDisponible(double saldoDisponible) { this.saldoDisponible = saldoDisponible; }

    public StringProperty nombreProperty() { return new SimpleStringProperty(nombre); }
    public DoubleProperty presupuestoProperty() { return new SimpleDoubleProperty(presupuesto); }
    public DoubleProperty limiteProperty() { return new SimpleDoubleProperty(limite); }
    public DoubleProperty saldoDisponibleProperty() { return new SimpleDoubleProperty(saldoDisponible); }
}