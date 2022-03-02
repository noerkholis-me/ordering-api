package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by nugraha on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderSeller {
    public Long id;
    @JsonProperty("order_number")
    public String orderNumber;
    @JsonProperty("courier_name")
    public String courierName;
    @JsonProperty("courier_code")
    public String courierCode;
    @JsonProperty("courier_service_name")
    public String courierServiceName;
    @JsonProperty("tracking_number")
    public String trackingNumber;
    @JsonProperty("seller_name")
    public String sellerName;
    public String sdate;
    public String edate;
    public Double volumes;
    public Double weights;
    @JsonProperty("merchant_id")
    private Long merchantId;
    @JsonProperty("vendor_id")
    private Long vendorId;
    @JsonProperty("courier_service_id")
    private Long courierServiceId;
    @JsonProperty("sub_total")
    public Double subTotal;
    public Double total;
    public Double discount;
    public Double voucher;
    public Double shipping;
    @JsonProperty("payment_method")
    public String paymentMethod;
    @JsonProperty("payment_status")
    public String paymentStatus;
    public List<MapOrderSellerProduct> items;
    @JsonProperty("order_status")
    public MapOrderUserStatus orderStatus;
    
    @JsonProperty("seller_loyalty")
    public Long sellerLoyalty;    
    @JsonProperty("seller_loyalty_earn")
    public Long sellerLoyaltyEarn;

	public String getCourierName() {
    	return courierName;
    }
    
    public void setCourierName(String courierName) {
    	this.courierName = courierName;
    }
    
    public String getCourierServiceName() {
        return courierServiceName;
    }
	
	public String getCourierCode(){
		return courierCode;
	}

    public void setCourierServiceName(String courierServiceName) {
        this.courierServiceName = courierServiceName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }

    public String getEdate() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
    }

    public List<MapOrderSellerProduct> getItems() {
        return items;
    }

    public void setItems(List<MapOrderSellerProduct> items) {
        this.items = items;
    }

    public MapOrderUserStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(MapOrderUserStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getShipping() {
        return shipping;
    }

    public void setShipping(Double shipping) {
        this.shipping = shipping;
    }

    public Long getCourierServiceId() {
        return courierServiceId;
    }

    public void setCourierServiceId(Long courierServiceId) {
        this.courierServiceId = courierServiceId;
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

    public Double getVolumes() {
        return volumes;
    }

    public void setVolumes(Double volumes) {
        this.volumes = volumes;
    }

    public Double getWeights() {
        return weights;
    }

    public void setWeights(Double weights) {
        this.weights = weights;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getVoucher() {
        return voucher;
    }

    public void setVoucher(Double voucher) {
        this.voucher = voucher;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	
	public Long getSellerLoyalty() {
		return sellerLoyalty;
	}

	public void setSellerLoyalty(Long sellerLoyalty) {
		this.sellerLoyalty = sellerLoyalty;
	}

	public Long getSellerLoyaltyEarn() {
		return sellerLoyaltyEarn;
	}

	public void setSellerLoyaltyEarn(Long sellerLoyaltyEarn) {
		this.sellerLoyaltyEarn = sellerLoyaltyEarn;
	}
}