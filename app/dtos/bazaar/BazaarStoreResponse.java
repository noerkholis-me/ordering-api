package dtos.bazaar;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BazaarStoreResponse {

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    public Long id;

    @com.fasterxml.jackson.annotation.JsonProperty("store_name")
    public String storeName;

    @com.fasterxml.jackson.annotation.JsonProperty("store_address")
    public String storeAddress;

    @com.fasterxml.jackson.annotation.JsonProperty("store_image")
    public String storeImage;

    @com.fasterxml.jackson.annotation.JsonProperty("store_distance")
    public double storeDistance;

    @com.fasterxml.jackson.annotation.JsonProperty("store_rating")
    public Double storeRating;

    @com.fasterxml.jackson.annotation.JsonProperty("merchant_id")
    public Long merchantId;

    @com.fasterxml.jackson.annotation.JsonProperty("slug")
    public String slug;

}
