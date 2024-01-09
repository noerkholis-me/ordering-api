package dtos.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;


import javax.persistence.Column;
import java.util.List;

@Data
@Builder


public class DeliverySettingRequest {

    @JsonProperty("store_id")
    private Long store_id;

    @JsonProperty("max_range_delivery")
    public Integer maxRangeDelivery;

    @JsonProperty("km_price_value")
    public Integer kmPriceValue;

    @JsonProperty("enable_flat_price")
    public Boolean enableFlatPrice;

    @JsonProperty("max_range_flat_price")
    public Integer maxRangeFlatPrice;

    @JsonProperty("flat_price_value")
    public Integer flatPriceValue;

    @JsonProperty("calculate_method")
    public String calculateMethod;
}
