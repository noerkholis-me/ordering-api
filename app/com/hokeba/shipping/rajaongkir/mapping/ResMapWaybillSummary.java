package com.hokeba.shipping.rajaongkir.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResMapWaybillSummary {

	@JsonProperty("courier_code")
	public String courierCode;

	@JsonProperty("courier_name")
	public String courierName;

	@JsonProperty("waybill_number")
	public String waybillNumber;

	@JsonProperty("service_code")
	public String serviceCode;

	@JsonProperty("waybill_date")
	public String waybillDate;

	@JsonProperty("shipper_name")
	public String shipperName;

	@JsonProperty("receiver_name")
	public String receiverName;

	@JsonProperty("origin")
	public String origin;

	@JsonProperty("destination")
	public String destination;

	@JsonProperty("status")
	public String status;
}