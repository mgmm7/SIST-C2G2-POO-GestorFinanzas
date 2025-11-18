package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.model.Egreso;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.repository.EgresoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EgresoService {

    private final EgresoRepository egresoRepository;

    public void guardar(Egreso e) {
        egresoRepository.save(e);
    }

    public List<Egreso> listar(Usuario u) {
        return egresoRepository.findByUsuarioOrderByIdDesc(u);
    }

    public void eliminar(Egreso e) {
        egresoRepository.delete(e);
    }

    public double total(Usuario u) {
        return listar(u).stream().mapToDouble(Egreso::getMonto).sum();
    }
    public List<Egreso> listarPorCategoria(Usuario usuario, String categoria) {
        return egresoRepository.findByUsuarioOrderByIdDesc(usuario)
                .stream()
                .filter(e -> e.getCategoria().equals(categoria))
                .toList();
    }

}
