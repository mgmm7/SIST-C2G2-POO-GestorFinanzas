package pe.edu.upeu.gestorfinanciero.config;

import lombok.Data;
import org.springframework.stereotype.Component;
import pe.edu.upeu.gestorfinanciero.model.Usuario;


@Data
@Component
public class UsuarioSesion {

    private Usuario usuarioActual;

    public void login(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public boolean estaLogueado() {
        return usuarioActual != null;
    }

    public void logout() {
        usuarioActual = null;
    }
}
