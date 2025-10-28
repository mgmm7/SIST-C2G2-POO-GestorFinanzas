package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.model.Ingreso;
import pe.edu.upeu.gestorfinanciero.repository.IngresoRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class IngresoService {

    private final IngresoRepository ingresoRepository;

    public void guardarIngreso(Ingreso ingreso) {
        ingresoRepository.save(ingreso);
    }

    public List<Ingreso> listarIngresos() {
        return ingresoRepository.findAll();
    }

    public void eliminarIngreso(Ingreso ingreso) {
        ingresoRepository.delete(ingreso);
    }

    public double totalIngresos() {
        return ingresoRepository.findAll()
                .stream().mapToDouble(Ingreso::getMonto)
                .sum();
    }
}
