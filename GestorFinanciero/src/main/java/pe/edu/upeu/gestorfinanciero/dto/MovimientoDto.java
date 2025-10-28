package pe.edu.upeu.gestorfinanciero.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MovimientoDto {
    private String tipo; // "Ingreso" o "Egreso"
    private String categoria;
    private String descripcion;
    private double monto;
    private String fecha;
}