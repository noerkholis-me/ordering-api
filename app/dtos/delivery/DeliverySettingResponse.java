package dtos.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import models.Store;
import models.delivery.DeliverySetting;
import utils.BigDecimalSerialize;

import java.math.BigDecimal;

@Data
public class DeliverySettingResponse {

    private Long id;

    @JsonProperty("delivery_method")
    private String deliveryMethod;

    @JsonProperty("normal_price")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal normalPrice;

    @JsonProperty("normal_price_max_range")
    private Integer normalPriceMaxRange;

    @JsonProperty("basic_price")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal basicPrice;

    @JsonProperty("basic_price_max_range")
    private Integer basicPriceMaxRange;

    @JsonProperty("is_active_base_price")
    private Boolean isActiveBasePrice;

    @JsonProperty("is_shipper")
    private Boolean isShipper;

    @JsonProperty("store_id")
    private Long storeId;

    @JsonProperty("store_name")
    private String storeName;

    public DeliverySettingResponse(DeliverySetting data) {
        this.setId(data.id);
        this.setDeliveryMethod(data.getDeliveryMethod().equalsIgnoreCase("KILOMETER") ? "Tarif Per KM" : "Tarif Flat");
        this.setNormalPrice(data.getNormalPrice());
        this.setNormalPriceMaxRange(data.getNormalPriceMaxRange());
        this.setBasicPrice(data.getBasicPrice());
        this.setBasicPriceMaxRange(data.getBasicPriceMaxRange());
        this.setIsActiveBasePrice(data.getIsActiveBasePrice());
        this.setIsShipper(data.getIsShipper());
        this.setStoreId(data.getStore().id);
        this.setStoreName(data.getStore().getStoreName());
    }

}