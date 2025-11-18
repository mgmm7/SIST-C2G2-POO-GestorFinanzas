package pe.edu.upeu.gestorfinanciero.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MovimientoDto {
    private String tipo;        // Ingreso o Egreso
    private String categoria;   // General o nombre categoria
    private String descripcion;
    private double monto;
    private String fecha;       // String porque tus entidades usan String
}