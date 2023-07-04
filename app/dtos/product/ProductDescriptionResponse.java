package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.merchant.ProductMerchantDescription;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductDescriptionResponse {
    @JsonProperty("short_description")
    private String shortDescription;

    @JsonProperty("long_description")
    private String longDescription;

    public ProductDescriptionResponse(ProductMerchantDescription productMerchantDescription) {
        this.setShortDescription(productMerchantDescription.getShortDescription());
        this.setLongDescription(productMerchantDescription.getLongDescription());
    }

}
