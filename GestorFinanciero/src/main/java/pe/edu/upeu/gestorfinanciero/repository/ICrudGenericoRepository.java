package pe.edu.upeu.gestorfinanciero.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ICrudGenericoRepository <T,ID> extends JpaRepository<T,ID> {
}
