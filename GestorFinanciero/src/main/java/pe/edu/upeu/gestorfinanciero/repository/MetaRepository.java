package pe.edu.upeu.gestorfinanciero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.gestorfinanciero.model.Meta;

import java.util.List;

public interface MetaRepository extends JpaRepository<Meta, Long> {
    // Aquí puedes agregar métodos personalizados si necesitas realizar consultas específicas
    @Query(value = "SELECT m.* FROM upeu_meta m WHERE m.nombre like:filter", nativeQuery = true)
    List<Meta> listAutoCompletMeta(@Param("filter") String filter);
}
