package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.gestorfinanciero.model.Categoria;
import pe.edu.upeu.gestorfinanciero.model.Ingreso;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.repository.CategoriaRepository;
import pe.edu.upeu.gestorfinanciero.repository.EgresoRepository;
import pe.edu.upeu.gestorfinanciero.repository.IngresoRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    // Asignar presupuesto (SUMA presupuesto + SUMA saldo)
    // y CREA INGRESO NEGATIVO para restar del total general
    @Transactional
    public void asignarPresupuesto(String nombre, double monto, Usuario usuario) {

        // 1. Buscar la categoría
        Categoria c = categoriaRepository.findByNombreAndUsuario(nombre, usuario);
        if (c == null) return;

        // 2. Registrar movimiento como un ingreso negativo
        double saldoAnterior = ingresoRepository.findByUsuarioOrderByIdDesc(usuario)
                .stream()
                .mapToDouble(Ingreso::getMonto)
                .sum();

        double nuevoSaldo = saldoAnterior - monto;

        Ingreso mov = new Ingreso(
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                "Asignación a categoría: " + nombre,
                -monto,
                nuevoSaldo,
                usuario
        );
        mov.setTipo("Asignación");
        ingresoRepository.save(mov);

        // 3. Actualizar valores dentro de la categoría
        c.setPresupuesto(c.getPresupuesto() + monto);
        c.setSaldoDisponible(c.getSaldoDisponible() + monto);
        categoriaRepository.save(c);
    }


    // Asignar o editar límite
    @Transactional
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
    @Transactional
    public void editarCategoria(String antiguo, String nuevo, Usuario usuario) {
        Categoria c = categoriaRepository.findByNombreAndUsuario(antiguo, usuario);
        if (c != null) {
            c.setNombre(nuevo);
            categoriaRepository.save(c);
        }
    }

    @Transactional
    public void eliminarCategoria(String nombre, Usuario usuario) {

        // 1) Eliminar ingresos negativos asociados
        List<Ingreso> ingresosRelacionados =
                ingresoRepository.findByUsuarioAndDescripcionContaining(usuario, "Asignación a categoría: " + nombre);

        if (ingresosRelacionados != null && !ingresosRelacionados.isEmpty()) {
            ingresoRepository.deleteAll(ingresosRelacionados);
        }

        // 2) Eliminar categoría
        categoriaRepository.deleteByNombreAndUsuario(nombre, usuario);
    }


}
