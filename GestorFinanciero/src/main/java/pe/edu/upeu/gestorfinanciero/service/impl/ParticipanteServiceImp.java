package pe.edu.upeu.gestorfinanciero.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upeu.gestorfinanciero.model.Participante;
import pe.edu.upeu.gestorfinanciero.repository.ParticipanteRepository;
import pe.edu.upeu.gestorfinanciero.service.IParticipanteService;

import java.util.List;
@Service
public class ParticipanteServiceImp implements IParticipanteService {

    @Autowired
    ParticipanteRepository participanteRepository;

    @Override
    public void save(Participante participante) {
        participanteRepository.save(participante);
    }

    @Override
    public List<Participante> findAll() {
        return participanteRepository.findAll();
    }

    @Override
    public Participante update(Participante participante) {
        return participanteRepository.save(participante);
    }

    @Override
    public void delete(String usuario) {
        participanteRepository.deleteById(usuario);
    }

    @Override
    public Participante findById(String usuario) {
        return participanteRepository.findById(usuario).orElseThrow();

    }
}
