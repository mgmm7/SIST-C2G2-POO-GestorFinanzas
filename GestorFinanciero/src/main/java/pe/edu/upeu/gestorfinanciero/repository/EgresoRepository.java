package pe.edu.upeu.gestorfinanciero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.gestorfinanciero.model.Egreso;

@Repository
public interface EgresoRepository extends JpaRepository<Egreso, Long> {
    @Query("SELECT SUM(e.monto) FROM Egreso e")
    Double sumTotal();
}