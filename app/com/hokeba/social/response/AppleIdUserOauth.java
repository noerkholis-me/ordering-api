package com.hokeba.social.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppleIdUserOauth {
	private String iss;
	private String aud;
	private Long exp; //Long
	private Long iat; //Long
	private String sub;
	private String cHash;//c_hash
	private String email;
	private String emailVerified; //email_verified
	private String isPrivateEmail; //is_private_email
	private Long authTime; //Long auth_time
	private boolean nonceSupported; //boolean nonce_supported
	
	public String getIss() {
		return iss;
	}
	
	public void setIss(String iss) {
		this.iss = iss;
	}
	
	public String getAud() {
		return aud;
	}
	
	public void setAud(String aud) {
		this.aud = aud;
	}
	
	public Long getExp() {
		return exp;
	}
	
	public void setExp(Long exp) {
		this.exp = exp;
	}
	
	public Long getIat() {
		return iat;
	}
	
	public void setIat(Long iat) {
		this.iat = iat;
	}
	
	public String getSub() {
		return sub;
	}
	
	public void setSub(String sub) {
		this.sub = sub;
	}
	
	public String getcHash() {
		return cHash;
	}
	
	public void setcHash(String cHash) {
		this.cHash = cHash;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmailVerified() {
		return emailVerified;
	}
	
	public void setEmailVerified(String emailVerified) {
		this.emailVerified = emailVerified;
	}
	
	public String getIsPrivateEmail() {
		return isPrivateEmail;
	}
	
	public void setIsPrivateEmail(String isPrivateEmail) {
		this.isPrivateEmail = isPrivateEmail;
	}
	
	public Long getAuthTime() {
		return authTime;
	}
	
	public void setAuthTime(Long authTime) {
		this.authTime = authTime;
	}
	
	public boolean isNonceSupported() {
		return nonceSupported;
	}
	
	public void setNonceSupported(boolean nonceSupported) {
		this.nonceSupported = nonceSupported;
	}
}
