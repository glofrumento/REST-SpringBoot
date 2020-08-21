package it.gl.restsp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
//import javax.xml.bind.annotation.XmlAccessType;
//import javax.xml.bind.annotation.XmlAccessorType;
//import javax.xml.bind.annotation.XmlAttribute;
//import javax.xml.bind.annotation.XmlRootElement;

@Entity
@JacksonXmlRootElement(localName = "persona")
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "PERSONE")
public class Persona {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JacksonXmlProperty(isAttribute = true)
  private Long id;

  @JacksonXmlProperty
  @NotNull(message = "Il cognome non puà essere vuoto o mancante")
  @Size(min = 2, message = "Il cognome deve essere di almeno 2 caratteri")
  private String cognome;

  @JacksonXmlProperty
  private String nome;

  @JacksonXmlProperty
  private int eta;

  @JacksonXmlProperty
  @Email
  private String email;

  public Persona() {
    super();
  }

  public Persona(Long id, String cognome, String nome, int eta, String email) {
    super();
    this.id = id;
    this.cognome = cognome;
    this.nome = nome;
    this.eta = eta;
    this.email = email;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCognome() {
    return cognome;
  }

  public void setCognome(String cognome) {
    this.cognome = cognome;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public int getEta() {
    return eta;
  }

  public void setEta(int eta) {
    this.eta = eta;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cognome == null) ? 0 : cognome.hashCode());
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + eta;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((nome == null) ? 0 : nome.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Persona other = (Persona) obj;
    if (cognome == null) {
      if (other.cognome != null)
        return false;
    } else if (!cognome.equals(other.cognome))
      return false;
    if (email == null) {
      if (other.email != null)
        return false;
    } else if (!email.equals(other.email))
      return false;
    if (eta != other.eta)
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (nome == null) {
      if (other.nome != null)
        return false;
    } else if (!nome.equals(other.nome))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Persona [id=" + id + ", cognome=" + cognome + ", nome=" + nome + ", eta=" + eta + ", email=" + email + "]";
  }

}
