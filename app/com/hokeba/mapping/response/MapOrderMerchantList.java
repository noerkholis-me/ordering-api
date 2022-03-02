package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 4/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderMerchantList {
    public Long id;
    @JsonProperty("order_number")
    private String orderNumber;
    public String status;
    public String date;
    @JsonProperty("dispatch_date")
    public String dispatchDate;
    public String sdate;
    public String edate;
    @JsonProperty("payment_date_cust")
    public String paymentDateCust;
    @JsonProperty("total_price")
    public Double totalPrice;
    public Double total;
    @JsonProperty("sub_total")
    public Double subTotal;
    @JsonProperty("sub_total_with_discount")
    public Double subTotalWithDiscount;
    public Double shipping;
    public Double discount;
    public Double voucher;

    @JsonProperty("buyer_id")
    public Long buyerId;
    @JsonProperty("buyer_name")
    public String buyerName;
    @JsonProperty("buyer_address")
    public String buyerAddress;
    @JsonProperty("tracking_number")
    public String trackingNumber;
    @JsonProperty("shipping_address")
    public MapAddress shippingAddress;
    public String currency;
    @JsonProperty("courier_name")
    public String courierName;
    @JsonProperty("courier_code")
    public String courierCode;
    @JsonProperty("courier_service_name")
    public String courierServiceName;
    @JsonProperty("courier_service_code")
    public String courierServiceCode;
    @JsonProperty("order_notes")
    public String orderNotes;
    @JsonProperty("seller_loyalty")
    public Long sellerLoyalty;
    @JsonProperty("seller_loyalty_earn")
    public Long sellerLoyaltyEarn;

	public List<MapSalesOrderDetailMerchant> items;

    @JsonProperty("voucher_info")
    public List<MapOrderSellerVoucherDetail> voucherInfo;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(String dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public String getCourierCode() {
        return courierCode;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public List<MapSalesOrderDetailMerchant> getItems() {
        return items;
    }

    public void setItems(List<MapSalesOrderDetailMerchant> items) {
        this.items = items;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public MapAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(MapAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }
    
    public Double getSubTotalWithDiscount() {
		return subTotalWithDiscount;
	}

	public void setSubTotalWithDiscount(Double subTotalWithDiscount) {
		this.subTotalWithDiscount = subTotalWithDiscount;
	}

	public Double getShipping() {
        return shipping;
    }

    public void setShipping(Double shipping) {
        this.shipping = shipping;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getCourierServiceName() {
		return courierServiceName;
	}

	public void setCourierServiceName(String courierServiceName) {
		this.courierServiceName = courierServiceName;
	}
	
	public String getCourierServiceCode() {
		return this.courierServiceCode;
	}

	public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

	public String getOrderNotes() {
		return orderNotes;
	}

	public void setOrderNotes(String orderNotes) {
		this.orderNotes = orderNotes;
	}

	public List<MapOrderSellerVoucherDetail> getVoucherInfo() {
		return voucherInfo;
	}

	public void setVoucherInfo(List<MapOrderSellerVoucherDetail> voucherInfo) {
		this.voucherInfo = voucherInfo;
	}

	public String getPaymentDateCust() {
		return paymentDateCust;
	}

	public void setPaymentDateCust(String paymentDateCust) {
		this.paymentDateCust = paymentDateCust;
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