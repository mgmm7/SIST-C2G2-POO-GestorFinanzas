package pe.edu.upeu.gestorfinanciero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.gestorfinanciero.model.Categoria;
import pe.edu.upeu.gestorfinanciero.model.Usuario;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Categoria findByNombreAndUsuario(String nombre, Usuario usuario);
    List<Categoria> findByUsuarioOrderByNombreAsc(Usuario usuario);
    void deleteByNombreAndUsuario(String nombre, Usuario usuario);
}
