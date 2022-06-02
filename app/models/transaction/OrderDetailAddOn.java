package models.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import models.merchant.ProductMerchant;
import models.productaddon.ProductAddOn;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_detail_add_on")
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderDetailAddOn extends BaseModel {

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "product_add_on_id", referencedColumnName = "id")
    private ProductAddOn productAddOn;

    @Column(name = "product_assign_id")
    private Long productAssignId;

    @Column(name = "product_name")
    private String productName;

    // set to final price from product store
    @Column(name = "product_price")
    private BigDecimal productPrice;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "sub_total")
    private BigDecimal subTotal;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "order_detail_id", referencedColumnName = "id")
    private OrderDetail orderDetail;

}
