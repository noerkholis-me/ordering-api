package com.hellobisnis.mapping.mobile.response;

import java.util.Date;
import java.util.List;

import com.amazonaws.util.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.payment.midtrans.MidtransService;
import com.hokeba.payment.midtrans.request.MainTransactionSimple;

import models.SOrder;
import models.SOrderPayment;
import models.SalesOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SOrderResponseStatus {
	public Long id;
	public String orderNumber;
	
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
	public Date orderDate;

	public MemberShortResponse member;
	public Double discount;
	public double totalPrice;
	public String paymentType;
	public String status;
	public String tokenMidtrans;
	public String orderType;
	public String trackingUrl;


	public void setTrackingUrl(String trackingUrl){
		this.trackingUrl = trackingUrl;
	}

	public String getTrackingUrl(){
		return this.trackingUrl;
	}

	public String getOrderType(){
		return orderType;
	}

	public void setOrderType(String orderType){
		this.orderType = orderType;
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

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public MemberShortResponse getMember() {
		return member;
	}

	public void setMember(MemberShortResponse member) {
		this.member = member;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getTokenMidtrans() throws Exception {
		String tokenmid = "";
//		SOrderPayment payment = SOrderPayment.find.where().eq("order_id", id).findUnique();
/*		String tokenmid = ""; */
//		
//		if(payment != null) {
//			tokenmid = payment.transactionId;
//		}
/*		SOrder order = SOrder.find.where().eq("orderNumber", orderNumber).setMaxRows(1).findUnique();*/
//		SalesOrder orderData = SalesOrder.find.where().eq("orderNumber", orderNumber).setMaxRows(1).findUnique();
/*		try{
		ServiceResponse responscanceleMidtrans = MidtransService.getInstance().cancelTransaction(orderNumber); 
				
		MainTransactionSimple mainTransaction = new MainTransactionSimple(order, "kiosk"); */
//		MidtransService service = MidtransService.getInstance();
		
//		ServiceResponse wsResponse = MidtransService.getInstance().approveTransaction(orderNumber);
/*		ServiceResponse responseMidtrans = MidtransService.getInstance().checkout(mainTransaction);
		
		if (responscanceleMidtrans.getCode() == 408 || (responscanceleMidtrans.getCode() != 200 && responscanceleMidtrans.getCode() != 201) ) {
			System.out.println("Gagal cancel ey" + responscanceleMidtrans);
		}else {
			System.out.println("Berhasil" + responscanceleMidtrans);
		}
		
		if (responseMidtrans.getCode() == 408 || (responseMidtrans.getCode() != 200 && responseMidtrans.getCode() != 201) ) {
			tokenmid = "";
		}else {
			JSONObject obj = new JSONObject(responseMidtrans.getData().toString());
			tokenmid = obj.getString("token");
		}
		} catch(Exception err){
			err.printStackTrace();
			System.out.println("ERROR GET TOKEM MIDTRANS: " + err.getMessage());
		}
		return tokenmid; */
		return tokenmid;
	}

	public void setTokenMidtrans(String tokenMidtrans) {
		this.tokenMidtrans = tokenMidtrans;
		
		
	}
}
