package pe.edu.upeu.gestorfinanciero.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.repository.UsuarioRepository;
import pe.edu.upeu.gestorfinanciero.service.UsuarioIService;


@Service
public class UsuarioIServiceImp implements UsuarioIService {
    @Autowired
    UsuarioRepository uRepo;

    @Override
    public Usuario findById(Long id) {
        return uRepo.findById(id).orElse(null);
    }
}
