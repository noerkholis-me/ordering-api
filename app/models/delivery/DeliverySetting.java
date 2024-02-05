package models.delivery;

//import dtos.delivery.DeliverySettingRequest;
import lombok.Getter;
import lombok.Setter;
import models.BaseModel;
import models.Merchant;
import models.Store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class DeliverySetting extends BaseModel {
    @Column(name = "delivery_method")
    private String deliveryMethod;

    @Column(name = "normal_price")
    private BigDecimal normalPrice;

    @Column(name = "normal_price_max_range")
    private Integer normalPriceMaxRange;

    @Column(name = "basic_price")
    private BigDecimal basicPrice;

    @Column(name = "basic_price_max_range")
    private Integer basicPriceMaxRange;

    @Column(name = "is_active_base_price")
    private Boolean isActiveBasePrice;

    @Column(name = "is_shipper")
    private Boolean isShipper;

    @OneToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

//    public DeliverySetting(DeliverySettingRequest req, Merchant merchant, Store store) {
//        this.setDeliveryMethod(req.getDeliveryMethod());
//        this.setNormalPrice(req.getNormalPrice());
//        this.setNormalPriceMaxRange(req.getNormalPriceMaxRange());
//        this.setBasicPrice(req.getBasicPrice());
//        this.setBasicPriceMaxRange(req.getBasicPriceMaxRange());
//        this.setIsActiveBasePrice(req.getIsActiveBasePrice());
//        this.setIsShipper(req.getIsShipper() != null ? req.getIsShipper() : false);
//        this.setStore(store);
//        this.setMerchant(merchant);
//    }
//
//    public void updateDeliverySetting(DeliverySettingRequest req, DeliverySetting deliverySetting, Merchant merchant, Store store) {
//        this.setDeliveryMethod(req.getDeliveryMethod() != null ? req.getDeliveryMethod() : deliverySetting.getDeliveryMethod());
//        this.setNormalPrice(req.getNormalPrice() != null ? req.getNormalPrice() : deliverySetting.getNormalPrice());
//        this.setNormalPriceMaxRange(req.getNormalPriceMaxRange() != null ? req.getNormalPriceMaxRange() : deliverySetting.normalPriceMaxRange);
//        this.setBasicPrice(req.getBasicPrice() != null ? req.getBasicPrice() : deliverySetting.getBasicPrice());
//        this.setBasicPriceMaxRange(req.getBasicPriceMaxRange() != null ? req.getBasicPriceMaxRange() : deliverySetting.getBasicPriceMaxRange());
//        this.setIsActiveBasePrice(req.getIsActiveBasePrice() != null ? req.getIsActiveBasePrice() : deliverySetting.getIsActiveBasePrice());
//        this.setIsShipper(req.getIsShipper() != null ? req.getIsShipper() : false);
//        this.setStore(store);
//        this.setMerchant(merchant);
//    }

}
