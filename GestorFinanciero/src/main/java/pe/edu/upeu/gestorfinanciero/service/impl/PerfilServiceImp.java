package pe.edu.upeu.gestorfinanciero.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.model.Perfil;
import pe.edu.upeu.gestorfinanciero.repository.ICrudGenericoRepository;
import pe.edu.upeu.gestorfinanciero.repository.PerfilRepository;
import pe.edu.upeu.gestorfinanciero.service.IPerfilService;

@RequiredArgsConstructor
@Service
public class PerfilServiceImp extends CrudGenericoServiceImp<Perfil, Long> implements IPerfilService {

    private final PerfilRepository perfilRepository;

    @Override
    protected ICrudGenericoRepository<Perfil, Long> getRepo() {
        return perfilRepository;
    }
}

