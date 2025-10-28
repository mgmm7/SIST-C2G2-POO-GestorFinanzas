package pe.edu.upeu.gestorfinanciero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.model.Egreso;
import pe.edu.upeu.gestorfinanciero.repository.EgresoRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EgresoService {

    private final EgresoRepository egresoRepository;

    public void guardarEgreso(Egreso egreso) {
        egresoRepository.save(egreso);
    }

    public List<Egreso> listarEgresos() {
        return egresoRepository.findAll();
    }

    public void eliminarEgreso(Egreso egreso) {
        egresoRepository.delete(egreso);
    }

    public double totalEgresos() {
        return egresoRepository.findAll()
                .stream().mapToDouble(Egreso::getMonto)
                .sum();
    }
}