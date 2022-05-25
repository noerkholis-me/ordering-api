package dtos.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShowQrCodeResponse {

    @JsonProperty("order_number")
    private String orderNumber;
    @JsonProperty("invoice_no")
    private String invoiceNo;
    @JsonProperty("qr_code")
    private String qrCode;
    @JsonProperty("status")
    private String status;

}
