package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model.Finder;

@Entity
@Table(name = "diamond_type")
public class DiamondType extends BaseModel {
	
	public String name;
	
	@OneToMany(mappedBy = "diamondType")
	public List<MasterDiamondPrice> masterDiamondPriceList;
	
	@OneToMany(mappedBy = "diamondType")
    public List<MasterDiamondInventory> masterDiamondInventoryList;
	
	@JsonIgnore
	@JoinColumn(name = "user_id")
	@ManyToOne
	public UserCms userCms;
	
	@Transient
	public String save;
	
	public static Finder<Long, DiamondType> find = new Finder<Long, DiamondType>(Long.class, DiamondType.class);

}
