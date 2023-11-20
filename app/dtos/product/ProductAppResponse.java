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

    @com.fasterxml.jackson.annotation.JsonProperty("price")
    public int price;

    @com.fasterxml.jackson.annotation.JsonProperty("rating")
    public double rating;

    @com.fasterxml.jackson.annotation.JsonProperty("discount")
    public double discount;

}

