package pe.edu.upeu.gestorfinanciero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.gestorfinanciero.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
