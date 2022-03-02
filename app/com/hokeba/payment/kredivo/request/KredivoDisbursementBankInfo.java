package com.hokeba.payment.kredivo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class KredivoDisbursementBankInfo {
	@JsonProperty("bank_name")
	public String bankName;
	@JsonProperty("bank_account_number")
	public String bankAccountNumber;
	@JsonProperty("bank_account_name")
	public String bankAccountName;
	
}
