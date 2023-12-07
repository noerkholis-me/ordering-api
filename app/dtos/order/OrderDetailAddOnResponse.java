package dtos.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDetailAddOnResponse {

    @JsonProperty("no_sku")
    private String noSku;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("product_Price")
    private String productPrice;

}
