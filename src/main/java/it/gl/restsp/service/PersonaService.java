package it.gl.restsp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.gl.restsp.model.Persona;
import it.gl.restsp.repository.IPersonaRepository;

@Service
public class PersonaService implements IPersonaService {

  @Autowired
  IPersonaRepository repository;
  
  @Override
  public List<Persona> findAll() {
    List<Persona> persone = (List<Persona>) repository.findAll();
    return persone;
  }

  @Override
  public Persona findById(Long id) {
    Persona persona = repository.findById(id).orElse(null);
    //Optional<Persona> persona = Optional.of(repository.findById(id).orElse(null));
    return persona;
  }

  @Override
  public Persona save(Persona persona) {
    repository.save(persona);
    return persona;
  }

}
