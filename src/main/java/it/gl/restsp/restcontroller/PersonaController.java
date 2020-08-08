package it.gl.restsp.restcontroller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.gl.restsp.exception.PersonaNotCreatedException;
import it.gl.restsp.exception.PersonaNotFoundException;
import it.gl.restsp.model.Persona;
import it.gl.restsp.service.IPersonaService;

@RestController
@RequestMapping("/v1")
public class PersonaController {

  @Autowired
  IPersonaService personaService;
  
  // curl localhost:8080/rest/v1/persone | jq
  //
  @GetMapping("/persone")
  public ResponseEntity<List<Persona>> findPersone() {

    List<Persona> persone = personaService.findAll();
    return new ResponseEntity<>(persone, HttpStatus.OK);
  }

  /*
   * @PathVariable
   */
  // curl localhost:8080/rest/v1/persona/100 | jq
  //
  @GetMapping("/persona/{personaId}")
  public ResponseEntity<Persona> findPersona(@PathVariable Long personaId) throws  PersonaNotFoundException {

    Persona persona = personaService.findById(personaId);
    if (persona != null)
      return new ResponseEntity<>(persona, HttpStatus.OK);
    else {
      System.out.println("############ ECCEZIONE");
      throw new PersonaNotFoundException("Persona non trovata con id: " + personaId);
    }
  }

  /*
   * @RequestParam
   */

  // Parametro singolo con valore di default
  // curl localhost:8080/rest/v1/persona?personaId=101 | jq
  // curl localhost:8080/rest/v1/persona | jq
  //
  @GetMapping("/persona")
  public ResponseEntity<Persona> findPersona1(@RequestParam(value = "personaId", defaultValue = "100") Long userId) {

    Persona persona = personaService.findById(userId);
    return new ResponseEntity<>(persona, HttpStatus.OK);
  }

  // parametri multipli senza nome
  // curl localhost:8080/rest/v1/personeidmultipli?ids="1,10,100,102" | jq
  //
  @GetMapping("/personeidmultipli")
  public ResponseEntity<List<Persona>> findPersoneInList(@RequestParam List<Long> ids) {

    System.out.println("ids: " + ids);

    List<Persona> persone = new ArrayList<>();

    for (Long id : ids) {
      Persona persona = personaService.findById(id);
      if (persona != null) {
        persone.add(persona);
      }
    }

    return new ResponseEntity<>(persone, HttpStatus.OK);
  }

  // curl localhost:8080/rest/v1/personein?da=100^&a=102 | jq
  // Powershell: curl 'localhost:8080/rest/v1/personein?da=100&a=102' | jq
  //
  @GetMapping(path = "/personein")
  public ResponseEntity<List<Persona>> findPersoneInRange(@RequestParam("da") Long from,
      @RequestParam(value = "a", required = false) Long to) {

    System.out.println("da: " + from + "   " + "a: " + to);

    List<Persona> persone = personaService.findAll();
    List<Persona> personeInRange = new ArrayList<>();

    if (to != null) {
      persone.stream().forEach(p -> {
        if ((p.getId() >= from && p.getId() <= to)) {
          personeInRange.add(p);
        }
      });
    } else {
      persone.stream().forEach(p -> {
        if (p.getId() >= from) {
          personeInRange.add(p);
        }
      });
    }

    return new ResponseEntity<>(personeInRange, HttpStatus.OK);
  }
  
  
  
  // 
  // curl -X POST -H "Content-Type: application/json" -d "{\"id\":200,\"cognome\":\"Gialli\", \"nome\":\"Antonio\", \"eta\":35, \"email\":\"antonio.gialli@gmail.com\"}" localhost:8080/rest/v1/persona | jq
  // curl -X POST -H "Content-Type: application/json" -d "{\"id\":200,\"cognome\":\"Gialli\", \"nome\":\"Ulisse\", \"eta\":35, \"email\":\"antonio.gialli@gmail.com\"}" localhost:8080/rest/v1/persona -i
  // curl --% -X POST -H "Content-Type: application/json" -H "Accept: application/xml" -d "{\"id\":200,\"cognome\":\"Gialli\", \"nome\":\"Antonio\", \"eta\":35, \"email\":\"antonio.gialli@gmail.com\"}" localhost:8080/rest/v1/persona | xmllint --format -
  //
  // Powershell: curl --% -X POST -H "Content-Type: application/json" -d "{\"id\":200,\"cognome\":\"Gialli\", \"nome\":\"Antonio\", \"eta\":35, \"email\":\"antonio.gialli@gmail.com\"}" localhost:8080/rest/v1/persona | jq
  //
  // curl -X POST -H "Content-Type: application/json" -H "Accept: application/xml" -d "{\"id\":200,\"cognome\":\"Gialli\", \"nome\":\"Antonio\", \"eta\":35, \"email\":\"antonio.gialli@gmail.com\"}" localhost:8080/rest/v1/persona | xmllint --format -
  // curl -X POST -H "Content-Type: application/xml" -H "Accept: application/xml" -d "<Persona><id>200</id><cognome>Verdi</cognome><nome>Achille</nome><eta>45</eta><email>achille.verdi@gmail.com</email></Persona>" localhost:8080/rest/v1/persona | xmllint --format -
  // curl -X POST -H "Content-Type: application/xml" -H "Accept: application/json" -d "<Persona><id>200</id><cognome>Verdi</cognome><nome>Achille</nome><eta>45</eta><email>achille.verdi@gmail.com</email></Persona>" localhost:8080/rest/v1/persona | jq
  // curl -X POST -H "Content-Type: application/xml" -d "<Persona><id>200</id><cognome>Verdi</cognome><nome>Achille</nome><eta>45</eta><email>achille.verdi@gmail.com</email></Persona>" localhost:8080/rest/v1/persona | jq
  //
  @PostMapping(value = "/persona",
              consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
              produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  @ResponseStatus(value = HttpStatus.CREATED)
  public Persona createPersona(@RequestBody Persona persona) throws PersonaNotCreatedException {
    
    Persona newPersona = personaService.save(persona);

    // Genera l'eccezione se il nome è Ulisse
    if (newPersona.getNome().equals("Ulisse"))
        newPersona = null;
    
    if (newPersona != null)
      return newPersona;
    else
      throw new PersonaNotCreatedException("Persona non creata");
  }  
  
  // Validazione email
  // curl -X PATCH localhost:8080/rest/v1/persona/100?email=a.gialli@gmail.com | jq
  //
  @PatchMapping("/persona/{id}")
  public ResponseEntity<Persona> changeEmail(
      @PathVariable("id") Long userId,
      @RequestParam("email") String email) {
    
    Persona persona = personaService.findById(userId);
    persona.setEmail(email);
    personaService.save(persona);
    return new ResponseEntity<>(persona, HttpStatus.OK);
  }
  
  // Request Header
  // curl -H "AuthToken: 12345" localhost:8080/rest/v1/persona/header
  //
  @GetMapping("/persona/header")
  public ResponseEntity<String> getHeader(
      @RequestHeader("User-Agent") String userAgent,
      @RequestHeader("AuthToken") String token) {
    
    String response = new String("User agent: " + userAgent + "   " + "AuthToken: " + token);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
  
}
