package oauth.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The persistent class for the oauth_client_scope database table.
 * 
 */
@Entity
@Table(name = "oauth_client_scope")
@NamedQuery(name = "OauthClientScope.findAll", query = "SELECT o FROM OauthClientScope o")
@NamedEntityGraph(name = "OauthClientScope.findClientResourse", 
attributeNodes = {@NamedAttributeNode("oauthClientDetail"),@NamedAttributeNode("oauthClientResource")},
includeAllAttributes=false)
public class OauthClientScope implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "scope_id")
  @SequenceGenerator(name="oauth_client_resource_resource_id_seq", sequenceName="oauth_client_resource_resource_id_seq", allocationSize=1, initialValue=1)
  @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="oauth_client_resource_resource_id_seq")
  private Integer scopeId;

  @Column(name = "scope", unique=true)
  private String scope;

  // bi-directional many-to-one association to oauthClientResource
  @ManyToOne
  @JoinColumn(name = "resource_id")
  @JsonIgnoreProperties(value={"oauthClientDetail", "oauthClientScopes"}, allowSetters=true)
  private OauthClientResource oauthClientResource;

  // bi-directional many-to-one association to OauthClientDetail
  @ManyToOne
  @JoinColumn(name = "client_id")
  @JsonIgnoreProperties({"oauthClientScopes", "oauthClientResources"})
  private OauthClientDetail oauthClientDetail;

  public OauthClientScope() {
  }

  public OauthClientScope(Integer scopeId, String scope) {
    super();
    this.scopeId = scopeId;
    this.scope = scope;
  }

  public Integer getScopeId() {
    return this.scopeId;
  }

  public void setScopeId(Integer scopeId) {
    this.scopeId = scopeId;
  }

  public String getScope() {
    return this.scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public OauthClientDetail getOauthClientDetail() {
    return this.oauthClientDetail;
  }

  public void setOauthClientDetail(OauthClientDetail oauthClientDetail) {
    this.oauthClientDetail = oauthClientDetail;
  }

  public OauthClientResource getOauthClientResource() {
    return this.oauthClientResource;
  }

  public void setOauthClientResource(OauthClientResource oauthClientResource) {
    this.oauthClientResource = oauthClientResource;
  }
  
  
  @Override
  public boolean equals(Object o){
    if(o == null || this.getClass() != o.getClass()) return false;
    OauthClientScope detail = (OauthClientScope) o;
    return detail.getScopeId().equals(this.scopeId);
  }
  
  @Override
  public int hashCode(){
    return this.scopeId.hashCode();
  }

}