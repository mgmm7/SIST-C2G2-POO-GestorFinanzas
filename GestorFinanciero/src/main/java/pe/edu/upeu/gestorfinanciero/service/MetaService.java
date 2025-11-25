package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.gestorfinanciero.model.Aporte;
import pe.edu.upeu.gestorfinanciero.model.Meta;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.repository.AporteRepository;
import pe.edu.upeu.gestorfinanciero.repository.MetaRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetaService {

    private final MetaRepository metaRepository;
    private final AporteRepository aporteRepository;
    private final AporteService aporteService;

    // ⭐ LO QUE NECESITA ReporteController
    public List<Meta> listarMetasUsuario(Usuario usuario) {
        return metaRepository.findByUsuarioOrderByIdDesc(usuario);
    }

    @Transactional
    public Meta crearMeta(String nombre, double objetivo, LocalDate fechaLimite, Usuario usuario) {
        Meta meta = new Meta();
        meta.setNombre(nombre);
        meta.setObjetivo(objetivo);
        meta.setAportado(0.0);   // evitar NOT NULL
        meta.setFechaRegistro(LocalDate.now());
        meta.setFechaLimite(fechaLimite);
        meta.setUsuario(usuario);
        return metaRepository.save(meta);
    }

    // sobrecarga si en algún lugar creas y pasas la entidad completa
    @Transactional
    public Meta crearMeta(Meta meta) {
        if (meta.getAportado() == 0.0) meta.setAportado(0.0);
        if (meta.getFechaRegistro() == null) meta.setFechaRegistro(LocalDate.now());
        return metaRepository.save(meta);
    }

    @Transactional
    public void editarMeta(Long idMeta, String nuevoNombre, Double nuevoObjetivo, LocalDate nuevaFecha, Usuario usuario) {
        Meta meta = metaRepository.findByIdAndUsuario(idMeta, usuario);
        if (meta == null) return;
        if (nuevoNombre != null) meta.setNombre(nuevoNombre);
        if (nuevoObjetivo != null) meta.setObjetivo(nuevoObjetivo);
        if (nuevaFecha != null) meta.setFechaLimite(nuevaFecha);
        metaRepository.save(meta); // actualizar la entidad existente (no crear)
    }

    @Transactional
    public void eliminarMeta(Long idMeta, Usuario usuario) {
        Meta meta = metaRepository.findByIdAndUsuario(idMeta, usuario);
        if (meta != null) {
            // 1) eliminar aportes e ingresos relacionados
            aporteService.eliminarAportesPorMeta(meta);

            // 2) eliminar la meta
            metaRepository.delete(meta);
        }
    }

    @Transactional
    public void actualizarAportado(Meta meta, double monto) {
        Meta m = metaRepository.findById(meta.getId()).orElseThrow();
        m.setAportado(m.getAportado() + monto);
        metaRepository.save(m);
    }

    @Transactional
    public void agregarAporte(Long idMeta, double monto, String descripcion, String evidencia, Usuario usuario) {
        Meta meta = metaRepository.findByIdAndUsuario(idMeta, usuario);
        if (meta == null) return;

        Aporte a = new Aporte();
        a.setMeta(meta);
        a.setUsuario(usuario);
        a.setMonto(monto);
        a.setDescripcion(descripcion);
        a.setEvidencia(evidencia);
        a.setFecha(LocalDate.now());

        aporteService.registrar(a);
    }

    public List<Aporte> listarAportesMeta(Meta meta) {
        return aporteRepository.findByMetaOrderByFechaDesc(meta);
    }
}
