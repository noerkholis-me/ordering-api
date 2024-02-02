package dtos.xendit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class XenditCreateQrCodeResponse {

    private String id;

    @JsonProperty("external_id")
    private String externalId;

    private BigDecimal amount;

    @JsonProperty("qr_string")
    private String qrString;

    @JsonProperty("callback_url")
    private String callbackUrl;

    private String description;

    private String type;

    private String status;

    private Date created;

    private Date updated;

    private Map<String, String> metadata;

}