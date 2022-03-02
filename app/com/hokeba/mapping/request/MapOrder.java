package com.hokeba.mapping.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.payment.kredivo.KredivoService;
import com.hokeba.payment.midtrans.MidtransService;

import java.util.List;

/**
 * Created by hendriksaragih on 4/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapOrder {
    @JsonProperty("shipping_address")
    private Long shippingAddress;
    @JsonProperty("pickup_point")
    private Long pickupPoint;
    @JsonProperty("billing_address")
    private Long billingAddress;
    @JsonProperty("bank_id")
    private Long bankId;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("order_notes")
    private String orderNotes;
    
    @JsonIgnore
    private String deviceType;
    
    public void setDeviceType(String deviceType){
    	this.deviceType = deviceType;
    }
    
    public String getDeviceType(){
    	return this.deviceType;
    }

    List<MapOrderSeller> sellers;

    List<MapVoucherCode> vouchers;

    @JsonProperty("loyalty")
    private Long loyalty;
    
    public Long getLoyalty() {
        return loyalty;
    }
    
    public void setLoyalty(Long loyalty) {
        this.loyalty = loyalty;
    }

	public Long getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Long shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Long getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Long billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getPaymentMethod() {
        return MidtransService.PAYMENT_METHOD_MIDTRANS.equals(paymentMethod) ||
        	   KredivoService.PAYMENT_METHOD_KREDIVO.equals(paymentMethod) ? 
        			   paymentMethod : MidtransService.PAYMENT_METHOD_MIDTRANS;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderNotes() {
		return orderNotes;
	}

	public void setOrderNotes(String orderNotes) {
		this.orderNotes = orderNotes;
	}

	public List<MapOrderSeller> getSellers() {
        return sellers;
    }

    public void setSellers(List<MapOrderSeller> sellers) {
        this.sellers = sellers;
    }

    public List<MapVoucherCode> getVouchers() {
        return vouchers;
    }

    public void setVouchers(List<MapVoucherCode> vouchers) {
        this.vouchers = vouchers;
    }

    public Long getPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(Long pickupPoint) {
        this.pickupPoint = pickupPoint;
    }
}
