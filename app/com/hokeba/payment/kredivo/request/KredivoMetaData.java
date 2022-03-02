package com.hokeba.payment.kredivo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class KredivoMetaData {
	@JsonProperty("ip_address")
	public String ipAddress;
	@JsonProperty("user_agent")
	public String userAgent;
	@JsonProperty("device_id")
	public String deviceId;
	@JsonProperty("imsi")
	public String imsi;
}
