package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.model.Categoria;
import pe.edu.upeu.gestorfinanciero.repository.CategoriaRepository;
import pe.edu.upeu.gestorfinanciero.repository.EgresoRepository;
import pe.edu.upeu.gestorfinanciero.repository.IngresoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final CategoriaRepository categoriaRepository;
    private final IngresoRepository ingresoRepository;
    private final EgresoRepository egresoRepository;

    // Crear nueva categoría
    public void crearCategoria(String nombre) {
        Categoria nueva = new Categoria();
        nueva.setNombre(nombre);
        nueva.setPresupuesto(0);
        nueva.setLimite(0);
        nueva.setSaldoDisponible(0);
        categoriaRepository.save(nueva);
    }

    // Listar todas las categorías
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    // Obtener solo los nombres
    public List<String> obtenerCategorias() {
        return listarCategorias().stream()
                .map(Categoria::getNombre)
                .collect(Collectors.toList());
    }

    private Optional<Categoria> buscarPorNombre(String nombre) {
        return Optional.ofNullable(categoriaRepository.findByNombre(nombre));
    }

    public void asignarPresupuesto(String nombre, double monto) {
        buscarPorNombre(nombre).ifPresent(c -> {
            c.setPresupuesto(c.getPresupuesto() + monto);
            c.setSaldoDisponible(c.getSaldoDisponible() + monto);
            categoriaRepository.save(c);
        });
    }

    public void asignarLimite(String nombre, double nuevoLimite) {
        buscarPorNombre(nombre).ifPresent(c -> {
            c.setLimite(nuevoLimite);
            categoriaRepository.save(c);
        });
    }

    public void actualizarSaldoCategoria(String nombre, double nuevoSaldo) {
        buscarPorNombre(nombre).ifPresent(c -> {
            c.setSaldoDisponible(nuevoSaldo);
            categoriaRepository.save(c);
        });
    }

    public double obtenerSaldoCategoria(String nombre) {
        return buscarPorNombre(nombre)
                .map(Categoria::getSaldoDisponible)
                .orElse(0.0);
    }

    public double obtenerLimiteCategoria(String nombre) {
        return buscarPorNombre(nombre)
                .map(Categoria::getLimite)
                .orElse(0.0);
    }

    public void editarCategoria(String nombreActual, String nuevoNombre, double nuevoPresupuesto) {
        buscarPorNombre(nombreActual).ifPresent(c -> {
            c.setNombre(nuevoNombre);
            c.setPresupuesto(nuevoPresupuesto);
            categoriaRepository.save(c);
        });
    }

    public void eliminarCategoria(String nombre) {
        categoriaRepository.deleteByNombre(nombre);
    }

    // 🔹 Calcular saldo global del sistema
    public double obtenerSaldoActual() {
        Double totalIngresos = ingresoRepository.sumTotal();
        Double totalEgresos = egresoRepository.sumTotal();
        if (totalIngresos == null) totalIngresos = 0.0;
        if (totalEgresos == null) totalEgresos = 0.0;
        return totalIngresos - totalEgresos;
    }
}
