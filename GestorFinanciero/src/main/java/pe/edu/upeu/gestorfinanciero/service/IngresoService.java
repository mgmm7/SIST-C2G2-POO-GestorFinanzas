package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.gestorfinanciero.model.Ingreso;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.repository.IngresoRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IngresoService {

    private final IngresoRepository ingresoRepository;

    public void guardar(Ingreso ingreso) {
        ingresoRepository.save(ingreso);
    }

    public List<Ingreso> listar(Usuario usuario) {
        return ingresoRepository.findByUsuarioOrderByIdDesc(usuario);
    }

    public void eliminar(Ingreso ingreso) {
        ingresoRepository.delete(ingreso);
    }

    public double total(Usuario usuario) {
        return listar(usuario)
                .stream()
                .mapToDouble(Ingreso::getMonto)
                .sum();
    }

    // firma: (monto, descripcion, usuario)
    @Transactional
    public Ingreso descontar(double monto, String descripcion, Usuario usuario) {
        // monto > 0 es la cantidad a restar; se registrará como negativo
        double totalPrev = listar(usuario).stream().mapToDouble(Ingreso::getMonto).sum();
        double montoNeg = -Math.abs(monto);
        double nuevoSaldo = totalPrev + montoNeg;

        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Ingreso ingresoNeg = new Ingreso(fecha, descripcion, montoNeg, nuevoSaldo, usuario);
        ingresoNeg.setTipo("Descuento"); // opcional
        return ingresoRepository.save(ingresoNeg);
    }

    // -----------------------------
    // eliminar ingresos que contengan una parte de descripción para un usuario
    // útil para eliminar los ingresos negativos generados por aportes
    // -----------------------------
    @Transactional
    public void eliminarIngresosPorDescripcionYUsuario(String descripcionParte, Usuario usuario) {
        List<Ingreso> encontrados = ingresoRepository.findByUsuarioAndDescripcionContaining(usuario, descripcionParte);
        if (encontrados != null && !encontrados.isEmpty()) {
            ingresoRepository.deleteAll(encontrados);
        }
    }
}
