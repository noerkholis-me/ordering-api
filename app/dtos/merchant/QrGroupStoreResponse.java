package dtos.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QrGroupStoreResponse {

    @JsonProperty("store_id")
    private Long storeId;

    @JsonProperty("qr_group_id")
    private Long qrGroupId;

}
