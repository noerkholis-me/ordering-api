package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.mapping.request.MapOrderDetail;

import controllers.users.BagController;
//import com.hokeba.mapping.request.MapOrderDetail;

@Entity
@Table(name = "bag")
public class Bag extends BaseModel {
	
	public static final String BAG_STATUS_IN_BAG = "IB";
	public static final String BAG_STATUS_TAKEN_OUT = "TO";
	public static final String BAG_STATUS_CHECKOUT = "CH";

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="member_id")
    public Member member;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="product_detail_variance_id")
    public ProductDetailVariance productVariance;
    
    @JsonProperty("quantity")
    public Long quantity;

    @JsonProperty("status")
    public String status;
    
//    @JsonIgnore
//    public Product product;
    
    public static Finder<Long, Bag> find = new Finder<Long, Bag>(Long.class, Bag.class);

    public Bag() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    public Bag(Member member, ProductDetailVariance productVariance, Long quantity,String status) {
		super();
		this.member = member;
		this.productVariance = productVariance;
//		this.product = productVariance.mainProduct;
		this.quantity = quantity;
		this.status = status;
	}
    
	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	@JsonIgnore
	public Member getMember() {
		return member;
	}

	@JsonIgnore
	public ProductDetailVariance getProductDetailVariance() {
		return productVariance;
	}
    
//    public Product getProduct() {
//    	return productVariance.mainProduct;
//    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
    
	public static void bagCheckOut(List<MapOrderDetail> map, Member actor) {
		Bag curBag = null;
		for(MapOrderDetail mods: map) {
			curBag = Bag.find.where()
					.eq("id",mods.getBagId())
                    .setMaxRows(1)
                    .findUnique();
			curBag.setStatus(Bag.BAG_STATUS_CHECKOUT);
			curBag.update();
			BagController.deleteMailchimpCartItem(actor, curBag);
		}
	}
}
