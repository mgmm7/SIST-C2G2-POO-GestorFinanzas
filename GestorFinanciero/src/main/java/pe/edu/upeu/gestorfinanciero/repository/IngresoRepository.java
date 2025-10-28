package pe.edu.upeu.gestorfinanciero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.gestorfinanciero.model.Ingreso;
@Repository
public interface IngresoRepository extends JpaRepository<Ingreso, Long> {
    @Query("SELECT SUM(i.monto) FROM Ingreso i")
    Double sumTotal();
}