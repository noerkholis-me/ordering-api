package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.payment.midtrans.response.TransactionToken;

import java.util.List;

/**
 * Created by nugraha on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderUserList {
    public Long id;
    @JsonProperty("order_number")
    public String orderNumber;
    @JsonProperty("order_number_encrypt")
    public String orderNumberEncrypt;
    @JsonProperty("order_date")
    public String orderDate;
    @JsonProperty("order_date_string")
    public String orderDateString;
    @JsonProperty("order_notes")
    public String orderNotes;
    public String currency;
    @JsonProperty("sub_total")
    public Double subTotal;
    public Double total;
    public Double discount;
    public Double voucher;
    public Double shipping;
    @JsonProperty("instalment_cost")
    public Double instalmentCost;
    @JsonProperty("bank_name")
    public String bankName;
    @JsonProperty("payment_method")
    public String paymentMethod;
    @JsonProperty("payment_status")
    public String paymentStatus;
    public String status;
    @JsonProperty("status_text")
    public String statusText;
    @JsonProperty("billing_address")
    public MapAddress billingAddress;
    @JsonProperty("bank")
    public MapBank bank;
    public int qty;
    @JsonProperty("sellers")
    public List<MapOrderSeller> sellers;

    @JsonProperty("midtrans_info")
    public TransactionToken midtransInfo;
    @JsonProperty("kredivo_url")
    public String kredivoUrl;
    
    @JsonProperty("loyalty_point")
    public Long loyaltyPoint;
    
    @JsonProperty("payment_time")
    public String paymentTime;

    @JsonProperty("checkout_type")
    public Long checkoutType;
	public MapOrderUserList(){}

    public MapOrderUserList(Long id, String orderNumber, String orderDate, String currency, Double total, String bankName, int qty, String shippedDate, MapOrderUserStatus orderStatus) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.currency = currency;
        this.bankName = bankName;
        this.qty = qty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getOrderDateString() {
        return orderDateString;
    }

    public void setOrderDateString(String orderDateString) {
        this.orderDateString = orderDateString;
    }

    public List<MapOrderSeller> getSellers() {
        return sellers;
    }

    public void setSellers(List<MapOrderSeller> sellers) {
        this.sellers = sellers;
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

    public Double getShipping() {
        return shipping;
    }

    public void setShipping(Double shipping) {
        this.shipping = shipping;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public MapAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(MapAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getOrderNumberEncrypt() {
        return orderNumberEncrypt;
    }

    public void setOrderNumberEncrypt(String orderNumberEncrypt) {
        this.orderNumberEncrypt = orderNumberEncrypt;
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public MapBank getBank() {
        return bank;
    }

    public void setBank(MapBank bank) {
        this.bank = bank;
    }

	public Double getInstalmentCost() {
		return instalmentCost;
	}

	public void setInstalmentCost(Double instalmentCost) {
		this.instalmentCost = instalmentCost;
	}

	public TransactionToken getMidtransInfo() {
		return midtransInfo;
	}

	public void setMidtransInfo(TransactionToken midtransInfo) {
		this.midtransInfo = midtransInfo;
	}

	public String getKredivoUrl() {
		return kredivoUrl;
	}

	public void setKredivoUrl(String kredivoUrl) {
		this.kredivoUrl = kredivoUrl;
	}

	public String getOrderNotes() {
		return orderNotes;
	}

	public void setOrderNotes(String orderNotes) {
		this.orderNotes = orderNotes;
	}

    public Long getLoyaltyPoint() {
		return loyaltyPoint;
	}

	public void setLoyaltyPoint(Long loyaltyPoint) {
		this.loyaltyPoint = loyaltyPoint;
	}

	public String getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(String paymentTime) {
		this.paymentTime = paymentTime;
	}

	public Long getCheckoutType() {
		return checkoutType;
	}

	public void setFlag(Long checkoutType) {
		this.checkoutType = checkoutType;
	}

	
}