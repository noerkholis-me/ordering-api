package com.hokeba.payment.midtrans.request;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionCreditCard {
	private static final boolean defaultSecure = true;
	private static final String defaultChannel = "migs";
	private static final String defaultBank = "bca";
	private static final boolean defaultInstallmentRequire = true;
	
	@JsonProperty("secure")
	public boolean secure;
	@JsonProperty("channel")
	public String channel;
//	@JsonProperty("bank")
//	public String bank;
	@JsonProperty("installment")
	public TransactionCreditCardInstallment installment;
	@JsonProperty("whitelist_bins")
	public String[] whitelist_bins;
	
	public TransactionCreditCard() {
		super();
	}
	
//	public TransactionCreditCard(MapPaymentTypeCms paymentType, Payment paymentData) {
//		this.secure = defaultSecure;
//		this.channel = defaultChannel;
////		this.bank = defaultBank;
//		this.installment = paymentType.installment == null ? null :
//			new TransactionCreditCardInstallment(defaultInstallmentRequire, paymentType.installment, paymentData.instalmentMonth);
//		
//		List<String> whitelistBin = new ArrayList<>();
//		if (paymentType.whitelist_bin != null) {
//			for (MapPaymentWhitelistBin whitelistData : paymentType.whitelist_bin) {
//				whitelistBin.add(whitelistData.bin_number);
//			}
//		}
//		this.whitelist_bins = whitelistBin.toArray(new String[0]);
//	}
}
