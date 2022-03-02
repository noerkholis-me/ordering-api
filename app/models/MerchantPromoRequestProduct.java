package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class MerchantPromoRequestProduct extends BaseModel {

	private static final long serialVersionUID = 1L;
	public static final String STATUS_PENDING = "P";
	public static final String STATUS_APPROVED = "A";
	public static final String STATUS_REJECTED = "R";


	@ManyToOne
	@JsonProperty("mpr_id")
	public MerchantPromoRequest request;

	@ManyToOne
	@JsonProperty("product_id")
	public Product product;

	public String status;
	public Integer stock;
	public Double price;

	public String getStatus(){
		String result;
		switch (status){
			case STATUS_PENDING : result = "Pending"; break;
			case STATUS_APPROVED : result = "Approved"; break;
			case STATUS_REJECTED : result = "Rejected"; break;
			default: result = "";
		}

		return  result;
	}

	@javax.persistence.Transient
	@JsonProperty("promo_name")
	public String getPromoName(){
		return request.promo.name;
	}

	@javax.persistence.Transient
	@JsonProperty("banner_image")
	public String getBannerImage(){
		return request.promo.getImageLink();
	}

	@javax.persistence.Transient
	@JsonProperty("start_date")
	public String getStartDate(){
		return request.promo.getDateFrom();
	}

	@javax.persistence.Transient
	@JsonProperty("end_date")
	public String getEndDate(){
		return request.promo.getDateTo();
	}

	@javax.persistence.Transient
	@JsonProperty("product_name")
	public String getProductName(){
		return product.name;
	}

	@javax.persistence.Transient
	@JsonProperty("product_image")
	public String getProductImage(){
		return product.getThumbnailUrl();
	}

	public static Finder<Long, MerchantPromoRequestProduct> find = new Finder<>(Long.class, MerchantPromoRequestProduct.class);

	public static Page<MerchantPromoRequestProduct> page(Long id, int page, int pageSize, String sortBy, String order, String name, String filter) {
		ExpressionList<MerchantPromoRequestProduct> qry = MerchantPromoRequestProduct.find.where()
				.ilike("product.name", "%" + name + "%")
				.eq("request.promo.id", id)
				.eq("t0.is_deleted", false);

		switch (filter){
			case STATUS_PENDING: qry.eq("status", STATUS_PENDING);
				break;
			case STATUS_APPROVED: qry.eq("status", STATUS_APPROVED);
				break;
			case STATUS_REJECTED: qry.eq("status", STATUS_REJECTED);
				break;
		}

		return
				qry.orderBy(sortBy + " " + order)
						.findPagingList(pageSize)
						.setFetchAhead(false)
						.getPage(page);
	}

	public static int findRowCount(Long id) {
		return
				find.where()
						.eq("request.promo.id", id)
						.eq("t0.is_deleted", false)
						.findRowCount();
	}

	public static int findRowCountActive(Long id) {
		return
				find.where()
						.eq("request.promo.id", id)
						.eq("t0.status", STATUS_APPROVED)
						.eq("t0.is_deleted", false)
						.findRowCount();
	}

	public static MerchantPromoRequestProduct findProduct(Long id, Long productId) {
		return
				find.where()
						.eq("request.promo.id", id)
						.eq("t0.product_id", productId)
						.eq("t0.is_deleted", false)
						.setMaxRows(1).findUnique();
	}
}
