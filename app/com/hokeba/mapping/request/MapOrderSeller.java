package com.hokeba.mapping.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapOrderSeller {
	@JsonProperty("merchant_id")
	private Long merchantId;
	@JsonProperty("vendor_id")
	private Long vendorId;
	@JsonProperty("courier_service_id")
	private Long courierServiceId;
	@JsonProperty("shipping_price")
	private Double shippingPrice;
	@JsonProperty("shipment_type")
	private Long shipmentType;

	private MapPickUpPoint pickuppoint;
	private MapOrderCourier courier;
	
	List<MapOrderDetail> items;

	public final Double getShippingPrice() {
		return shippingPrice;
	}

	public final void setShippingPrice(Double shippingPrice) {
		this.shippingPrice = shippingPrice;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public Long getVendorId() {
		return vendorId;
	}

	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}

	public Long getCourierServiceId() {
		return courierServiceId;
	}

	public void setCourierServiceId(Long courierServiceId) {
		this.courierServiceId = courierServiceId;
	}

	public List<MapOrderDetail> getItems() {
		return items;
	}

	public void setItems(List<MapOrderDetail> items) {
		this.items = items;
	}

	public MapOrderCourier getCourier() {
		return courier;
	}

	public void setCourier(MapOrderCourier courier) {
		this.courier = courier;
	}

	public Long getShipmentType() {
		return shipmentType == null ? 0L : shipmentType;
	}

	public void setShipmentType(Long shipmentType) {
		this.shipmentType = shipmentType;
	}

	public MapPickUpPoint getPickuppoint() {
		return pickuppoint;
	}

	public void setPickuppoint(MapPickUpPoint pickuppoint) {
		this.pickuppoint = pickuppoint;
	}
	
}
