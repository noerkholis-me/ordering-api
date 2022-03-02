package models;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "cart_additional_detail")
public class CartAdditionalDetail extends BaseModel {
	
	private static final long serialVersionUID = 1L;

    @ManyToOne
    public Product product;
    public String status;
    public Double price;
    public Double discount;

    @JsonIgnore
    @ManyToOne
    public Cart cart;

    public CartAdditionalDetail() {
		super();
	}
    
    public CartAdditionalDetail(Product product, String status) {
		super();
		this.product = product;
		this.status = status;
	}

    public static Finder<Long, CartAdditionalDetail> find = new Finder<Long, CartAdditionalDetail>(Long.class, CartAdditionalDetail.class);

}
