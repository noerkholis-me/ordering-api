package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.avaje.ebean.Page;

@Entity
@Table(name = "cart")
public class Cart extends BaseModel {

	private static final long serialVersionUID = 1L;
	public static final String STATUS_IN_CART = "IC";
	public static final String STATUS_TAKEN_OUT = "TO";
	public static final String STATUS_CHECKOUT = "CH";

	@ManyToOne
	public Member member;

	@ManyToOne
	public Product product;

	public Integer quantity;
	public Double price;
	public Double totalPrice;
	public Double discount;
	public String status;
	public String note;
	
	@Transient
	public String action;

	@OneToMany
	public List<CartAdditionalDetail> additionalDetails;

	public Cart() {
		super();
	}

	public Cart(Member member, Product product, Integer quantity, Double price, String status) {
		super();
		this.member = member;
		this.product = product;
		this.quantity = quantity;
		this.price = price;
		this.status = status;
	}

	public static Page<Cart> getCartsByMemberId(Long memberId) {
		return find.where().eq("member.id", memberId).findPagingList(100).getPage(0);
	}

	public static Finder<Long, Cart> find = new Finder<Long, Cart>(Long.class, Cart.class);

}
