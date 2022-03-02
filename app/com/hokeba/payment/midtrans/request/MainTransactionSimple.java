package com.hokeba.payment.midtrans.request;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.SOrder;
import models.SOrderDetail;
import play.Logger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MainTransactionSimple {
	@JsonProperty("transaction_details")
	public TransactionDetail transaction_details;
	@JsonProperty("item_details")
	public TransactionItemDetail[] item_details;
	@JsonProperty("enabled_payments")
	public String[] enabled_payments;
	
	public MainTransactionSimple() {
		super();
	}
	
	public MainTransactionSimple(SOrder order, String device) throws Exception{
		try {
			if (order == null) {
				throw new Exception("Order data not found");
			}
			Long grossAmount = order.fetchRoundedGrandTotal();
			if ("kiosk".equals(device)) {
				this.enabled_payments = new String[] {"gopay"};
			} else if ("mobile".equals(device)) {
				this.enabled_payments = new String[]{"credit_card", "bca_va", "echannel", "bni_va", "bri_va", "permata_va", "other_va", "gopay"};
			}
			this.transaction_details = new TransactionDetail(order.orderNumber, grossAmount);
			
			this.item_details = createItemList(order, grossAmount).toArray(new TransactionItemDetail[0]);

		} catch (Exception e) {
			Logger.error("MainTransaction", e);
			throw new Exception(e.getMessage());
		}
	}
	
	public List<TransactionItemDetail> createItemList(SOrder order, long grossAmount) {
		List<TransactionItemDetail> result = new ArrayList<>();
		for (SOrderDetail orderDetail : order.details) {
			Long price = Math.round(Math.ceil(orderDetail.totalPrice));
			
			System.out.println(orderDetail.product.sku);
			System.out.println(orderDetail.product.name);
			System.out.println(price);
			System.out.println(orderDetail.quantity);
			
			TransactionItemDetail detail = new TransactionItemDetail(orderDetail.product.sku, orderDetail.product.name,
					price, orderDetail.quantity);
			result.add(detail);
		}
		return result;
	}


}
