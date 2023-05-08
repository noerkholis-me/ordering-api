package dtos.voucher;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherPurchaseReq {
	@JsonProperty(value = "user_email")
	private String email;
	@JsonProperty(value = "voucher_id")
	private Long voucherId;
	@JsonProperty(value = "voucher_price")
	private double price;
}
