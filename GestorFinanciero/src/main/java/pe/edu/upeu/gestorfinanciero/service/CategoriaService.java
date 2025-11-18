package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.model.Categoria;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.repository.CategoriaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    // Crear una categoría para un usuario
    public Categoria crearCategoria(String nombre, Usuario usuario) {
        if (categoriaRepository.findByNombreAndUsuario(nombre, usuario) != null) {
            return null; // ya existe
        }
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setUsuario(usuario);
        c.setPresupuesto(0);
        c.setLimite(0);
        c.setSaldoDisponible(0);
        return categoriaRepository.save(c);
    }

    // Listar categorías de un usuario
    public List<Categoria> listarCategorias(Usuario usuario) {
        return categoriaRepository.findByUsuarioOrderByNombreAsc(usuario);
    }

    // Asignar presupuesto
    public Categoria asignarPresupuesto(String nombre, Double monto, Usuario usuario) {
        Categoria c = categoriaRepository.findByNombreAndUsuario(nombre, usuario);
        if (c != null) {
            c.setPresupuesto(monto);
            c.setSaldoDisponible(monto);
            return categoriaRepository.save(c);
        }
        return null;
    }

    // Asignar límite
    public Categoria asignarLimite(String nombre, Double limite, Usuario usuario) {
        Categoria c = categoriaRepository.findByNombreAndUsuario(nombre, usuario);
        if (c != null) {
            c.setLimite(limite);
            return categoriaRepository.save(c);
        }
        return null;
    }

    // Editar nombre
    public Categoria editarCategoria(String nombreActual, String nuevoNombre, Usuario usuario) {
        Categoria c = categoriaRepository.findByNombreAndUsuario(nombreActual, usuario);
        if (c != null) {
            c.setNombre(nuevoNombre);
            return categoriaRepository.save(c);
        }
        return null;
    }

    // Eliminar categoría
    public void eliminarCategoria(String nombre, Usuario usuario) {
        categoriaRepository.deleteByNombreAndUsuario(nombre, usuario);
    }
}
