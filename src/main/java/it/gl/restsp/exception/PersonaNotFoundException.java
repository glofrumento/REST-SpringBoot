package it.gl.restsp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// reason, se presente, è prioritario rispetto al messaggio passato all'istanza della classe
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Persona non trovata")
public class PersonaNotFoundException extends Exception {

  private static final long serialVersionUID = 100L;
  private String message;

  public PersonaNotFoundException(String msg) {
    super(msg);
    this.message = msg;
    String messaggio = super.getMessage();
    System.out.println("Messaggio: " + messaggio);
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "PersonaNotFoundException [message=" + message + "]";
  }

}
