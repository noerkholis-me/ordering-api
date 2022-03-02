package com.hokeba.mapping.request;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import models.Merchant;
import models.VoucherDetail;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapVoucher {

	@JsonProperty("name")
	public String name;

	@JsonProperty("description")
	public String description;

	@JsonProperty("masking")
	public String masking;

	@JsonProperty("type")
	public String type;

	@JsonProperty("status")
	public boolean status;

	@JsonProperty("discount")
	public Double discount;

	@JsonProperty("discount_type")
	public int discountType;

	@JsonProperty("count")
	public int count;

	@JsonProperty("max_value")
	public Double maxValue;

	@JsonProperty("min_purchase")
	public Double minPurchase;

	@JsonProperty("priority")
	public int priority;

	@JsonProperty("stop_further_rule_porcessing")
	public int stopFurtherRulePorcessing;

	@JsonProperty("valid_from")
	public String validFrom;

	@JsonProperty("valid_to")
	public String validTo;

	@JsonProperty("filter_status")
	public String filterStatus;

	@JsonProperty("merchant_by")
	public Merchant merchantBy;

	@JsonProperty("details")
	public List<VoucherDetail> voucherDetail;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMasking() {
		return masking;
	}

	public void setMasking(String masking) {
		this.masking = masking;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public int getDiscountType() {
		return discountType;
	}

	public void setDiscountType(int discountType) {
		this.discountType = discountType;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public Double getMinPurchase() {
		return minPurchase;
	}

	public void setMinPurchase(Double minPurchase) {
		this.minPurchase = minPurchase;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getStopFurtherRulePorcessing() {
		return stopFurtherRulePorcessing;
	}

	public void setStopFurtherRulePorcessing(int stopFurtherRulePorcessing) {
		this.stopFurtherRulePorcessing = stopFurtherRulePorcessing;
	}

	public String getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
	}

	public String getValidTo() {
		return validTo;
	}

	public void setValidTo(String validTo) {
		this.validTo = validTo;
	}

	public String getFilterStatus() {
		return filterStatus;
	}

	public void setFilterStatus(String filterStatus) {
		this.filterStatus = filterStatus;
	}

	public List<VoucherDetail> getVoucherDetail() {
		return voucherDetail;
	}

	public void setVoucherDetail(List<VoucherDetail> voucherDetail) {
		this.voucherDetail = voucherDetail;
	}

	public Merchant getMerchantBy() {
		return merchantBy;
	}

	public void setMerchantBy(Merchant merchantBy) {
		this.merchantBy = merchantBy;
	}
}