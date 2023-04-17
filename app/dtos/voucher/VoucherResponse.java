package dtos.voucher;

import java.math.BigDecimal;
import java.util.List;

import dtos.merchant.MerchantResponse;
import dtos.shipper.AreaResponse;
import dtos.shipper.CityResponse;
import dtos.shipper.ProvinceResponse;
import dtos.shipper.SuburbResponse;
import dtos.store.ProductStoreResponseForStore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoucherResponse {
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
