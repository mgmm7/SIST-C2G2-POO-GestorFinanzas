package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.model.Categoria;
import pe.edu.upeu.gestorfinanciero.repository.CategoriaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public Categoria crearCategoria(String nombre) {
        if (categoriaRepository.findByNombre(nombre) != null) {
            return null; // ya existe
        }
        Categoria c = new Categoria();
        c.setNombre(nombre);
        return categoriaRepository.save(c);
    }

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria asignarPresupuesto(String nombre, Double monto) {
        Categoria c = categoriaRepository.findByNombre(nombre);
        if (c != null) {
            c.setPresupuesto(monto);
            return categoriaRepository.save(c);
        }
        return null;
    }

    public Categoria asignarLimite(String nombre, Double monto) {
        Categoria c = categoriaRepository.findByNombre(nombre);
        if (c != null) {
            c.setLimite(monto);
            return categoriaRepository.save(c);
        }
        return null;
    }
}