package dtos.merchant;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MerchantRequest  {
    
    @JsonProperty("email")
    public String email;
	
    @JsonProperty("name")
	public String name;

    @JsonProperty("url")
	public String url;

    @JsonProperty("phone")
	public String phone;

    @JsonProperty("address")
    public String address;

    @Transient
    @JsonProperty("province_id")
    public Long provinceId;

    @Transient
    @JsonProperty("city_id")
    public Long cityId;

    @Transient
    @JsonProperty("suburb_id")
    public Long suburbId;

    @Transient
    @JsonProperty("area_id")
    public Long areaId;

    @JsonProperty("postal_code")
	public String postalCode;

    @JsonProperty("merchant_type")
    public String merchantType;

    @JsonProperty("product_store_required")
    public boolean productStoreRequired;

    @JsonProperty("global_store_qr_group")
    public boolean globalStoreQrGroup;

    @Transient
    @JsonProperty("is_cash_merchant")
    public boolean isCashMerchant;

    @Transient
    @JsonProperty("type_cash_merchant")
    public String typeCashMerchant;

    @Transient
    @JsonProperty("is_debit_merchant")
    public boolean isDebitMerchant;

    @Transient
    @JsonProperty("type_debit_merchant")
    public String typeDebitMerchant;

    @Transient
    @JsonProperty("is_credit_merchant")
    public boolean isCreditMerchant;

    @Transient
    @JsonProperty("type_credit_merchant")
    public String typeCreditMerchant;

    @Transient
    @JsonProperty("is_qris_merchant")
    public boolean isQrisMerchant;

    @Transient
    @JsonProperty("type_qris_merchant")
    public String typeQrisMerchant;

    @Transient
    @JsonProperty("is_va_merchant")
    public boolean isVaMerchant;

    @Transient
    @JsonProperty("type_va_merchant")
    public String typeVaMerchant;

    @Transient
    @JsonProperty("is_kiosk")
    public boolean isKiosk;

    @Transient
    @JsonProperty("is_cash_kiosk")
    public boolean isCashKiosk;

    @Transient
    @JsonProperty("type_cash_kiosk")
    public String typeCashKiosk;

    @Transient
    @JsonProperty("is_debit_kiosk")
    public boolean isDebitKiosk;

    @Transient
    @JsonProperty("type_debit_kiosk")
    public String typeDebitKiosk;

    @Transient
    @JsonProperty("is_credit_kiosk")
    public boolean isCreditKiosk;

    @Transient
    @JsonProperty("type_credit_kiosk")
    public String typeCreditKiosk;

    @Transient
    @JsonProperty("is_qris_kiosk")
    public boolean isQrisKiosk;

    @Transient
    @JsonProperty("type_qris_kiosk")
    public String typeQrisKiosk;

    @Transient
    @JsonProperty("is_va_kiosk")
    public boolean isVaKiosk;

    @Transient
    @JsonProperty("type_va_kiosk")
    public String typeVaKiosk;

    @Transient
    @JsonProperty("is_mobile_qr")
    public boolean isMobileQr;

    @Transient
    @JsonProperty("is_cash_mobile_qr")
    public boolean isCashMobileQr;

    @Transient
    @JsonProperty("type_cash_mobile_qr")
    public String typeCashMobileQr;

    @Transient
    @JsonProperty("is_debit_mobile_qr")
    public boolean isDebitMobileQr;

    @Transient
    @JsonProperty("type_debit_mobile_qr")
    public String typeDebitMobileQr;

    @Transient
    @JsonProperty("is_credit_mobile_qr")
    public boolean isCreditMobileQr;

    @Transient
    @JsonProperty("type_credit_mobile_qr")
    public String typeCreditMobileQr;

    @Transient
    @JsonProperty("is_qris_mobile_qr")
    public boolean isQrisMobileQr;

    @Transient
    @JsonProperty("type_qris_mobile_qr")
    public String typeQrisMobileQr;

    @Transient
    @JsonProperty("is_va_mobile_qr")
    public boolean isVaMobileQr;

    @Transient
    @JsonProperty("type_va_mobile_qr")
    public String typeVaMobileQr;

    @Transient
    @JsonProperty("is_pos")
    public boolean isPos;

    @Transient
    @JsonProperty("is_cash_pos")
    public boolean isCashPos;

    @Transient
    @JsonProperty("type_cash_pos")
    public String typeCashPos;

    @Transient
    @JsonProperty("is_debit_pos")
    public boolean isDebitPos;

    @Transient
    @JsonProperty("type_debit_pos")
    public String typeDebitPos;

    @Transient
    @JsonProperty("is_credit_pos")
    public boolean isCreditPos;

    @Transient
    @JsonProperty("type_credit_pos")
    public String typeCreditPos;

    @Transient
    @JsonProperty("is_qris_pos")
    public boolean isQrisPos;

    @Transient
    @JsonProperty("type_qris_pos")
    public String typeQrisPos;

    @Transient
    @JsonProperty("is_va_pos")
    public boolean isVaPos;

    @Transient
    @JsonProperty("type_va_pos")
    public String typeVaPos;

}
