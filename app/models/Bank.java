package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;

import javax.persistence.*;
import java.util.List;

@Entity
public class Bank extends BaseModel {

	private static final long serialVersionUID = 1L;
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;

	@JsonProperty("bank_name")
	public String bankName;
	@JsonProperty("account_name")
	public String accountName;
	@JsonProperty("account_number")
	public String accountNumber;
	public String description;
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
	public boolean status;
	//odoo
	@Column(name = "odoo_id")
	public Integer odooId;
	@Column(name = "partner_bank_id")
	public Integer partnerBankId;
	@Column(name = "account_journal_id")
	public Integer accoountJournalId;
	@Transient
	public String save;

	@Transient
	public String imageLink;

	@Transient
	public String getStatus() {
		String statusName = "";
		if(status)
			statusName = "Active";
		else statusName = "Inactive";

		return statusName;
	}

	@JsonIgnore
	@JoinColumn(name = "user_id")
	@ManyToOne
	public UserCms userCms;

	public String getImageLink(){
		return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
	}

	public String getImageUrl(){
		return getImageLink();
	}

	public static Finder<Long, Bank> find = new Finder<>(Long.class, Bank.class);

	public static void seed(String name, String accountName, String accountNumber, String description, Long id, String imageUrl){
        Bank model = new Bank();
		UserCms user = UserCms.find.byId(id);
		model.imageDescription = model.bankName = model.imageName = model.imageKeyword = model.imageTitle = name;
		model.accountName = accountName;
		model.accountNumber= accountNumber;
		model.description= description;
        model.userCms = user;
        model.imageUrl = imageUrl;
		model.status = true;
        model.save();
    }

	public static Page<Bank> page(int page, int pageSize, String sortBy, String order, String name, Integer filter) {
		ExpressionList<Bank> qry = Bank.find
				.where()
				.ilike("accountName", "%" + name + "%")
				.eq("is_deleted", false);

		switch (filter){
			case 1: qry.eq("status", true);
				break;
			case 2: qry.eq("status", false);
				break;
		}

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

	public static List<Bank> getHomePage() {
		return Bank.find.where()
				.eq("is_deleted", false)
				.eq("status", true)
				.setMaxRows(10).findList();
	}

	public void updateStatus(String newStatus) {
//		String oldBannerData = getChangeLogData(this);
//
		if(newStatus.equals("active"))
			status = Bank.ACTIVE;
		else if(newStatus.equals("inactive"))
			status = Bank.INACTIVE;
//
		super.update();
//
//		ChangeLog changeLog;
//		changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldBannerData, getChangeLogData(this));
//		changeLog.save();

	}
}
