package dtos.shipper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RateType {
    @JsonProperty("rate_id")
    private Long rateId;
    @JsonProperty("show_id")
    private Long showId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("rate_name")
    private String rateName;
    @JsonProperty("stop_origin")
    private Integer stopOrigin;
    @JsonProperty("stop_destination")
    private Integer stopDestination;
    @JsonProperty("logo_url")
    private String logoUrl;
    @JsonProperty("weight")
    private Double weight;
    @JsonProperty("volumeWeight")
    private Double volumeWeight;
    @JsonProperty("logistic_id")
    private Long logisticId;
    @JsonProperty("finalWeight")
    private Double finalWeight;
    @JsonProperty("item_price")
    private Double itemPrice;
    @JsonProperty("finalRate")
    private Double finalRate;
    @JsonProperty("insuranceRate")
    private Double insuranceRate;
    @JsonProperty("compulsory_insurance")
    private Double compulsoryInsurance;
    @JsonProperty("liability")
    private Double liability;
    @JsonProperty("discount")
    private Double discount;
    @JsonProperty("min_day")
    private Integer minDay;
    @JsonProperty("max_day")
    private Integer maxDay;
    @JsonProperty("pickup_agent")
    private Long pickupAgent;
    @JsonProperty("is_hubless")
    private Boolean isHubless;
    @JsonProperty("description")
    private String description;
}
