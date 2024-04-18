package dtos.bazaar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BazaarStoreResponse {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("store_name")
    public String storeName;

    @JsonProperty("store_address")
    public String storeAddress;

    @JsonProperty("store_image")
    public String storeImage;

    @JsonProperty("store_distance")
    public double storeDistance;

    @JsonProperty("store_rating")
    public Double storeRating;

    @JsonProperty("merchant_id")
    public Long merchantId;

    @JsonProperty("slug")
    public String slug;

    @JsonProperty("status_open_store")
    public Boolean statusOpenStore;

    @JsonProperty("open_at")
    public String openAt;

    @JsonProperty("closed_at")
    public String closedAt;

}
