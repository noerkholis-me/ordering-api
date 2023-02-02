package models.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import models.ProductStore;
import models.merchant.ProductMerchant;

import javax.persistence.*;

import com.hokeba.util.Helper;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "order_detail")
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderDetail extends BaseModel {

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private ProductMerchant productMerchant;

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

    @Column(name = "is_customizable")
    private Boolean isCustomizable;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @OneToMany(mappedBy = "orderDetail")
    private List<OrderDetailAddOn> orderDetailAddOns;


    public static Finder<Long, OrderDetail> find = new Finder<>(Long.class, OrderDetail.class);

    public String getNamaProduk() {
        return this.productName;
    }

    public List<OrderDetailAddOn> getProductDetailAddOn() {
        return this.orderDetailAddOns;
    }
    
    public String fetchPrice () {
    	return Helper.getRupiahFormat(this.productPrice.doubleValue());
    }

}
