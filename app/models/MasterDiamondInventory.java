package models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import play.db.ebean.Model.Finder;

@Entity
@Table(name = "master_diamond_inventory")
public class MasterDiamondInventory extends BaseModel {
	
	@JsonProperty("size_in_carat")
	public Float sizeInCarat;
	public String clarity;
	public String color;
	@JsonProperty("quantity_in_stock")
	public Long quantityInStock;
	
	@JsonIgnore
	@JoinColumn(name = "user_id")
	@ManyToOne
	public UserCms userCms;
	
	@JsonIgnore
	@JoinColumn(name = "master_diamond_price_id")
	@ManyToOne
	public MasterDiamondPrice masterDiamondPrice;
	
	@JsonIgnore
	@JoinColumn(name = "diamond_type_id")
	@ManyToOne
	public DiamondType diamondType;
	
	@Transient
	public String save;
	
	@javax.persistence.Transient
	public Long diamondTypeId;
	
	public static Finder<Long, MasterDiamondInventory> find = new Finder<Long, MasterDiamondInventory>(Long.class, MasterDiamondInventory.class);

}
