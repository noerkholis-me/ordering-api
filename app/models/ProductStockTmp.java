package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "product_stock_tmp")
public class ProductStockTmp extends Model {

	@Id
	@JsonProperty("id_tmp")
	public String idTmp;

	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	@JsonIgnore
	public Product product;

	public Long stock;

	@Column(name = "odoo_id")
	public Integer odooId;

	@JsonProperty("approved_status")
	public String approvedStatus;

	@JsonProperty("approved_note")
	public String approvedNote;

	@Column(name = "approved_by")
	@JsonIgnore
	@ManyToOne
	public UserCms approvedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
	public Date approvedAt;

	@Column(name = "user_id")
	@JsonIgnore
	@ManyToOne
	public UserCms user;

	@Temporal(TemporalType.TIMESTAMP)
	@CreatedTimestamp
	@Column(name = "created_at", updatable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
	public Date createdAt;

	@java.beans.Transient
	public String getApproveStatus() {
		String statusName = "";
		switch (approvedStatus){
			case "P" : statusName = "Pending"; break;
			case "A" : statusName = "Approved"; break;
			case "R" : statusName = "Rejected"; break;
		}

		return statusName;
	}

	public static Finder<String, ProductStockTmp> find = new Finder<String, ProductStockTmp>(String.class, ProductStockTmp.class);


	public ProductStockTmp(){

	}

	public ProductStockTmp(Product product, Long stock){
		this(product, stock, null);
	}

	public ProductStockTmp(Product product, Long stock, UserCms user){

		this.idTmp = UUID.randomUUID().toString();
		this.product = product;
		this.stock = stock;
		if (user != null){
			this.user = user;
		}
		this.createdAt = new Date();
		this.approvedStatus = Product.PENDING;
	}

	public static int findRowCount() {
		return
				find.where()
						.eq("approvedStatus", Product.PENDING)
						.findRowCount();
	}

	public static ProductStockTmp approveProduct(String id, UserCms userCms) {
		ProductStockTmp productTmp = null;
		if(id != null){
			productTmp = ProductStockTmp.find.where().eq("idTmp", id).findUnique();
//			productTmp.product.itemCount = productTmp.product.itemCount + productTmp.stock;
			productTmp.product.userCms = productTmp.user;
			productTmp.approvedStatus = Product.AUTHORIZED;
			productTmp.approvedBy = userCms;
			productTmp.approvedAt = new Date();
			productTmp.update();

		}

		return productTmp;
	}

}
