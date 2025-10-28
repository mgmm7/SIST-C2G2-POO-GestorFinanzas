package pe.edu.upeu.gestorfinanciero.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.dto.ModeloDataAutocomplet;
import pe.edu.upeu.gestorfinanciero.model.Meta;
import pe.edu.upeu.gestorfinanciero.repository.MetaRepository;
import pe.edu.upeu.gestorfinanciero.service.MetaIService;

import java.util.ArrayList;
import java.util.List;

@Service
public class MetaServiceImp implements MetaIService {

    private static final Logger logger = LoggerFactory.getLogger(MetaServiceImp.class);
    @Autowired
    MetaRepository mRepo;

    @Override
    public Meta save(Meta meta) {
        return mRepo.save(meta);
    }

    @Override
    public List<Meta> findAll() {
        return mRepo.findAll();
    }

    @Override
    public Meta update(Meta meta) {
        return mRepo.save(meta);
    }

    @Override
    public void delete(Long id) {
        mRepo.deleteById(id);
    }

    @Override
    public Meta findById(Long id) {
        return mRepo.findById(id).orElse(null);
    }

    @Override
    public List<ModeloDataAutocomplet> listAutoCompletMeta(String nombre) {
        List<ModeloDataAutocomplet> listarMeta = new ArrayList<>();
        try {
            for (Meta meta : mRepo.listAutoCompletMeta(nombre + "%")) {
                ModeloDataAutocomplet data = new ModeloDataAutocomplet();
                data.setIdx(meta.getNombre());

                data.setNameDysplay(String.valueOf(meta.getIdmeta()));
                data.setOtherData(meta.getMobjetivo() + ":" +
                        meta.getMactual());
                listarMeta.add(data);
            }
        } catch (Exception e) {
            logger.error("Error al realizar la busqueda", e);
        }
        return listarMeta;
    }

    @Override
    public List<ModeloDataAutocomplet> listAutoCompletMeta() {
        List<ModeloDataAutocomplet> listarMeta = new ArrayList<>();
        try {
            for (Meta meta : mRepo.findAll()) {
                ModeloDataAutocomplet data = new ModeloDataAutocomplet();
                data.setIdx(String.valueOf(meta.getIdmeta()));
                data.setNameDysplay(meta.getNombre());
                data.setOtherData(meta.getMobjetivo() + ":" +
                        meta.getMactual());
                listarMeta.add(data);
            }
        } catch (Exception e) {
            logger.error("Error al realizar la busqueda", e);
        }
        return listarMeta;
    }
}
