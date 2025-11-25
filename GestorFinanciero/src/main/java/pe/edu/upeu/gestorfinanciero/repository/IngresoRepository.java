package pe.edu.upeu.gestorfinanciero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.gestorfinanciero.model.Ingreso;
import pe.edu.upeu.gestorfinanciero.model.Usuario;

import java.util.List;

@Repository
public interface IngresoRepository extends JpaRepository<Ingreso, Long> {

    List<Ingreso> findByUsuarioOrderByIdDesc(Usuario usuario);

    // nuevo: buscar por usuario y descripción parcial (para localizar ingresos creados por aportes)
    List<Ingreso> findByUsuarioAndDescripcionContaining(Usuario usuario, String descripcionPart);
}
