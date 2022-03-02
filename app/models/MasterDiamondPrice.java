package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import play.db.ebean.Model.Finder;

@Entity
@Table(name = "master_diamond_price")
public class MasterDiamondPrice extends BaseModel {
	
	@JsonProperty("size_in_carat")
	public String sizeInCarat;
	public String clarity;
	public String color;
	public Double price;
	
	@JsonIgnore
	@JoinColumn(name = "user_id")
	@ManyToOne
	public UserCms userCms;
	
	@JsonIgnore
	@JoinColumn(name = "diamond_type_id")
	@ManyToOne
	public DiamondType diamondType;
	
	@OneToMany(mappedBy = "masterDiamondPrice")
    public List<MasterDiamondInventory> masterDiamondInventoryList;
	
	@Transient
	public String save;
	
	@Transient
	public String f;
	
	@Transient
	public String t;
	
	public static Finder<Long, MasterDiamondPrice> find = new Finder<Long, MasterDiamondPrice>(Long.class, MasterDiamondPrice.class);

}
