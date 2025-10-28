package pe.edu.upeu.gestorfinanciero.service;

import pe.edu.upeu.gestorfinanciero.dto.ModeloDataAutocomplet;
import pe.edu.upeu.gestorfinanciero.model.Meta;

import java.util.List;

public interface MetaIService {
    Meta save(Meta meta);

    List<Meta> findAll();

    Meta update(Meta meta);

    void delete(Long id);

    Meta findById(Long id);

    List<ModeloDataAutocomplet> listAutoCompletMeta(String nombre);

    public List<ModeloDataAutocomplet> listAutoCompletMeta();
}
