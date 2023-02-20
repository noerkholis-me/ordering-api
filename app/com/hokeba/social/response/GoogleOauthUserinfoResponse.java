package com.hokeba.social.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleOauthUserinfoResponse {
	@JsonProperty("sub")
    private String sub; //id
	@JsonProperty("name")
    private String name;
	@JsonProperty("given_name")
    private String givenName;
	@JsonProperty("family_name")
    private String familyName;
	@JsonProperty("picture")
    private String picture;
	@JsonProperty("email")
    private String email;
	@JsonProperty("email_verified")
    private Boolean emailVerified;
	@JsonProperty("locale")
    private String locale;
	@JsonProperty("hd")
    private String hd; //email domain

}
