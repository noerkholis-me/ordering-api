package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductAppResponse {

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public Long id;

    @com.fasterxml.jackson.annotation.JsonProperty("product_name")
    public String productName;

    @com.fasterxml.jackson.annotation.JsonProperty("image")
    public String image;

    @com.fasterxml.jackson.annotation.JsonProperty("base_price")
    public int basePrice;

    @com.fasterxml.jackson.annotation.JsonProperty("price")
    public int price;

    @com.fasterxml.jackson.annotation.JsonProperty("stock")
    public long stock;

    @com.fasterxml.jackson.annotation.JsonProperty("rating")
    public double rating;

    @com.fasterxml.jackson.annotation.JsonProperty("discount")
    public double discount;

    @com.fasterxml.jackson.annotation.JsonProperty("discount_type")
    public String discountType;

    @com.fasterxml.jackson.annotation.JsonProperty("category_merchant_id")
    public Long categoryId;

    @com.fasterxml.jackson.annotation.JsonProperty("sub_category_merchant_id")
    public Long subCategoryId;

    @com.fasterxml.jackson.annotation.JsonProperty("subs_category_merchant_id")
    public Long subsCategoryId;

    @com.fasterxml.jackson.annotation.JsonProperty("brand_merchant_id")
    public Long brandId;

    @com.fasterxml.jackson.annotation.JsonProperty("description")
    public String description;

    @com.fasterxml.jackson.annotation.JsonProperty("long_description")
    public String longDescription;

    @com.fasterxml.jackson.annotation.JsonProperty("is_stock")
    public Boolean isStock;
}

