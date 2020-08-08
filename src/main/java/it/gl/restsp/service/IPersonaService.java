package it.gl.restsp.service;

import java.util.List;

import it.gl.restsp.model.Persona;

public interface IPersonaService {

  public List<Persona> findAll();
  public Persona findById(Long id);
  public Persona save(Persona persona);
}
