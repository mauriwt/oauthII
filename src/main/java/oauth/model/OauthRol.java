package oauth.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The persistent class for the oauth_permiso database table.
 * 
 */
@Entity
@Table(name = "oauth_rol")
@NamedQuery(name = "OauthRol.findAll", query = "SELECT o FROM OauthRol o")
@NamedEntityGraph(name = "OauthRol.findRolScopes", attributeNodes = {
    @NamedAttributeNode("oauthClientScope") }, includeAllAttributes = false)
public class OauthRol implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "rol_id")
  @SequenceGenerator(name = "oauth_rol_rol_id_seq", sequenceName = "oauth_rol_rol_id_seq", allocationSize = 1, initialValue = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oauth_rol_rol_id_seq")
  private Integer rolId;

  @Column(name = "rol_name")
  private String name;

  @Column(name = "rol_desc")
  private String desc;
  
  @Column(name = "rol_enabled")
  private boolean enabled;

  // bi-directional many-to-many association to OauthClientResource
  @ManyToMany
  @JoinTable(name = "oauth_rol_has_scopes", 
  joinColumns = { @JoinColumn(name = "rol_id") }, 
    inverseJoinColumns = {
      @JoinColumn(name = "scope_id") })
  @JsonIgnoreProperties(value = { "oauthClientResource", "oauthClientDetail" }, allowSetters = false)
  private List<OauthClientScope> oauthClientScope;

  public OauthRol() {
  }

  public OauthRol(Integer rolId, String name, String desc, boolean enabled) {
    super();
    this.rolId = rolId;
    this.name = name;
    this.desc = desc;
    this.enabled = enabled;
  }

  public Integer getRolId() {
    return rolId;
  }

  public void setRolId(Integer rolId) {
    this.rolId = rolId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public List<OauthClientScope> getOauthClientScope() {
    return oauthClientScope;
  }

  public void setOauthClientScope(List<OauthClientScope> oauthClientScope) {
    this.oauthClientScope = oauthClientScope;
  }

}