package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model.Finder;

@Entity
@Table(name = "product_price")
public class ProductPrice extends BaseModel {
	public static final int OVERRIDE_SALEPRICE = 1;
	public static final int OVERRIDE_DISCPERCENTAGE = 2;
	public static final int OVERRIDE_DISCNOMINAL = 3;
	
	public Date startDate;
	public Date endDate;
	public Double salePrice;
	public Boolean isActive;
	public int overrideType;
	public Double discountPercentage;
	public Double discountNominal;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "product_id")
	public Product product;
	
	public String getProductName()
	{
		return product.name;
	}
	
	public long getProductId()
	{
		return product.id;
	}
	
	public double getPrice()
	{
		return product.price;
	}
	
	public double getBuyPrice()
	{
		return product.buyPrice;
	}
	
	public String getStatus()
	{
		return isActive ? "Active" : "Inactive";
	}

    public static Finder<Long, ProductPrice> find = new Finder<Long, ProductPrice>(Long.class, ProductPrice.class);
    
}
