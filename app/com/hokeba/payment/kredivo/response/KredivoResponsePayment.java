package com.hokeba.payment.kredivo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KredivoResponsePayment {
	@JsonProperty("id")
	public String id;
	@JsonProperty("name")
	public String name;
	@JsonProperty("tenure")
	public Integer tenure;
	@JsonProperty("rate")
	public Double rate;
	@JsonProperty("amount")
	public Double amount;
	@JsonProperty("monthly_installment")
	public Double monthlyInstallment;
	@JsonProperty("installment_amount")
	public Double installmentAmount;
	@JsonProperty("down_payment")
	public Double downPayment;
	@JsonProperty("interest_rate_transition_term")
	public Double interestRateTransitionTerm;
	@JsonProperty("discounted_monthly_installment")
	public Double discountedMonthlyInstallment;
	
}
