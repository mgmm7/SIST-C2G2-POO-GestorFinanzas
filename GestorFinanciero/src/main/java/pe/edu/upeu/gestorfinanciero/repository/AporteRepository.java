package pe.edu.upeu.gestorfinanciero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.gestorfinanciero.model.Aporte;
import pe.edu.upeu.gestorfinanciero.model.Meta;

import java.util.List;

public interface AporteRepository extends JpaRepository<Aporte, Long> {
    List<Aporte> findByMetaOrderByFechaDesc(Meta meta);

    // eliminar aportes por meta
    void deleteByMeta(Meta meta);

    // listar por meta id
    List<Aporte> findByMetaIdOrderByFechaDesc(Long metaId);
}
