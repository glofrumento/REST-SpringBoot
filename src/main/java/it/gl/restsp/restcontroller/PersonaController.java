package it.gl.restsp.restcontroller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
  // curl localhost:8080/rest/v1/persone/100 | jq
  //
  @GetMapping("/persone/{personaId}")
  public ResponseEntity<Persona> findPersona(@PathVariable Long personaId) throws PersonaNotFoundException {

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
  // curl -X POST -H "Content-Type: application/json" -d "{\"cognome\":\"Gialli\", \"nome\":\"Antonio\", \"eta\":35, \"email\":\"antonio.gialli@gmail.com\"}" localhost:8080/rest/v1/persone | jq
  // curl -X POST -H "Content-Type: application/json" -d "{\"cognome\":\"Gialli\", \"nome\":\"Ulisse\", \"eta\":35, \"email\":\"antonio.gialli@gmail.com\"}" localhost:8080/rest/v1/persone -i
  // curl --% -X POST -H "Content-Type: application/json" -H "Accept: application/xml" -d "{\"cognome\":\"Gialli\", \"nome\":\"Antonio\", \"eta\":35, \"email\":\"antonio.gialli@gmail.com\"}" localhost:8080/rest/v1/persone | xmllint --format -
  //
  // Powershell: curl --% -X POST -H "Content-Type: application/json" -d "{\"cognome\":\"Gialli\", \"nome\":\"Antonio\", \"eta\":35, \"email\":\"antonio.gialli@gmail.com\"}" localhost:8080/rest/v1/persone | jq
  //
  // curl -X POST -H "Content-Type: application/json" -H "Accept: application/xml" -d "{\"id\":200,\"cognome\":\"Gialli\", \"nome\":\"Antonio\", \"eta\":35, \"email\":\"antonio.gialli@gmail.com\"}" localhost:8080/rest/v1/persone | xmllint --format -
  // curl -X POST -H "Content-Type: application/xml" -H "Accept: application/xml" -d "<Persona><cognome>Verdi</cognome><nome>Achille</nome><eta>35</eta><email>achille.verdi@gmail.com</email></Persona>" localhost:8080/rest/v1/persone | xmllint --format -
  // curl -X POST -H "Content-Type: application/xml" -H "Accept: application/json" -d "<Persona><cognome>Verdi</cognome><nome>Achille</nome><eta>35</eta><email>achille.verdi@gmail.com</email></Persona>" localhost:8080/rest/v1/persone | jq
  // curl -X POST -H "Content-Type: application/xml" -d "<Persona><cognome>Verdi</cognome><nome>Achille</nome><eta>35</eta><email>achille.verdi@gmail.com</email></Persona>" localhost:8080/rest/v1/persone | jq
  //
  @PostMapping(value = "/persone", consumes = { MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
          MediaType.APPLICATION_XML_VALUE })
  @ResponseStatus(value = HttpStatus.CREATED)
//1  public Persona createPersona(@Valid @RequestBody Persona persona) throws PersonaNotCreatedException {
  public ResponseEntity<?> createPersona(@Valid @RequestBody Persona persona) throws PersonaNotCreatedException {

    // Genera l'eccezione se il nome è Ulisse
    if (persona.getNome().equals("Ulisse"))
      throw new PersonaNotCreatedException("Persona non creata");

    Persona newPersona = personaService.save(persona);

//1    if (newPersona != null)
//1      return newPersona;
//1    else
//1      throw new PersonaNotCreatedException("Persona non creata");
    
    if (newPersona != null) {
      URI location = ServletUriComponentsBuilder
                       .fromCurrentRequest()
                       .path("/{id}")
                       .buildAndExpand(newPersona.getId())
                       .toUri();
      return ResponseEntity.created(location).build();
      // return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, location.toString()).build();
    } else {
      throw new PersonaNotCreatedException("Persona non creata");
    }
  }

  //
  // curl -X PUT -H "Content-Type: application/json" -d "{\"cognome\":\"Giallo\", \"nome\":\"Antonino\", \"eta\":45, \"email\":\"antonino.giallo@gmail.com\"}" localhost:8080/rest/v1/persone/1 | jq
  // curl --% -X PUT -H "Content-Type: application/json" -H "Accept: application/xml" -d "{\"cognome\":\"Gialli\", \"nome\":\"Antonio\", \"eta\":45, \"email\":\"antonio.gialli@gmail.com\"}" localhost:8080/rest/v1/persone/1 | xmllint --format -
  //
  // Powershell --%: curl --% -X PUT -H "Content-Type: application/json" -d "{\"cognome\":\"Gialli\", \"nome\":\"Antonio\", \"eta\":45, \"email\":\"antonio.gialli@gmail.com\"}" localhost:8080/rest/v1/persone/1 | jq
  //
  // curl -X PUT -H "Content-Type: application/json" -H "Accept: application/xml" -d "{\"id\":200,\"cognome\":\"Gialli\", \"nome\":\"Antonio\", \"eta\":35, \"email\":\"antonio.gialli@gmail.com\"}" localhost:8080/rest/v1/persone/1 | xmllint --format -
  // curl -X PUT -H "Content-Type: application/xml" -H "Accept: application/xml" -d "<Persona><cognome>Verdi</cognome><nome>Achille</nome><eta>45</eta><email>achille.verdi@gmail.com</email></Persona>" localhost:8080/rest/v1/persone/1 | xmllint --format -
  // curl -X PUT -H "Content-Type: application/xml" -H "Accept: application/json" -d "<Persona><cognome>Verdi</cognome><nome>Achille</nome><eta>45</eta><email>achille.verdi@gmail.com</email></Persona>" localhost:8080/rest/v1/persone/1 | jq
  // curl -X PUT -H "Content-Type: application/xml" -d "<Persona><cognome>Verdi</cognome><nome>Achille</nome><eta>45</eta><email>achille.verdi@gmail.com</email></Persona>" localhost:8080/rest/v1/persone/1 | jq
  //
  @PutMapping(value = "/persone/{personaId}", consumes = { MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
          MediaType.APPLICATION_XML_VALUE })
  @ResponseStatus(value = HttpStatus.CREATED)
  public ResponseEntity<Persona> replacePersona(@PathVariable Long personaId, @Valid @RequestBody Persona persona)
      throws PersonaNotFoundException {

    persona.setId(personaId);
    Persona updatePersona = personaService.replace(persona);

    if (updatePersona != null)
      return new ResponseEntity<>(updatePersona, HttpStatus.OK);
    else {
      System.out.println("§§§§§§§§§§§§ ECCEZIONE");
      throw new PersonaNotFoundException("Persona non trovata con id: " + personaId);
    }
  }

  // 
  // curl -X PATCH localhost:8080/rest/v1/persone/100?email=a.gialli@gmail.com | jq
  //
  @PatchMapping("/persone/{id}")
  public ResponseEntity<Persona> changeEmail(@PathVariable("id") Long userId, @RequestParam("email") String email) {

    Persona persona = personaService.findById(userId);
    persona.setEmail(email);
    personaService.save(persona);
    return new ResponseEntity<>(persona, HttpStatus.OK);
  }

  // Request Header
  // curl -H "AuthToken: 12345" localhost:8080/rest/v1/persone/header
  //
  @GetMapping("/persone/header")
  public ResponseEntity<String> getHeader(@RequestHeader("User-Agent") String userAgent,
      @RequestHeader("AuthToken") String token) {

    String response = new String("User agent: " + userAgent + "   " + "AuthToken: " + token);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  // curl -X DELETE localhost:8080/rest/v1/persone/102 | jq
  //
  @DeleteMapping("/persone/{personaId}")
  public ResponseEntity<Persona> deletePersona(@PathVariable Long personaId) throws PersonaNotFoundException {

    Persona persona = personaService.findById(personaId);
    if (persona != null) {
      personaService.deletePersona(personaId);
      return new ResponseEntity<>(persona, HttpStatus.OK);
    } else {
      throw new PersonaNotFoundException("Persona non trovata con id: " + personaId);
    }
  }

}
