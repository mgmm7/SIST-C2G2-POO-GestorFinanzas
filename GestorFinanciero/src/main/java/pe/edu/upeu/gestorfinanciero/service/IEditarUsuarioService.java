package pe.edu.upeu.gestorfinanciero.service;

import pe.edu.upeu.gestorfinanciero.model.EditarUsuario;
import java.util.List;

public interface IEditarUsuarioService {
    void save(EditarUsuario editarUsuario); //C

    List<EditarUsuario> findAll(); // R

    EditarUsuario update(EditarUsuario editarUsuario);//U

    void delete(Long usuario); //D

    EditarUsuario findById(Long usuario); //Buscar
    List<EditarUsuario> findByUserContainingIgnoreCase(String user);




}
