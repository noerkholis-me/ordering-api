package com.hokeba.payment.midtrans.request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.SalesOrder;
import models.SalesOrderDetail;
import models.SalesOrderSeller;
import play.Logger;
import play.Play;
import play.libs.Json;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MainTransaction {
	@JsonProperty("transaction_details")
	public TransactionDetail transaction_details;
	@JsonProperty("item_details")
	public TransactionItemDetail[] item_details;
	@JsonProperty("enabled_payments")
	public String[] enabled_payments;
	@JsonProperty("credit_card")
	public TransactionCreditCard credit_card;
	@JsonProperty("gopay")
	public TransactionGopay gopay;
	@JsonProperty("customer_details")
	public TransactionCustomerDetail customer_details;
	@JsonProperty("expiry")
	public TransactionExpiry expiry;
	@JsonProperty("custom_field1")
	public Object custom_field1;
	@JsonProperty("custom_field2")
	public Object custom_field2;
	@JsonProperty("custom_field3")
	public Object custom_field3;
	
	public MainTransaction() {
		super();
	}
	
	public MainTransaction(SalesOrder orderTarget) throws Exception{
		try {
			if (orderTarget == null) {
				throw new Exception("Order data not found");
			}
			Long grossAmount = orderTarget.fetchRoundedGrandTotal();
			this.transaction_details = new TransactionDetail(orderTarget.orderNumber, grossAmount);
			this.customer_details = new TransactionCustomerDetail(orderTarget.member, orderTarget.shipmentAddress, orderTarget.billingAddress);

			this.enabled_payments = new String[]{"credit_card", "bca_va", "echannel", "bni_va", "bri_va", "permata_va", "other_va", "gopay"};
			//this.enabled_payments = new String[]{"credit_card", "bca_va", "gopay"};
			this.expiry = new TransactionExpiry(orderTarget.orderDate, orderTarget.expiredDate, TimeUnit.MINUTES);
			
			this.item_details = createItemList(orderTarget, grossAmount).toArray(new TransactionItemDetail[0]);

//			this.credit_card = new TransactionCreditCard(paymentType, paymentData);
			this.credit_card = createDefaultTransactionCreditCard();
			this.gopay = createDefaultTransactionGopay();
		} catch (Exception e) {
			Logger.error("MainTransaction", e);
			throw new Exception(e.getMessage());
		}
	}
	
	public List<TransactionItemDetail> createItemList(SalesOrder salesOrder, long grossAmount) {
		List<TransactionItemDetail> result = new ArrayList<>();
		long totalRoundUp = 0;
		for (SalesOrderSeller orderSeller : salesOrder.salesOrderSellers) {
			//add all item from this merchant
			for (SalesOrderDetail orderDetail : orderSeller.salesOrderDetail) {
				if(!orderDetail.isDeleted) {
					TransactionItemDetail transactionItem = new TransactionItemDetail(
							orderDetail.getProductSku(), orderDetail.fetchProductNameFull(), 
							Math.round(Math.ceil(orderDetail.priceDiscount)), orderDetail.quantity);
					result.add(transactionItem);
					totalRoundUp += transactionItem.fetchTotalPrice();
				}
			}
			
			//add logistic cost from this merchant
			TransactionItemDetail transactionShipping = new TransactionItemDetail(
					orderSeller.merchant.merchantCode + "-" + orderSeller.courierCode, 
					"<Shipping>" + orderSeller.merchant.name + " - " + orderSeller.courierName, 
					Math.round(Math.ceil(orderSeller.shipping)), 1);
			result.add(transactionShipping);
			totalRoundUp += transactionShipping.fetchTotalPrice();
		}
		
		//add voucher
//		for (OrderVoucherDetail orderVoucher : salesOrder.orderVoucherDetail) {
//			TransactionItemDetail transactionItem = new TransactionItemDetail(
//					orderVoucher.voucherCode, "<Voucher>" + orderVoucher.programName, 
//					-Math.round(Math.ceil(orderVoucher.voucherNominal)), 1);
//			result.add(transactionItem);
//			totalRoundUp += transactionItem.fetchTotalPrice();
//		}
		if (!salesOrder.voucher.equals(0D)) {
			TransactionItemDetail transactionVoucher = new TransactionItemDetail(
					"<Voucher>", "<Voucher>" , -Math.round(Math.ceil(salesOrder.voucher)), 1);
			result.add(transactionVoucher);
			totalRoundUp += transactionVoucher.fetchTotalPrice();
		}

		if (!salesOrder.getLoyaltyPoint().equals(0L)) {
			TransactionItemDetail transactionLoyalty = new TransactionItemDetail(
					"<Loyalty Point>", "<Loyalty Point>" ,salesOrder.getLoyaltyPoint() , 1);
			result.add(transactionLoyalty);
			totalRoundUp += transactionLoyalty.fetchTotalPrice();
		}
		
		if (grossAmount != totalRoundUp) {
			TransactionItemDetail roundUpItem = new TransactionItemDetail(
					"<Round Up Price>", "<Round Up Price>", 
					 (grossAmount - totalRoundUp), 1);
			result.add(roundUpItem);
		}
		
//		System.out.println("TOTAL ROUND UP = " + totalRoundUp);
//		System.out.println("ITEM LIST = \n" + Tool.prettyPrint(Json.toJson(result)));
		return result;
	}

	public TransactionCreditCard createDefaultTransactionCreditCard() {
		TransactionCreditCard result = new TransactionCreditCard();
		result.channel = "migs";
		result.secure = true;
		result.installment = new TransactionCreditCardInstallment();
		result.installment.setRequired(false);
		
		ObjectNode termData = Json.newObject();
		termData.put("bca", Json.toJson(new Integer[]{6, 12}));
		termData.put("danamon", Json.toJson(new Integer[]{3, 6, 12}));
		termData.put("hsbc", Json.toJson(new Integer[]{6, 12}));
		result.installment.setTerms(termData);
		
		List<String> whitelistBin = new ArrayList<>();
//		whitelistBin.add("48111111");
//		whitelistBin.add("41111111");
		result.whitelist_bins = whitelistBin.toArray(new String[0]);
		return result;
	}
	
	public TransactionGopay createDefaultTransactionGopay() {
		String appCallbackUrl = Play.application().configuration().getString("whizliz.app_callback.url", null);
		return new TransactionGopay(true, appCallbackUrl);
	}
	
	
}
