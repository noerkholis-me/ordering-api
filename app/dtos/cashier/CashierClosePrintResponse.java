package dtos.cashier;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CashierClosePrintResponse extends CashierReportResponse {
    @JsonProperty("image_store_url")
    private String imageStoreUrl;
    @JsonProperty("store_name")
    private String storeName;
    @JsonProperty("store_address")
    private String storeAddress;
    @JsonProperty("store_phone_number")
    private String storePhoneNumber;
}
