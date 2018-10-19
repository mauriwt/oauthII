package oauth.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the oauth_access_token database table.
 * 
 */
@Entity
@Table(name="oauth_access_token")
@NamedQuery(name="OauthAccessToken.findAll", query="SELECT o FROM OauthAccessToken o")
public class OauthAccessToken implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="authentication_id")
	private String authenticationId;

	private byte[] authentication;

	@Column(name="client_id")
	private String clientId;

	@Column(name="refresh_token")
	private String refreshToken;

	private byte[] token;

	@Column(name="token_id")
	private String tokenId;

	@Column(name="user_name")
	private String userName;

	public OauthAccessToken() {
	}

	public String getAuthenticationId() {
		return this.authenticationId;
	}

	public void setAuthenticationId(String authenticationId) {
		this.authenticationId = authenticationId;
	}

	public byte[] getAuthentication() {
		return this.authentication;
	}

	public void setAuthentication(byte[] authentication) {
		this.authentication = authentication;
	}

	public String getClientId() {
		return this.clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getRefreshToken() {
		return this.refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public byte[] getToken() {
		return this.token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}

	public String getTokenId() {
		return this.tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}