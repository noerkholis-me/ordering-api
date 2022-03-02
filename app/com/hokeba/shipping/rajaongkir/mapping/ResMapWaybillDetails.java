package com.hokeba.shipping.rajaongkir.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResMapWaybillDetails {

	@JsonProperty("waybill_number")
	public String waybillNumber;

	@JsonProperty("waybill_date")
	public String waybillDate;

	@JsonProperty("waybill_time")
	public String waybillTime;

	@JsonProperty("weight")
	public String weight;

	@JsonProperty("origin")
	public String origin;

	@JsonProperty("destination")
	public String destination;

	@JsonProperty("shippper_name")
	public String shippperName;

	@JsonProperty("shipper_address1")
	public String shipperAddress1;

	@JsonProperty("shipper_address2")
	public String shipperAddress2;

	@JsonProperty("shipper_address3")
	public String shipperAddress3;

	@JsonProperty("shipper_city")
	public String shipperCity;

	@JsonProperty("receiver_name")
	public String receiverName;

	@JsonProperty("receiver_address1")
	public String receiverAddress1;

	@JsonProperty("receiver_address2")
	public String receiverAddress2;

	@JsonProperty("receiver_address3")
	public String receiverAddress3;

	@JsonProperty("receiver_city")
	public String receiverCity;
}
