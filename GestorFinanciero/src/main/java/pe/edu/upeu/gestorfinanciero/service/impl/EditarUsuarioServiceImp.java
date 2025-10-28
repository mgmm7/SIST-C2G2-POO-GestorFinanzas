package pe.edu.upeu.gestorfinanciero.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.model.EditarUsuario;
import pe.edu.upeu.gestorfinanciero.repository.EditarUsuarioRepository;
import pe.edu.upeu.gestorfinanciero.service.IEditarUsuarioService;

import java.util.List;
@Service
public class EditarUsuarioServiceImp implements IEditarUsuarioService {

    @Autowired
    EditarUsuarioRepository editarUsuarioRepository;

    @Override
    public void save(EditarUsuario editarUsuario) {
        editarUsuarioRepository.save(editarUsuario);
    }

    @Override
    public List<EditarUsuario> findAll() {
        return editarUsuarioRepository.findAll();
    }

    @Override
    public EditarUsuario update(EditarUsuario editarUsuario) {
        return editarUsuarioRepository.save(editarUsuario);
    }

    @Override
    public void delete(Long usuario) {
        editarUsuarioRepository.deleteById(usuario);
    }

    @Override
    public EditarUsuario findById(Long usuario) {
        return editarUsuarioRepository.findById(usuario).orElseThrow();

    }
    @Override
    public List<EditarUsuario> findByUserContainingIgnoreCase(String user) {
        return editarUsuarioRepository.findByUserContainingIgnoreCase(user);
    }
}
