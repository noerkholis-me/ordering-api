package com.hokeba.payment.kredivo.request;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.payment.kredivo.KredivoService;
import com.hokeba.payment.midtrans.request.TransactionItemDetail;

import controllers.users.PaymentsController;
import models.SalesOrder;
import models.SalesOrderDetail;
import models.SalesOrderSeller;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class KredivoRequest {
	@JsonProperty("server_key")
	public String serverKey;
	@JsonProperty("payment_type")
	public String paymentType;
	
	@JsonProperty("tokenize_user")
	public boolean tokenizeUser;
	@JsonProperty("client_user_key")
	public String clientUserKey;
	@JsonProperty("user_token")
	public String userToken;
	
	@JsonProperty("push_uri")
	public String pushUri;
	@JsonProperty("user_cancel_uri")
	public String userCancelUri;
	@JsonProperty("back_to_store_uri")
	public String backToStoreUri;
	
	@JsonProperty("expiration_time")
	public String expirationTime;
	
	@JsonProperty("sellers")
	public List<KredivoSeller> sellers;
	@JsonProperty("customer_details")
	public KredivoCustomerDetails customerDetails;
	@JsonProperty("billing_address")
	public KredivoAddress billingAddress;
	@JsonProperty("shipping_address")
	public KredivoAddress shippingAddress;
	
	@JsonProperty("transaction_details")
	public KredivoTransactionDetail transactionDetails;
	
	@JsonProperty("metadata")
	public KredivoMetaData metadata;
	@JsonProperty("disbursement_bank_info")
	public KredivoDisbursementBankInfo disbursementBankInfo;
	
	public KredivoRequest() {
		super();
	}
	
	public KredivoRequest(SalesOrder model) {
		//callback config
		this.pushUri = PaymentsController.URL_KREDIVO_PUSH;
		this.userCancelUri = PaymentsController.URL_TXN_CANCEL_REDIRECT;
		this.backToStoreUri = PaymentsController.URL_TXN_SUCCESS_REDIRECT;
		this.expirationTime = "" + model.expiredDate.getTime(); //TODO cari tahu format expiration_time Kredivo
		
		//customer data
		this.customerDetails = new KredivoCustomerDetails(model.member);
		this.shippingAddress = new KredivoAddress(model.shipmentAddress);
		this.billingAddress = new KredivoAddress(model.billingAddress);
		
		//transaction data
		this.transactionDetails = new KredivoTransactionDetail();
		this.transactionDetails.orderId = model.orderNumber;
		this.transactionDetails.amount = model.subtotal;
		
		this.transactionDetails.items = new ArrayList<>();
		this.sellers = new ArrayList<>();
		
		double subtotal = model.subtotal;
		double totalCount = 0D;
		for (SalesOrderSeller orderSeller : model.salesOrderSellers) {
			KredivoSeller sellerData = new KredivoSeller(orderSeller.merchant);
			this.sellers.add(sellerData);
			
			for (SalesOrderDetail orderDetail : orderSeller.salesOrderDetail) {
				KredivoTransactionItem orderDetailData = new KredivoTransactionItem(
						orderDetail.getProductSku(), orderDetail.fetchProductNameFull(),
						orderDetail.priceDiscount, orderDetail.quantity,
						orderDetail.getProductUrl(), orderDetail.getImageUrl(), 
						orderDetail.fetchProductCategory(),
						KredivoService.ITEM_PARENT_TYPE_ITEM, orderDetail.getSku());
				this.transactionDetails.items.add(orderDetailData);
				totalCount += (orderDetail.priceDiscount * orderDetail.quantity);
			}
			
			KredivoTransactionItem sellerShippingData = new KredivoTransactionItem(
					KredivoService.ITEM_ID_SHIPPING, "<Shipping>" + orderSeller.merchant.name + " - " + orderSeller.courierName,
					orderSeller.shipping, 1,
					null, null, null,
					KredivoService.ITEM_PARENT_TYPE_SELLER, orderSeller.merchant.merchantCode);
			this.transactionDetails.items.add(sellerShippingData);
			totalCount += (orderSeller.shipping);
		}
		if (!model.voucher.equals(0D)) {
			KredivoTransactionItem voucherData = new KredivoTransactionItem(
					KredivoService.ITEM_ID_DISCOUNT, "<Voucher>",
					model.voucher, 1,
					null, null, null, null, null);
			this.transactionDetails.items.add(voucherData);
			totalCount -= (model.voucher);
		}
		
		if (!model.getLoyaltyPoint().equals(0L)) {
			KredivoTransactionItem loyaltyData = new KredivoTransactionItem(
					KredivoService.ITEM_ID_MIXPAYMENT, "<Loyalty Point>",
					model.getLoyaltyPoint().doubleValue(), 1,
					null, null, null, null, null);
			this.transactionDetails.items.add(loyaltyData);
			totalCount += (model.getLoyaltyPoint().doubleValue());
		}
		
		double difference = subtotal - totalCount;
		if (difference != 0D) {
			KredivoTransactionItem roundUpData = new KredivoTransactionItem(
					(difference < 0D ? KredivoService.ITEM_ID_DISCOUNT : KredivoService.ITEM_ID_ADDITIONAL), 
					"<Round Up Price>", Math.abs(difference), 1,
					null, null, null, null, null);
			this.transactionDetails.items.add(roundUpData);
		}
	}
	
}
