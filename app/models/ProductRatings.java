package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import models.merchant.ProductMerchant;

import java.util.Date;

import javax.persistence.*;

@Table(name = "product_ratings")
@Getter
@Setter
@Entity
public class ProductRatings extends BaseModel {
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    public Store store;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_merchant_id", referencedColumnName = "id")
    private ProductMerchant productMerchant;

    @Column(columnDefinition = "text")
    private String feedback;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date updatedAt;

    private float rate;

}
