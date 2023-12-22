package dtos.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class QrCodeStaticPaymentRequest {

    @JsonProperty("external_id")
    private String externalId;

    @JsonProperty("store_code")
    private String storeCode;

}
