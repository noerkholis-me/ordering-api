package dtos.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeliverySettingRequest {

    @JsonProperty("delivery_method")
    private String deliveryMethod;

    @JsonProperty("normal_price")
    private BigDecimal normalPrice;

    @JsonProperty("normal_price_max_range")
    private Integer normalPriceMaxRange;

    @JsonProperty("basic_price")
    private BigDecimal basicPrice;

    @JsonProperty("basic_price_max_range")
    private Integer basicPriceMaxRange;

    @JsonProperty("is_active_base_price")
    private Boolean isActiveBasePrice;

    @JsonProperty("is_shipper")
    private Boolean isShipper;

    @JsonProperty("store_id")
    private Long storeId;

}
