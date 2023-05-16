package dtos.voucher;

import dtos.merchant.MerchantResponse;
import lombok.Setter;
import lombok.Builder;
import lombok.Getter;
@Getter
@Setter
@Builder
public class VoucherListResponse {
	private Long id;
	private boolean isAvailable;
	private int value;
	private int purchasePrice;
	private int expiryDay;
	private String name;
	private String code;
	private String description;
	private String valueText;
	private MerchantResponse merchant;
	private String voucherType;
}
