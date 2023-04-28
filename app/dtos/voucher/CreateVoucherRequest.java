package dtos.voucher;

import java.math.BigDecimal;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVoucherRequest {
	private String value;
	private String valueText;
	private int purchasePrice;
	private int expiryDay;
	private String name;
	private String code;
	private String description;
	private String voucherType;
}
