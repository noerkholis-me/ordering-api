package models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDetailVariance extends BaseModel {
	private static final long serialVersionUID = 1L;
	public static Finder<Long, ProductDetailVariance> find = new Finder<Long, ProductDetailVariance>(Long.class, ProductDetailVariance.class);

	
	public String sku;
	
	@JsonProperty("total_stock")
	public long totalStock;
	@JsonProperty("free_stock")
	public long freeStock;
	@JsonProperty("reserved_stock")
	public long reservedStock;

	public boolean stock;
	@JsonProperty("limited_stock")
	public boolean limitedStock;
	@JsonProperty("stock_counter")
	public boolean stockCounter;
	
	@ManyToOne
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	@JsonIgnore
	public Product mainProduct;
	
	@ManyToOne
	@JoinColumn(name = "color_id", referencedColumnName = "id")
	@JsonIgnore
	public MasterColor color;
	
	@ManyToOne
	@JoinColumn(name = "size_id", referencedColumnName = "id")
	@JsonIgnore
	public Size size;
	
	//GETTER
	@JsonGetter("product_id")
	public Long getProductId() {
		return mainProduct == null ? null : mainProduct.id;
	}
	
	@JsonGetter("color_id")
	public Long getColorId() {
		return color == null ? null : color.id;
	}
	
	@JsonGetter("size_id")
	public Long getSizeId(){
		return size == null ? null : size.id;
	}
	
	@JsonGetter("product_name")
	public String getProductName() {
		return mainProduct == null ? null : mainProduct.name;
	}
	
	@JsonGetter("color_name")
	public String getColorName() {
		return color == null ? null : color.name;
	}
	
	@JsonGetter("size_name")
	public String getSizeName(){
		return size == null ? null : "" + size.eu;
	}
	
	
	public String validate() {
		if (this.mainProduct == null || mainProduct.isDeleted) {
			return "Main product target doesn't exist";
		}
    	if (this.size == null || this.size.isDeleted) {
			return "Size doesn't exist";
		}
		if (this.color == null || this.color.isDeleted) {
			return "Color doesn't exist";
		}
    	ProductDetailVariance duplicateCheck = ProductDetailVariance.find.where()
    			.eq("t0.is_deleted", false).eq("t0.product_id", this.mainProduct.id)
    			.eq("t0.color_id", this.color.id).eq("t0.size_id", this.size.id).setMaxRows(1).findUnique();
    	if (duplicateCheck != null && !duplicateCheck.id.equals(this.id)) {
    		return "Similar product already exist, please choose different variance";
    	}
    	return null;
    }
	
}
