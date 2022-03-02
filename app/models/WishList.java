package models;



import com.avaje.ebean.Ebean;
import com.avaje.ebean.Update;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class WishList extends BaseModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JsonIgnore
	public Member member;
	
	@ManyToOne
	public Product product;
	
	@JsonProperty("stock_history")
	public boolean stockHistory;
	
	@JsonProperty("price_history")
	public Double priceHistory;
	
	@JsonProperty("notification_count")
	public Integer notificationCount;

	
	public static Finder<Long, WishList> find = new Finder<Long,WishList>(Long.class, WishList.class);
	

	public static String validation(Member member,String productDetailSKU) {
		ProductDetail a = ProductDetail.find.where().eq("sku", productDetailSKU).eq("is_deleted",false).findUnique();
		if (a==null) {
			return "Product detail not found.";
		}
		
		return null;
	}
	
	public static void removeBoughtWishlist(Member member, List<Long> productIds) {
		if (productIds != null && !productIds.isEmpty()) {
			Update<WishList> upd = Ebean.createUpdate(WishList.class,
					"UPDATE wish_list SET is_deleted = true WHERE is_deleted = false "
					+ "AND member_id =:memberId AND product_id in (:productId)");
			upd.set("memberId", member.id);
			upd.set("productId", productIds);
			upd.execute();
		}
	}
}
