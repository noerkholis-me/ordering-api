package com.hokeba.shipping.rajaongkir.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResMapWaybillDeliveryStatus {

	@JsonProperty("status")
	public String status;

	@JsonProperty("pod_receiver")
	public String podReceiver;

	@JsonProperty("pod_date")
	public String podDate;

	@JsonProperty("pod_time")
	public String podTime;
}