package it.gl.restsp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class PersonaNotCreatedException extends Exception {

  private static final long serialVersionUID = 110L;

  public PersonaNotCreatedException(String msg) {
    super(msg);
  }

}
