package models.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import models.ProductStore;
import models.merchant.ProductMerchant;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_detail")
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderDetail extends BaseModel {

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "product_store_id", referencedColumnName = "id")
    private ProductStore productStore;

    @Column(name = "product_name")
    private String productName;

    // set to final price from product store
    @Column(name = "product_price")
    private BigDecimal productPrice;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;




}
