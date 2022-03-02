package com.hokeba.shipping.rajaongkir.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResMapWaybill {

	@JsonProperty("delivered")
	public String delivered;

	@JsonProperty("summary")
	public ResMapWaybillSummary summary;

	@JsonProperty("details")
	public ResMapWaybillDetails details;

	@JsonProperty("delivery_status")
	public ResMapWaybillDeliveryStatus deliveryStatus;

	@JsonProperty("manifest")
	public ResMapWaybillManifest[] manifest;
}
