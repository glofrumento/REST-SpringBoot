package it.gl.restsp.repository;

import org.springframework.data.repository.CrudRepository;

import it.gl.restsp.model.Persona;

public interface IPersonaRepository extends CrudRepository<Persona, Long> {

}
