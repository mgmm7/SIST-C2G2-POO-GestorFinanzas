package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.dto.MovimientoDto;
import pe.edu.upeu.gestorfinanciero.model.Categoria;
import pe.edu.upeu.gestorfinanciero.model.Egreso;
import pe.edu.upeu.gestorfinanciero.model.Ingreso;
import pe.edu.upeu.gestorfinanciero.model.Usuario;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final IngresoService ingresoService;
    private final EgresoService egresoService;
    private final CategoriaService categoriaService;

    // ============================================================
    //                    MOVIMIENTOS GENERALES
    // ============================================================
    public List<MovimientoDto> obtenerMovimientosGenerales(Usuario usuario) {

        List<MovimientoDto> lista = new ArrayList<>();

        // INGRESOS DEL USUARIO
        for (Ingreso i : ingresoService.listar(usuario)) {
            lista.add(new MovimientoDto(
                    i.getId(),
                    i.getTipo(),
                    "General",
                    i.getDescripcion(),
                    i.getMonto(),
                    i.getFecha(),
                    null
            ));
        }

        // EGRESOS DEL USUARIO
        for (Egreso e : egresoService.listar(usuario)) {
            lista.add(new MovimientoDto(
                    e.getId(),
                    "Egreso",
                    e.getCategoria(),
                    e.getDescripcion(),
                    e.getMonto(),
                    e.getFecha(),
                    null
            ));
        }

        lista.sort(Comparator.comparing(MovimientoDto::getFecha).reversed());
        return lista;
    }

    // ============================================================
    //               MOVIMIENTOS POR CATEGORÍA
    // ============================================================
    public List<MovimientoDto> obtenerMovimientosPorCategoria(Usuario usuario, Categoria categoria) {

        List<MovimientoDto> lista = new ArrayList<>();

        // 1. Presupuesto base (si lo usas como movimiento)
        if (categoria.getPresupuesto() > 0) {
            lista.add(new MovimientoDto(
                    null, // No existe ID real
                    "Presupuesto",
                    categoria.getNombre(),
                    "Asignación de presupuesto",
                    categoria.getPresupuesto(),
                    "N/A",
                    null
            ));
        }

        // 2. Egresos por categoría
        for (Egreso e : egresoService.listar(usuario)) {
            if (e.getCategoria().equals(categoria.getNombre())) {
                lista.add(new MovimientoDto(
                        e.getId(),
                        "Egreso",
                        categoria.getNombre(),
                        e.getDescripcion(),
                        e.getMonto(),
                        e.getFecha(),
                        null
                ));
            }
        }

        lista.sort(Comparator.comparing(MovimientoDto::getFecha).reversed());
        return lista;
    }

    // ============================================================
    //                        TOTALES
    // ============================================================
    public double totalIngresos(Usuario usuario) {
        return ingresoService.listar(usuario)
                .stream().mapToDouble(Ingreso::getMonto).sum();
    }

    public double totalEgresos(Usuario usuario) {
        return egresoService.listar(usuario)
                .stream().mapToDouble(Egreso::getMonto).sum();
    }
}
