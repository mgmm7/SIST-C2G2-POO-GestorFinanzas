package pe.edu.upeu.gestorfinanciero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.gestorfinanciero.model.Meta;
import pe.edu.upeu.gestorfinanciero.model.Usuario;

import java.util.List;

public interface MetaRepository extends JpaRepository<Meta, Long> {

    List<Meta> findByUsuarioOrderByIdDesc(Usuario usuario);

    Meta findByIdAndUsuario(Long id, Usuario usuario);
}
