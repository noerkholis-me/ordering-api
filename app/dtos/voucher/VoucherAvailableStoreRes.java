package dtos.voucher;

import dtos.store.StoreResponse;
import dtos.store.StoreResponsePuP;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherAvailableStoreRes {
    private Long voucherId;
    private int value;
    private String voucherName;
    private List<StoreResponsePuP> store;
}
