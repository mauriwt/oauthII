package oauth.model;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

/**
 * The persistent class for the oauth_client_details database table.
 * 
 */
@Entity
@Table(name = "oauth_client_details")
@JsonInclude(Include.NON_NULL)
@NamedQuery(name = "OauthClientDetail.findAll", query = "SELECT o FROM OauthClientDetail o")
@NamedEntityGraph(name = "OauthClientDetail.findScopesResources", attributeNodes = {
    @NamedAttributeNode("oauthClientResources") }, includeAllAttributes = false)
public class OauthClientDetail implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "client_id")
  private String clientId;

  @Column(name = "access_token_validity")
  private Integer accessTokenValidity;

  @Column(name = "additional_information")
  private String additionalInformation;

  @Column(name = "authorized_grant_types")
  private String authorizedGrantTypes;

  @Column(name = "autoapprove")
  private String autoapprove;

  @Column(name = "client_secret")
  private String clientSecret;

  @Column(name = "refresh_token_validity")
  private Integer refreshTokenValidity;

  @Column(name = "web_server_redirect_uri")
  private String webServerRedirectUri;

  // bi-directional many-to-many association to OauthClientResource
  @OneToMany(mappedBy = "oauthClientDetail")
  @Cascade({CascadeType.ALL})
  @JsonIgnoreProperties(value = { "oauthClientDetail", "oauthClientScopes" }, allowSetters=true)
  private List<OauthClientResource> oauthClientResources;

  // bi-directional many-to-one association to OauthClientScope
  @OneToMany(mappedBy = "oauthClientDetail")
  @Cascade({CascadeType.ALL})
  @JsonIgnoreProperties({ "oauthClientDetail", "oauthClientResource" })
  private List<OauthClientScope> oauthClientScopes;

  public OauthClientDetail() {
  }

  public OauthClientDetail(String clientId, Integer accessTokenValidity, String additionalInformation,
      String authorizedGrantTypes, String autoapprove, String clientSecret, Integer refreshTokenValidity,
      String webServerRedirectUri) {
    super();
    this.clientId = clientId;
    this.accessTokenValidity = accessTokenValidity;
    this.additionalInformation = additionalInformation;
    this.authorizedGrantTypes = authorizedGrantTypes;
    this.autoapprove = autoapprove;
    this.clientSecret = clientSecret;
    this.refreshTokenValidity = refreshTokenValidity;
    this.webServerRedirectUri = webServerRedirectUri;
  }

  public String getClientId() {
    return this.clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public Integer getAccessTokenValidity() {
    return this.accessTokenValidity;
  }

  public void setAccessTokenValidity(Integer accessTokenValidity) {
    this.accessTokenValidity = accessTokenValidity;
  }

  public String getAdditionalInformation() {
    return this.additionalInformation;
  }

  public void setAdditionalInformation(String additionalInformation) {
    this.additionalInformation = additionalInformation;
  }

  public String getAuthorizedGrantTypes() {
    return this.authorizedGrantTypes;
  }

  public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
    this.authorizedGrantTypes = authorizedGrantTypes;
  }

  public String getAutoapprove() {
    return this.autoapprove;
  }

  public void setAutoapprove(String autoapprove) {
    this.autoapprove = autoapprove;
  }

  public String getClientSecret() {
    return this.clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public Integer getRefreshTokenValidity() {
    return this.refreshTokenValidity;
  }

  public void setRefreshTokenValidity(Integer refreshTokenValidity) {
    this.refreshTokenValidity = refreshTokenValidity;
  }

  public String getWebServerRedirectUri() {
    return this.webServerRedirectUri;
  }

  public void setWebServerRedirectUri(String webServerRedirectUri) {
    this.webServerRedirectUri = webServerRedirectUri;
  }

  public List<OauthClientResource> getOauthClientResources() {
    return this.oauthClientResources;
  }

  public void setOauthClientResources(List<OauthClientResource> oauthClientResources) {
    this.oauthClientResources = oauthClientResources;
  }

  public List<OauthClientScope> getOauthClientScopes() {
    return this.oauthClientScopes;
  }

  public void setOauthClientScopes(List<OauthClientScope> oauthClientScopes) {
    this.oauthClientScopes = oauthClientScopes;
  }

  public OauthClientScope addOauthClientScope(OauthClientScope oauthClientScope) {
    getOauthClientScopes().add(oauthClientScope);
    oauthClientScope.setOauthClientDetail(this);

    return oauthClientScope;
  }

  public OauthClientScope removeOauthClientScope(OauthClientScope oauthClientScope) {
    getOauthClientScopes().remove(oauthClientScope);
    oauthClientScope.setOauthClientDetail(null);

    return oauthClientScope;
  }
  
  @Override
  public boolean equals(Object o){
    if(o == null || this.getClass() != o.getClass()) return false;
    OauthClientDetail detail = (OauthClientDetail) o;
    return detail.getClientId().equals(this.clientId);
  }
  
  @Override
  public int hashCode(){
    return this.clientId.hashCode();
  }

}