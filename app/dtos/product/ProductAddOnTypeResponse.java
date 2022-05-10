package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import models.*;
import utils.BigDecimalSerialize;

import java.io.Serializable;
import java.math.BigDecimal;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductAddOnTypeResponse implements Serializable {

    private Long id;

    @JsonProperty("product_type")
    private String productType;
    
    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("merchant_id")
    private Long merchantId;

}
