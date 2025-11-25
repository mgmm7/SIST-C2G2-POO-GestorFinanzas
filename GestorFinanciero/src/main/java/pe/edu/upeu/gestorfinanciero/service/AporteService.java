package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.gestorfinanciero.model.Aporte;
import pe.edu.upeu.gestorfinanciero.model.Meta;
import pe.edu.upeu.gestorfinanciero.repository.AporteRepository;
import pe.edu.upeu.gestorfinanciero.repository.MetaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AporteService {

    private final AporteRepository aporteRepository;
    private final MetaRepository metaRepository;
    private final IngresoService ingresoService;

    @Transactional
    public void registrar(Aporte aporte) {
        // 1) guardar aporte
        aporteRepository.save(aporte);

        // 2) actualizar meta (sumar aportado)
        Meta meta = metaRepository.findById(aporte.getMeta().getId()).orElseThrow();
        meta.setAportado(meta.getAportado() + aporte.getMonto());
        metaRepository.save(meta);

        // 3) descontar del saldo general (registra ingreso negativo)
        ingresoService.descontar(aporte.getMonto(),
                "Aporte a meta: " + aporte.getMeta().getNombre(),
                aporte.getUsuario());
    }

    public List<Aporte> listarPorMeta(Meta meta) {
        return aporteRepository.findByMetaOrderByFechaDesc(meta);
    }

    // -----------------------------
    // editar aporte existente
    // - actualiza aporte en BD
    // - ajusta meta.aportado (resta monto viejo, suma nuevo)
    // - actualiza ingreso negativo asociado
    // -----------------------------
    @Transactional
    public void editarAporte(Long aporteId, double nuevoMonto, String nuevaDescripcion) {
        Aporte orig = aporteRepository.findById(aporteId).orElseThrow();

        double montoViejo = orig.getMonto();
        Meta meta = metaRepository.findById(orig.getMeta().getId()).orElseThrow();

        // 1) actualizar aporte
        orig.setMonto(nuevoMonto);
        orig.setDescripcion(nuevaDescripcion);
        aporteRepository.save(orig);

        // 2) ajustar meta.aportado
        double delta = nuevoMonto - montoViejo; // puede ser positivo o negativo
        meta.setAportado(meta.getAportado() + delta);
        metaRepository.save(meta);

        // 3) actualizar ingreso negativo:
        String descripcionBase = "Aporte a meta: " + meta.getNombre();
        // eliminar ingresos existentes relacionados con esa meta y usuario
        ingresoService.eliminarIngresosPorDescripcionYUsuario(descripcionBase, orig.getUsuario());
        // volver a crear ingreso negativo con el nuevo monto
        ingresoService.descontar(nuevoMonto, descripcionBase, orig.getUsuario());
    }

    // -----------------------------
    // eliminar aporte
    // -----------------------------
    @Transactional
    public void eliminarAporte(Long aporteId) {
        Aporte orig = aporteRepository.findById(aporteId).orElseThrow();
        Meta meta = metaRepository.findById(orig.getMeta().getId()).orElseThrow();

        // 1) restar del aportado
        meta.setAportado(Math.max(0.0, meta.getAportado() - orig.getMonto()));
        metaRepository.save(meta);

        // 2) eliminar ingreso negativo asociado (por descripción)
        String descripcionBase = "Aporte a meta: " + meta.getNombre();
        ingresoService.eliminarIngresosPorDescripcionYUsuario(descripcionBase, orig.getUsuario());

        // 3) eliminar aporte
        aporteRepository.delete(orig);
    }

    // -----------------------------
    // eliminar TODOS los aportes de una meta (usado al borrar la meta)
    // -----------------------------
    @Transactional
    public void eliminarAportesPorMeta(Meta meta) {
        // eliminamos los ingresos relacionados por cada aporte antes de borrar
        List<Aporte> aportes = aporteRepository.findByMetaOrderByFechaDesc(meta);
        for (Aporte a : aportes) {
            String descripcionBase = "Aporte a meta: " + meta.getNombre();
            ingresoService.eliminarIngresosPorDescripcionYUsuario(descripcionBase, a.getUsuario());
        }
        aporteRepository.deleteByMeta(meta);
    }
}
