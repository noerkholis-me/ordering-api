package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.mapping.response.MapKeyValue;
import com.hokeba.mapping.response.MapPaymentMethod;
import com.hokeba.mapping.response.MapProductRatting;
import com.hokeba.util.Constant;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="vendor")
public class Vendor extends BaseModel{

	@JsonProperty("full_name")
    public String fullName;
    @JsonProperty("code")
	public String code;
	public String name;

	public boolean status;
	public Double rating;
	@JsonProperty("count_rating")
	public int countRating;

	public String address;
	public String phone;
	@JsonProperty("city_name")
	public String cityName;
	@JsonProperty("postal_code")
	public String postalCode;
	public String province;
	public String email;
	public String logo;
	
	//odoo
	@Column(name = "odoo_id")
	public Integer odooId;


	@Column(name = "unpaid_customer")
	public Double unpaidCustomer;
	@Column(name = "unpaid_hokeba")
	public Double unpaidHokeba;
	@Column(name = "paid_hokeba")
	public Double paidHokeba;

	@JsonIgnore
	@JoinColumn(name="user_id")
	@ManyToOne
	public UserCms userCms;

	@javax.persistence.Transient
	@JsonProperty("type")
	public String getType(){
		return "VENDOR";
	}

	@javax.persistence.Transient
	@JsonProperty("seller_reviews")
	public List<SellerReview> getSellerReviews(){
		return SellerReview.getReview("vendor", id);
	}

	@javax.persistence.Transient
	@JsonProperty("couriers")
	public List<Courier> couriers;

	public void setCouriers(){
//		couriers = Courier.find.where().eq("is_deleted", false).findList();
		couriers = Merchant.find.byId(-1L).couriers;
	}

	@javax.persistence.Transient
	@JsonProperty("payment_method")
	public List<MapPaymentMethod> paymentMethods;

	public void setPaymentMethods(){
		paymentMethods = Arrays.asList(new MapPaymentMethod(1L, "Transfer Bank", Constant.getInstance().getImageUrl() + "pm-bank-transfer.png"));
	}

	@javax.persistence.Transient
	@JsonProperty("order_stat")
	public List<MapKeyValue> orderStat = new ArrayList<>();

	public void setOrderStat(){
		orderStat.add(new MapKeyValue("Successful Transactions", "10"));
		orderStat.add(new MapKeyValue("Product Sold", "10"));
		orderStat.add(new MapKeyValue("Shipping Success", "10"));
		orderStat.add(new MapKeyValue("Shipping Failed", "10"));
	}

	@javax.persistence.Transient
	@JsonProperty("rating_stat")
	public MapProductRatting ratingStat;

	public void setRatingStat(){
		ratingStat = new MapProductRatting();
		ratingStat.setAverage(SellerReview.getAverage("vendor", id));
		ratingStat.setBintang1(SellerReview.getJumlah("vendor", id, 1));
		ratingStat.setBintang2(SellerReview.getJumlah("vendor", id, 2));
		ratingStat.setBintang3(SellerReview.getJumlah("vendor", id, 3));
		ratingStat.setBintang4(SellerReview.getJumlah("vendor", id, 4));
		ratingStat.setBintang5(SellerReview.getJumlah("vendor", id, 5));
		ratingStat.setCount(SellerReview.getJumlah("vendor", id));
	}

	public String generateVendorCode(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");

		Vendor vendor = Vendor.find.where().ilike("code", "VE"+simpleDateFormat.format(new Date())+"%")
				.order("created_at desc").setMaxRows(1).findUnique();
		String seqNum = "";
		if(vendor == null){
			seqNum = "0000001";
		}else{
			seqNum = vendor.code.substring(vendor.code.length() - 7);
			int seq = Integer.parseInt(seqNum)+1;
			seqNum = "0000000" + String.valueOf(seq);
			seqNum = seqNum.substring(seqNum.length() - 7);
		}
		String code = "VE";
		code += simpleDateFormat.format(new Date()) + seqNum;
		return code;
	}

	public Double getUnpaidHokeba(){
		return unpaidHokeba == null ? 0D : unpaidHokeba;
	}

	public Double getUnpaidCustomer(){
		return unpaidCustomer == null ? 0D : unpaidCustomer;
	}

	public Double getPaidHokeba(){
		return paidHokeba == null ? 0D : paidHokeba;
	}

	public String getLogo(){
		return logo==null || logo.isEmpty() ? "http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/shop-icon.png" : Constant.getInstance().getImageUrl() + logo;
	}

	public static Finder<Long, Vendor> find = new Finder<Long, Vendor>(Long.class, Vendor.class);

	public static Page<Vendor> page(int page, int pageSize, String sortBy, String order, String filter) {
		return
				find.where()
						.ilike("name", "%" + filter + "%")
						.eq("is_deleted", false)
						.orderBy(sortBy + " " + order)
						.findPagingList(pageSize)
						.setFetchAhead(false)
						.getPage(page);
	}

	public static int findRowCount() {
		return
				find.where()
						.eq("is_deleted", false)
						.findRowCount();
	}

}
