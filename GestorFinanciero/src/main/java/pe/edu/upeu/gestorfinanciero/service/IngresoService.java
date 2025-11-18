package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.model.Ingreso;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.repository.IngresoRepository;

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
}
