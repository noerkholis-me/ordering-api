package dtos.merchant.qrgroup.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QrGroupStoreRequest {

    @JsonProperty("qr_group_id")
    private Long qrGroupId;

    @JsonProperty("store_id")
    private List<Long> storeId;

}
