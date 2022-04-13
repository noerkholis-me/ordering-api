package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import utils.BigDecimalSerialize;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductStoreResponse implements Serializable {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("store_id")
    private Long storeId;
    @JsonProperty("store_price")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal storePrice;
    @JsonProperty("discount_type")
    private String discountType;
    @JsonProperty("discount")
    private Double discount;
    @JsonProperty("final_price")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal finalPrice;

    @JsonProperty("is_active")
    private Boolean isActive;
    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    @JsonProperty("merchant_id")
    private Long merchantId;

}
