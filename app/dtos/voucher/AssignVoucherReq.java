package dtos.voucher;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignVoucherReq {
    @JsonProperty(value = "voucher_id")
    private Long voucherId;
    @JsonProperty(value = "store_id")
    private List<Long> storeId;
    @JsonProperty("is_edit")
    private boolean isEdit;
}
