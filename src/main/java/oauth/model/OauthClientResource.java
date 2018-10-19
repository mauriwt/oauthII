package oauth.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The persistent class for the oauth_client_resource database table.
 * 
 */
@Entity
@Table(name = "oauth_client_resource")
@NamedQuery(name = "OauthClientResource.findAll", query = "SELECT o FROM OauthClientResource o")
@NamedEntityGraph(name = "OauthClientResource.findClientScopes", attributeNodes = {
    @NamedAttributeNode("oauthClientDetail"), @NamedAttributeNode("oauthClientScopes") }, includeAllAttributes = false)
public class OauthClientResource implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "resource_id")
  @SequenceGenerator(name = "oauth_client_scope_scope_id_seq", sequenceName = "oauth_client_scope_scope_id_seq", allocationSize = 1, initialValue = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oauth_client_scope_scope_id_seq")
  private Integer resourceId;

  @Column(name = "resource")
  private String resource;

  // bi-directional many-to-one association to OauthClientDetail
  @ManyToOne
  @JoinColumn(name = "client_id")
  @JsonIgnoreProperties(value = { "oauthClientScopes", "oauthClientResources" }, allowSetters = true)
  private OauthClientDetail oauthClientDetail;

  // bi-directional many-to-many association to oauthClientResource
  @OneToMany(mappedBy = "oauthClientResource", cascade = CascadeType.ALL)
  @JsonIgnoreProperties(value = { "oauthClientResource", "oauthClientDetail" }, allowSetters = true)
  private List<OauthClientScope> oauthClientScopes;

  public OauthClientResource() {
  }

  public OauthClientResource(Integer resourceId, String resource) {
    super();
    this.resourceId = resourceId;
    this.resource = resource;
  }

  public Integer getResourceId() {
    return this.resourceId;
  }

  public void setResourceId(Integer resourceId) {
    this.resourceId = resourceId;
  }

  public String getResource() {
    return this.resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public OauthClientDetail getOauthClientDetail() {
    return this.oauthClientDetail;
  }

  public void setOauthClientDetail(OauthClientDetail oauthClientDetail) {
    this.oauthClientDetail = oauthClientDetail;
  }

  public List<OauthClientScope> getOauthClientScopes() {
    return this.oauthClientScopes;
  }

  public void setOauthClientScopes(List<OauthClientScope> oauthClientScopes) {
    this.oauthClientScopes = oauthClientScopes;
  }

  @Override
  public boolean equals(Object o){
    if(o == null || this.getClass() != o.getClass()) return false;
    OauthClientResource detail = (OauthClientResource) o;
    return detail.getResourceId().equals(this.resourceId);
  }
  
  @Override
  public int hashCode(){
    return this.resourceId.hashCode();
  }

}