package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.dto.MovimientoDto;
import pe.edu.upeu.gestorfinanciero.model.Egreso;
import pe.edu.upeu.gestorfinanciero.model.Ingreso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final IngresoService ingresoService;
    private final EgresoService egresoService;

    public List<MovimientoDto> obtenerTodosLosMovimientos() {
        List<MovimientoDto> lista = new ArrayList<>();

        for (Ingreso i : ingresoService.listarIngresos()) {
            lista.add(new MovimientoDto(
                    "Ingreso",
                    "General",
                    i.getDescripcion(),
                    i.getMonto(),
                    i.getFecha()
            ));
        }

        for (Egreso e : egresoService.listarEgresos()) {
            lista.add(new MovimientoDto(
                    "Egreso",
                    e.getCategoria(),
                    e.getDescripcion(),
                    e.getMonto(),
                    e.getFecha()
            ));
        }

        // ordenar por fecha descendente
        lista.sort(Comparator.comparing(MovimientoDto::getFecha).reversed());
        return lista;
    }

    public double totalIngresos() {
        return ingresoService.listarIngresos().stream()
                .mapToDouble(Ingreso::getMonto).sum();
    }

    public double totalEgresos() {
        return egresoService.listarEgresos().stream()
                .mapToDouble(Egreso::getMonto).sum();
    }

    public double saldoActual() {
        return totalIngresos() - totalEgresos();
    }
}