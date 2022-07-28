package dtos.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.BigDecimalSerialize;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductDetailPosResponse {

    @JsonProperty("product_name")
    private String productName;
    private int qty;
    @JsonProperty("product_price")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal productPrice;

    private List<ProductAddOnPosResponse> productAddOnPosResponses;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class ProductAddOnPosResponse {
        @JsonProperty("product_name")
        private String productName;
        private int qty;
        @JsonProperty("product_price")
        @JsonSerialize(using = BigDecimalSerialize.class)
        private BigDecimal productPrice;
    }


}
