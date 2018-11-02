package de.shiyar.springframework.security.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "app_access_token")
public class OauthAccessToken {

	@Id
	@Column(name = "jsessionid")
	private String jsessionid;

	@Column(name = "access_token")
	@Lob
	private String accessToken;

	@Column(name = "token_type")
	private String tokenType;

	@Column(name = "refresh_token")
	@Lob
	private String refreshToken;

	@Column(name = "expires_in")
	private int expiresIn;

	@Column(name = "scope")
	private String scope;

	@Column(name = "firstName")
	private String firstName;

	@Column(name = "lastname")
	private String lastName;

	@Column(name = "grantType")
	private String grantType;

	@Column(name = "jti")
	private String jti;

	public String getJsessionid() {
		return jsessionid;
	}

	public void setJsessionid(String jsessionid) {
		this.jsessionid = jsessionid;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	public String getJti() {
		return jti;
	}

	public void setJti(String jti) {
		this.jti = jti;
	}

}
