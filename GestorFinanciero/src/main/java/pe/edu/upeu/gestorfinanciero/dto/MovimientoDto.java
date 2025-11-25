package pe.edu.upeu.gestorfinanciero.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MovimientoDto {

    private Long id;            // ID del movimiento
    private String tipo;        // Ingreso, Egreso, Asignación, Descuento
    private String categoria;   // General o nombre de categoría
    private String descripcion;
    private double monto;
    private String fecha;       // String (por tu entidad)
    private String evidencia;   // Nombre de archivo o ruta
}
