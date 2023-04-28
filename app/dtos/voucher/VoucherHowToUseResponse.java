package dtos.voucher;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VoucherHowToUseResponse {
	public Long id;
	public String content;

}
