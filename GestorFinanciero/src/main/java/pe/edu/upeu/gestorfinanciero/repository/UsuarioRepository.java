package pe.edu.upeu.gestorfinanciero.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.gestorfinanciero.model.Perfil;
import pe.edu.upeu.gestorfinanciero.model.Usuario;

public interface UsuarioRepository extends CrudRepository<Perfil, Long> {
    @Query(value = "SELECT u.* FROM upeu_usuario u WHERE u.user=:userx ",
            nativeQuery = true)
    Usuario buscarUsuario(@Param("userx") String userx);
    @Query(value = "SELECT u.* FROM upeu_usuario u WHERE u.user=:user and u.clave=:clave", nativeQuery = true)
    Usuario loginUsuario(@Param("user") String user, @Param("clave") String
            clave);
}
