package pe.edu.upeu.gestorfinanciero.service;

import pe.edu.upeu.gestorfinanciero.dto.MenuMenuItenTO;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface IMenuMenuItemDao {
    List<MenuMenuItenTO> listaAccesos(String perfil, Properties idioma);
    Map<String, String[]> accesosAutorizados(List<MenuMenuItenTO> accesos);
}
