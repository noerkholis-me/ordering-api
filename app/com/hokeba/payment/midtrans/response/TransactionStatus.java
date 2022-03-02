package com.hokeba.payment.midtrans.response;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionStatus {
	//BASIC INFO
	//status
	@JsonProperty("status_code")
	public String status_code;
	@JsonProperty("status_message")
	public String status_message;
	
	//transaction detail
	@JsonProperty("order_id")
	public String order_id;
	@JsonProperty("gross_amount")
	public String gross_amount;
	@JsonProperty("signature_key")
	public String signature_key;

	//etc
	@JsonProperty("payment_type")
	public String payment_type;
	@JsonProperty("fraud_status")
	public String fraud_status;

	//transaction
	@JsonProperty("transaction_id")
	public String transaction_id;
	@JsonProperty("transaction_status")
	public String transaction_status;
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
	@JsonProperty("transaction_time")
	public Date transaction_time;
	
	//ADDITIONAL FIELDS
	//BANK TRANSFER
	//Permata
	@JsonProperty("permata_va_number")
	public String permata_va_number;
	
	//Bca
	@JsonProperty("va_numbers")
	public TransactionBcaVaNumber[] va_numbers;
	@JsonProperty("payment_amounts")
	public Object[] payment_amounts;
	
	//Mandiri (Mandiri Bill)
	@JsonProperty("biller_code")
	public String biller_code;
	@JsonProperty("bill_key")
	public String bill_key;
	
	//CREDIT CARD
	@JsonProperty("masked_card")
	public String masked_card;
	@JsonProperty("bank")
	public String bank;
	@JsonProperty("card_type")
	public String cardType;
	@JsonProperty("eci")
	public String eci;
	@JsonProperty("installment_term")
	public String installment_term;
	
	//OTHER
	//ePay BRI, CIMB Clicks, BCA Clickpay, Klik BCA
	@JsonProperty("approval_code")
	public String approval_code;
	
	//indomaret & kioson
	@JsonProperty("store")
	public String store;
	@JsonProperty("payment_code")
	public String payment_code;
	
	public String fetchVaNumber() {
		if (va_numbers != null && va_numbers.length != 0) {
			return va_numbers[0].va_number;
		} else if (permata_va_number != null) {
			return permata_va_number;
		} else if (bill_key != null) {
			return bill_key;
//		} else if (masked_card != null) {
//			return masked_card;
		} else {
			return null;
		}
	}
	
	public String fetchBankName() {
		if (va_numbers != null && va_numbers.length != 0) {
			return va_numbers[0].bank;
		} else {
			return bank;
		}
	}
	
}
