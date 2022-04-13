package models.transaction;

import lombok.Data;
import models.BaseModel;
import models.merchant.ProductMerchant;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_detail")
@Data
public class OrderDetail extends BaseModel {

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "product_merchant_id", referencedColumnName = "id")
    private ProductMerchant productMerchant;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "product_price")
    private BigDecimal productPrice;

    @Column(name = "qty")
    private Integer qty;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;




}
