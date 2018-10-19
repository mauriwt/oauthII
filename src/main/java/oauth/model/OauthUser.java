package oauth.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

/**
 * The persistent class for the oauth_user database table.
 * 
 */
@Entity
@JsonInclude(Include.NON_NULL)
@Table(name = "oauth_user")
@NamedEntityGraph(name = "OauthUser.findUserPermisos", 
  attributeNodes = {@NamedAttributeNode("oauthPermisos")},
  includeAllAttributes=false)
public class OauthUser implements Serializable {
  private static final long serialVersionUID = 1L;
  public static final String LDAP_TYPE = "[PROTECTED]";

  @Id
  @Column(name = "user_id")
  @SequenceGenerator(name = "oauth_user_user_id_seq", sequenceName = "oauth_user_user_id_seq", allocationSize = 1, initialValue = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oauth_user_user_id_seq")
  private Integer userId;

  @Column(name = "enabled")
  private Boolean enabled;

  @NotEmpty(message = "El campo password es obligatorio.")
  @Length(min = 6, message = "El campo password al menos debe tener 6 caracteres.")
  @Column(name = "password")
  private String password;

  @Column(name = "username")
  @NotEmpty(message = "El campo username es obligatorio.")
  private String username;

  @NotNull(message = "El campo UUID es obligatorio.")
  @Column(name = "user_uuid")
  private Long userUUID;

  @NotEmpty(message = "El campo DNI es obligatorio.")
  @Column(name = "user_dni")
  private String userDNI;
  
  @NotEmpty(message = "El campo mail es obligatorio.")
  @Column(name = "user_mail")
  private String mail;

  // bi-directional many-to-one association to OauthRol
  @OneToMany(mappedBy = "oauthUser", cascade=CascadeType.ALL)
  @JsonIgnoreProperties(value = "oauthUser")
  private List<OauthPermiso> oauthPermisos;
  
  

  public OauthUser() {
  }
  
  public OauthUser(Integer userId, Long userUUID, String userDNI, String username, String mail, 
      String password, Boolean enabled){
    super();
    this.userId =  userId;
    this.username =  username;
    this.userDNI = userDNI;
    this.userUUID = userUUID;
    this.enabled = enabled;
    this.mail = mail;
    this.password = password.equals(LDAP_TYPE) ? "LDAP" : "PASSWORD";
  }
  
  public Integer getUserId() {
    return this.userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Boolean getEnabled() {
    return this.enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<OauthPermiso> getOauthPermisos() {
    return this.oauthPermisos;
  }

  public void setOauthPermisos(List<OauthPermiso> oauthPermisos) {
    this.oauthPermisos = oauthPermisos;
  }

  public Long getUserUUID() {
    return userUUID;
  }

  public void setUserUUID(Long userUUID) {
    this.userUUID = userUUID;
  }

  public String getUserDNI() {
    return userDNI;
  }

  public void setUserDNI(String userDNI) {
    this.userDNI = userDNI;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }
  
  

}