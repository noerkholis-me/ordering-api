package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Courier extends BaseModel {

	private static final long serialVersionUID = 1L;
	public static final String DELIVERY_TYPE_PICK_UP_POINT = "P";
	public static final String DELIVERY_TYPE_SENT_TO_HOME = "H";

	public String name;
	public String code;
	public int type;
	public Double divider;
	@Column(name = "delivery_type")
	public String deliveryType;
	@JsonProperty("image_name")
	@Column(name = "image_name", columnDefinition = "TEXT")
	public String imageName;
	@JsonProperty("image_keyword")
	public String imageKeyword;
	@JsonProperty("image_title")
	public String imageTitle;
	@JsonProperty("image_description")
	@Column(name = "image_description", columnDefinition = "TEXT")
	public String imageDescription;
	@JsonProperty("image_url")
	public String imageUrl;


	@OneToMany(mappedBy = "courier")
	@JsonProperty("courier_service")
	public List<CourierService> services;

	@Transient
	public String save;

	@javax.persistence.Transient
	public String imageLink;

	@javax.persistence.Transient
	public List<String> service;

	@javax.persistence.Transient
	public List<Long> detailId;

	@javax.persistence.Transient
	public Long townshipId;

	@javax.persistence.Transient
	public List<String> listname;

	@javax.persistence.Transient
	public List<String> listaddress;

//	@javax.persistence.Transient
//	@JsonProperty("code")
//	public String getCode(){
//		return String.valueOf(id);
//	}

	@JsonIgnore
	@JoinColumn(name = "user_id")
	@ManyToOne
	public UserCms userCms;

	//odoo
	@Column(name = "odoo_id")
	public Integer odooId;

	@Column(name = "product_odoo_id")
	public Integer productOdooId;

	public String getImageUrl(){
		return getImageLink();
	}

	public String getImageLink(){
		return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
	}

	public String getDeliveryType(){
		String result = "";
		switch ((deliveryType == null ? "":deliveryType)){
			case DELIVERY_TYPE_PICK_UP_POINT : result = "Pick Up Point"; break;
			case DELIVERY_TYPE_SENT_TO_HOME : result = "Sent To Home"; break;
		}
		return result;
	}

	public static Finder<Long, Courier> find = new Finder<>(Long.class, Courier.class);

	public static void seed(String name, int type, Double divider, String image, UserCms user, Integer odooId, Integer productOdooId){
        Courier model = new Courier();
		model.name = model.imageName = model.imageDescription = model.imageKeyword = model.imageTitle = name;
		model.type = type;
		model.divider = divider;
        model.userCms = user;
		model.imageUrl = image;
//		model.odooId = odooId;
		model.productOdooId = productOdooId;
        model.save();

		CourierService cs = new CourierService();
		cs.service = "Regular";
		cs.courier = model;
		cs.createdAt = cs.updatedAt = new Date();
		cs.save();

		CourierService cs2 = new CourierService();
		cs2.service = "Express";
		cs2.courier = model;
		cs2.createdAt = cs2.updatedAt = new Date();
		cs2.save();
    }
	
	public static void seed2(String name, String code, String image, UserCms user) {
		Courier model = new Courier();
		model.name = model.imageName = model.imageDescription = model.imageKeyword = model.imageTitle = name;
		model.code = code;
        model.userCms = user;
		model.imageUrl = image;
        model.save();
	}

	public static Page<Courier> page(int page, int pageSize, String sortBy, String order, String name) {
		ExpressionList<Courier> qry = Courier.find
				.where()
				.ilike("name", "%" + name + "%")
				.eq("is_deleted", false);

		return
				qry.orderBy(sortBy + " " + order)
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
