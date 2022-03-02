package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
public class SellerReview extends BaseModel {

	private static final long serialVersionUID = 1L;

	public String title;
	public String comment;
	public int rating;

	@JsonProperty("is_active")
	public boolean isActive;

	@JsonProperty("approved_status")
	public String approvedStatus;

	@Column(name = "approved_by")
	@JsonIgnore
	@ManyToOne
	public UserCms approvedBy;

	@JsonProperty("image_url")
	public String imageUrl;

	@ManyToOne
	@JsonIgnore
	public Member member;

	@ManyToOne
	@JsonIgnore
	public Merchant merchant;

	@ManyToOne
	@JsonIgnore
	public Vendor vendor;

	@Transient
	@JsonProperty("reviewer_name")
	public String getReviewerName(){
		return member!=null ? member.fullName : "";
	}

	@Transient
	@JsonProperty("created_at")
	public Date getCreatedAt(){
		return createdAt;
	}



	@Transient
	public String getStatus() {
		String statusName = "";
		switch (approvedStatus){
			case "P" : statusName = "Pending";break;
			case "R" : statusName = "Rejected";break;
			case "A" : statusName = "Approved";break;
		}

		return statusName;
	}


	public String getImageLink(){
		return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
	}

	public static Finder<Long, SellerReview> find = new Finder<Long, SellerReview>(Long.class, SellerReview.class);

	public static String validation(Member member, String productCode, String title, String comment, Integer rating) {
		Product product = Product.find.where().eq("product_code", productCode).findUnique();
		if (product == null) {
			return "Product not found.";
		}
		if (title.equals("")) {
			return "title must not empty";
		}
		if (comment.equals("")) {
			return "comment must not empty";
		}
		if (!rating.toString().matches("[1-5]")) {
			return "rating must in interval 1-5";
		}
		return null;
	}

	public SellerReview(String title, String comment, int rating, boolean isActive, String approvedStatus, UserCms approvedBy,
                        String imageUrl, Member member) {
		super();
		this.title = title;
		this.comment = comment;
		this.rating = rating;
		this.isActive = isActive;
		this.approvedStatus = approvedStatus;
		this.approvedBy = approvedBy;
		this.imageUrl = imageUrl;
		this.member = member;
	}

	public static List<SellerReview> getReview(String type, Long id){
		return find.where().eq("is_deleted", false).eq(type.toLowerCase()+"_id", id).eq("approved_status", "A").setOrderBy("created_at DESC").findList();
	}

	public static int getJumlah(String type, Long id, int rating){
		return find.where().eq("is_deleted", false).eq(type.toLowerCase()+"_id", id).eq("rating", rating).eq("approved_status", "A").findRowCount();
	}

	public static int getJumlah(String type, Long id){
		return find.where().eq("is_deleted", false).eq(type.toLowerCase()+"_id", id).eq("approved_status", "A").findRowCount();
	}
	public static Float getAverage(String type, Long id){
		Integer jumlah = getJumlah(type, id);
		if (jumlah > 0){
			return (float) (sumRating(type, id) / jumlah);
		}
		return 0f;
	}

	public static Integer sumRating(String type, Long id) {
		Integer sum = 0;
		Set<SellerReview> targets = SellerReview.find.where().eq("is_deleted", false).eq(type.toLowerCase()+"_id", id).eq("approved_status", "A").findSet();

		for (SellerReview review : targets) {
			sum = sum + review.rating;
		}
		return sum;
	}

	public SellerReview() {
		super();
	}



	public static int findRowCount() {
		return
				find.where()
						.eq("is_deleted", false)
						.findRowCount();
	}

	public void updateStatus(String newStatus, UserCms approvedBy) {
//		String oldPromoData = getChangeLogData(this);
		this.approvedBy = approvedBy;
		this.approvedStatus = newStatus;
		super.update();

//		ChangeLog changeLog;
//		changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldPromoData, getChangeLogData(this));
//		changeLog.save();

	}
}
