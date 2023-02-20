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
public class GoogleOauthUserinfoSandboxResponse {
	@JsonProperty("sub")
    private String sub; //id
	@JsonProperty("name")
    private String name;
	@JsonProperty("email")
    private String email;

}
