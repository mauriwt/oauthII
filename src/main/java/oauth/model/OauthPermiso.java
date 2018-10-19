package oauth.model;

import java.io.Serializable;
import javax.persistence.*;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The persistent class for the oauth_permiso database table.
 * 
 */
@Entity
@Table(name="oauth_permiso")
@NamedQuery(name="OauthPermiso.findAll", query="SELECT o FROM OauthPermiso o")
@NamedEntityGraph(name = "OauthPermiso.findPermisoUser", 
attributeNodes = {@NamedAttributeNode("oauthUser")},
includeAllAttributes=false)
public class OauthPermiso implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="permiso_id")
	@SequenceGenerator(name="oauth_permiso_permiso_id_seq", sequenceName="oauth_permiso_permiso_id_seq", allocationSize=1, initialValue=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="oauth_permiso_permiso_id_seq")
    private Integer permisoId;

	@Column(name="permiso_name")
	private String permisoName;

	//bi-directional many-to-one association to OauthUser
	@ManyToOne
	@JoinColumn(name="user_id")
	private OauthUser oauthUser;

	public OauthPermiso() {
	}
	
	

	public OauthPermiso(Integer permisoId, String permisoName) {
    super();
    this.permisoId = permisoId;
    this.permisoName = permisoName;
  }



  public OauthUser getOauthUser() {
		return this.oauthUser;
	}

	public void setOauthUser(OauthUser oauthUser) {
		this.oauthUser = oauthUser;
	}

    public Integer getPermisoId() {
      return permisoId;
    }
  
    public void setPermisoId(Integer permisoId) {
      this.permisoId = permisoId;
    }
  
    public String getPermisoName() {
      return permisoName;
    }
  
    public void setPermisoName(String permisoName) {
      this.permisoName = permisoName;
    }
	
	

}