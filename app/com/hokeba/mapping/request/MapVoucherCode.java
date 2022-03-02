package com.hokeba.mapping.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nugraha on 6/29/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapVoucherCode {
    @JsonProperty("voucher_id")
    private Long voucherId;
    @JsonProperty("voucher_code")
    private String voucherCode;
    @JsonProperty("voucher_amount")
    private Double voucherAmount;
    @JsonProperty("voucher_info")
    private String voucherInfo;
    private String message;

    public MapVoucherCode(){

    }

    public MapVoucherCode(String voucherCode, Double voucherAmount, String message) {
        this.voucherCode = voucherCode;
        this.voucherAmount = voucherAmount;
        this.message = message;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public Long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public Double getVoucherAmount() {
        return voucherAmount;
    }

    public void setVoucherAmount(Double voucherAmount) {
        this.voucherAmount = voucherAmount;
    }

    public String getVoucherInfo() {
		return voucherInfo;
	}

	public void setVoucherInfo(String voucherInfo) {
		this.voucherInfo = voucherInfo;
	}

	public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
