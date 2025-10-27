package pe.edu.upeu.gestorfinanciero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.gestorfinanciero.model.Participante;

public interface ParticipanteRepository extends JpaRepository<Participante, String> {
}
