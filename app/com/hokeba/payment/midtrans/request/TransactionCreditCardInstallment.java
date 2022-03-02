package com.hokeba.payment.midtrans.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;

public class TransactionCreditCardInstallment {
	@JsonProperty("required")
	private boolean required;
	@JsonProperty("terms")
	private Object terms;
	
	public TransactionCreditCardInstallment() {
		super();
	}
	
//	@SuppressWarnings("deprecation")
//	public TransactionCreditCardInstallment(boolean required, MapPaymentTenor installmentData, Integer installmentMonth) {
//		this.required = installmentData == null ? false : true;
//		ObjectNode termData = Json.newObject();
//		termData.put(installmentData.bank, Json.toJson(new Integer[]{installmentMonth}));
//		this.terms = termData;
//	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Object getTerms() {
		return terms;
	}

	public void setTerms(Object terms) {
		this.terms = terms;
	}
}
