package dtos.voucher;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckVoucherCodeRequest {
	@JsonProperty(value = "email")
	private String email;

	@JsonProperty(value = "store_code")
	private String storeCode;

	@JsonProperty(value = "voucher_code")
	private String voucherCode;
}
