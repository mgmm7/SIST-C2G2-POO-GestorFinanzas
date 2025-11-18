package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.model.Categoria;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.repository.CategoriaRepository;
import pe.edu.upeu.gestorfinanciero.repository.EgresoRepository;
import pe.edu.upeu.gestorfinanciero.repository.IngresoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final CategoriaRepository categoriaRepository;
    private final IngresoRepository ingresoRepository;
    private final EgresoRepository egresoRepository;

    // Crear nueva categoría
    public Categoria crearCategoria(String nombre, Usuario usuario) {
        Categoria existe = categoriaRepository.findByNombreAndUsuario(nombre, usuario);
        if (existe != null) return null;

        Categoria c = new Categoria(nombre, usuario);
        return categoriaRepository.save(c);
    }

    // Listar categorías del usuario
    public List<Categoria> listarCategoriasUsuario(Usuario usuario) {
        return categoriaRepository.findByUsuarioOrderByNombreAsc(usuario);
    }

    // Asignar presupuesto (SUMAR)
    public void asignarPresupuesto(String nombre, double monto, Usuario usuario) {
        Categoria c = categoriaRepository.findByNombreAndUsuario(nombre, usuario);
        if (c != null) {
            c.setPresupuesto(c.getPresupuesto() + monto);
            c.setSaldoDisponible(c.getSaldoDisponible() + monto);
            categoriaRepository.save(c);
        }
    }

    // Asignar o editar límite
    public void asignarLimite(String nombre, double limite, Usuario usuario) {
        Categoria c = categoriaRepository.findByNombreAndUsuario(nombre, usuario);
        if (c != null) {
            c.setLimite(limite);
            categoriaRepository.save(c);
        }
    }

    public void actualizarSaldoCategoria(String nombre, double nuevoSaldo, Usuario usuario) {
        Categoria c = categoriaRepository.findByNombreAndUsuario(nombre, usuario);
        if (c != null) {
            c.setSaldoDisponible(nuevoSaldo);
            categoriaRepository.save(c);
        }
    }

    // Obtener datos
    public double obtenerSaldoCategoria(String nombre, Usuario usuario) {
        Categoria c = categoriaRepository.findByNombreAndUsuario(nombre, usuario);
        return c == null ? 0 : c.getSaldoDisponible();
    }

    public double obtenerLimiteCategoria(String nombre, Usuario usuario) {
        Categoria c = categoriaRepository.findByNombreAndUsuario(nombre, usuario);
        return c == null ? 0 : c.getLimite();
    }

    // Editar categoría
    public void editarCategoria(String antiguo, String nuevo, Usuario usuario) {
        Categoria c = categoriaRepository.findByNombreAndUsuario(antiguo, usuario);
        if (c != null) {
            c.setNombre(nuevo);
            categoriaRepository.save(c);
        }
    }

    // Eliminar categoría
    public void eliminarCategoria(String nombre, Usuario usuario) {
        categoriaRepository.deleteByNombreAndUsuario(nombre, usuario);
    }
}
