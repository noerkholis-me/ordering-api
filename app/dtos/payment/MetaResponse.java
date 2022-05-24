package dtos.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MetaResponse {

    @JsonProperty("reference_id")
    private String referenceId;
    @JsonProperty("qr_code")
    private String qrCode;
    @JsonProperty("account_number")
    private String accountNumber;

}
