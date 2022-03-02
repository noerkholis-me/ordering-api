package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
public class PurchaseOrder extends BaseModel {

	private static final long serialVersionUID = 1L;
	public static final int DRAFT = 0;
	public static final int SENT = 1;
	public static final int APPROVED = 2;
	public static final int RECEIVED = 3;
	public static final int COMPLETED = 4;
	public static final int REJECTED = 5;
	public static final int CANCELED = 6;

	@Column(unique = true)
	public String code;
    public Double total;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "received_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
	public Date receivedAt;

	public int status;

	public String information;

	@OneToMany(mappedBy = "po")
	public List<PurchaseOrderDetail> details;

	//odoo
	@Column(name = "odoo_id")
	public Integer odooId;

	@ManyToOne
	@JsonProperty("merchant_id")
	public Merchant merchant;

	@ManyToOne
	@JsonProperty("vendor_id")
	public Vendor vendor;

	@JsonIgnore
	@JoinColumn(name="user_id")
	@ManyToOne
	public UserCms userCms;

	@Transient
	public String save;

	@Transient
	public String receivedDate;

	@Transient
	public String productName;

	@Transient
	public List<String> qty;

	@Transient
	public List<String> price;

	@Transient
	public List<String> ids;

	@Transient
	public String getStatus() {
		String statusName = "";
		switch (status){
			case 0 : statusName = "Draft";break;
			case 1 : statusName = "Sent";break;
			case 2 : statusName = "Approved";break;
			case 3 : statusName = "Received";break;
			case 4 : statusName = "Completed";break;
			case 5 : statusName = "Rejected";break;
			case 6 : statusName = "Canceled";break;
		}

		return statusName;
	}

	public String generatePOCode(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        PurchaseOrder po = PurchaseOrder.find.where("created_at > '"+simpleDateFormat2.format(new Date())+" 00:00:00'")
                .order("created_at desc").setMaxRows(1).findUnique();
        String seqNum = "";
        if(po == null){
            seqNum = "000001";
        }else{
            seqNum = po.code.substring(po.code.length() - 6);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "000000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 6);
        }
		String code = "PO";
		code += simpleDateFormat.format(new Date()) + seqNum;
		return code;
	}

	public static Finder<Long, PurchaseOrder> find = new Finder<>(Long.class, PurchaseOrder.class);

//	public static void seed(String name, String url, Long id){
//        PurchaseOrder model = new PurchaseOrder();
//		UserCms user = UserCms.find.byId(id);
//        model.name = model.title = model.description = model.keyword = model.imageName = model.imageKeyword =
//                model.imageTitle = model.imageDescription = name;
//        model.slug = CommonFunction.slugGenerate(name);
//        model.imageUrl = url;
//        model.userCms = user;
//        model.save();
//
//        Photo.saveRecord("brd",url, "", "", "", url, user.id, "admin", "Brand", model.id);
//    }
//
//	public static String validation(PurchaseOrder model) {
//		PurchaseOrder uniqueCheck = PurchaseOrder.find.where().eq("slug", model.slug).setMaxRows(1).findUnique();
//		if (model.name.equals("")) {
//			return "Name must not empty.";
//		}
//		if (uniqueCheck != null && model.id == null) {
//			return "Brand with similar name already exist";
//		}
//		if ((model.imageUrl != null) && ((model.imageName == null || model.imageName.equals(""))
//				|| (model.imageTitle == null || model.imageTitle.equals(""))
//				|| (model.imageKeyword == null || model.imageKeyword.equals(""))
//				|| (model.imageDescription == null))) {
//			return "Please describe all information for brand's logo";
//		}
//		return null;
//	}
//
//	public List<ValidationError> validate() {
//		List<ValidationError> errors = new ArrayList<>();
//
//		if (name == null || name.isEmpty()) {
//			errors.add(new ValidationError("name", "Name must not empty."));
//		}
//		if (title == null || title.isEmpty()) {
//			errors.add(new ValidationError("title", "Meta Title must not empty."));
//		}
//		if (description == null || description.isEmpty()) {
//			errors.add(new ValidationError("description", "Meta Description must not empty."));
//		}
//		if (keyword == null || keyword.isEmpty()) {
//			errors.add(new ValidationError("keyword", "Meta Keyword must not empty."));
//		}
//		if (imageName == null || imageName.isEmpty()) {
//			errors.add(new ValidationError("imageName", "Image Name must not empty."));
//		}
//		if (imageTitle == null || imageTitle.isEmpty()) {
//			errors.add(new ValidationError("imageTitle", "Meta Title must not empty."));
//		}
//		if (imageDescription == null || imageDescription.isEmpty()) {
//			errors.add(new ValidationError("imageDescription", "Meta Description must not empty."));
//		}
//		if (imageKeyword == null || imageKeyword.isEmpty()) {
//			errors.add(new ValidationError("imageKeyword", "Meta Keyword must not empty."));
//		}
//
//		if(errors.size() > 0)
//			return errors;
//
//		return null;
//	}

	public static Page<PurchaseOrder> page(int page, int pageSize, String sortBy, String order, String name) {
		ExpressionList<PurchaseOrder> qry = PurchaseOrder.find
				.where()
				.ilike("code", "%" + name + "%")
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

	public void updateStatus(int newStatus) {
//		String oldPromoData = getChangeLogData(this);
		this.status = newStatus;
		super.update();

//		ChangeLog changeLog;
//		changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldPromoData, getChangeLogData(this));
//		changeLog.save();

	}

	public static void seed(Vendor vendor, int status, List<Product> productId, UserCms user){
		PurchaseOrder model = new PurchaseOrder();
		model.code = model.generatePOCode();
		model.status = status;
		model.receivedAt = new Date();
		model.vendor = vendor;
		model.userCms = user;
		model.save();

		Double total = 0D;
		for(Product id : productId){
			PurchaseOrderDetail detail = new PurchaseOrderDetail();
			detail.product = id;
			detail.qty = id.itemCount.intValue();
			detail.po = model;
			detail.price = id.price - (3D/100 * id.price);
			detail.subTotal = detail.price * detail.qty;
			detail.save();
			total += detail.subTotal;
		}

		model.total = total;
		model.update();
	}
}
