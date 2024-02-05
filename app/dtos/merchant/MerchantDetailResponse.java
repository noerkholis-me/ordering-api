package dtos.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.Merchant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MerchantDetailResponse {

	@JsonProperty("merchant_id")
	private Long merchantId;
	@JsonProperty("name")
	private String name;
	@JsonProperty("full_name")
	private String fullName;
	@JsonProperty("company_name")
	private String companyName;
	@JsonProperty("logo")
	private String logo;
	@JsonProperty("phone")
	private String phone;	
	@JsonProperty("merchant_code")
	private String merchantCode;
	@JsonProperty("merchant_qr_code")
	private String merchantQRCode;
	@JsonProperty("address")
	private String address;
	@JsonProperty("postal_code")
	private String postalCode;
	@JsonProperty("merchant_type")
    private String merchantType;
    
//    merchant_id, name, company_name, full_name, logo, phone, merchant_code, merchant_qr_code, address, postal_code, merchant_type
    
	public MerchantDetailResponse(Merchant model) {
		this.merchantId = model.id;
		this.name = model.name;
		this.fullName = model.fullName;
		this.companyName = model.companyName;
		this.logo = model.logo;
		this.phone = model.phone;
		this.merchantCode = model.merchantCode;
		this.merchantQRCode = model.merchantQrCode;
		this.address = model.address;
		this.postalCode = model.postalCode;
		this.merchantType = model.merchantType;
		
	}
}
