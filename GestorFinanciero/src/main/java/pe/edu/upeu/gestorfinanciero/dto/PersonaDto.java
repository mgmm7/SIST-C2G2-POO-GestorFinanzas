package pe.edu.upeu.gestorfinanciero.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonaDto {
    String dni, nombre, apellidoPaterno, apellidoMaterno;
}
