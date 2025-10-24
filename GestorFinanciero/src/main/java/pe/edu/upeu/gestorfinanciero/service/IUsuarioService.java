package pe.edu.upeu.gestorfinanciero.service;

import pe.edu.upeu.gestorfinanciero.model.Usuario;

public interface IUsuarioService extends ICrudGenericoService<Usuario,Long>{
    Usuario loginUsuario(String user, String clave);

}