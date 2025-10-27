package pe.edu.upeu.gestorfinanciero.service;

import pe.edu.upeu.gestorfinanciero.model.Participante;
import java.util.List;

public interface IParticipanteService {
    void save(Participante participante); //C

    List<Participante> findAll(); // R

    Participante update(Participante participante);//U

    void delete(String usuario); //D

    Participante findById(String usuario); //Buscar


}
