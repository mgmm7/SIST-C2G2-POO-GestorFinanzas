package pe.edu.upeu.gestorfinanciero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.gestorfinanciero.model.EditarUsuario;

import java.util.List;

public interface EditarUsuarioRepository extends JpaRepository<EditarUsuario, Long> {

    List<EditarUsuario> findByUserContainingIgnoreCase(String user);

}
