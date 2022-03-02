package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wordnik.swagger.annotations.ApiModelProperty;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
@Table(name = "category_loyaltypoint")
public class CategoryLoyalty extends BaseModel {
	public static final int PERCENTAGE = 1;
	public static final int NOMINAL = 2;
	
	@JsonProperty("loyalty_usage_type")
	public int loyaltyUsageType;
	
	@JsonProperty("loyalty_usage_value")
	public float loyaltyUsageValue;
	
	@JsonProperty("max_loyalty_usage_value")
	public float maxLoyaltyUsageValue;
	
	@JsonProperty("cashback_type")
	public int cashbackType;
	
	@JsonProperty("cashback_value")
	public float cashbackValue;
	
	@JsonProperty("max_cashback_value")
	public float maxCashbackValue;
	
	@JsonProperty("cashback_type_referral;")
	public int cashbackTypeReferral;
	
	@JsonProperty("cashback_value_referral")
	public float cashbackValueReferral;
	
	@JsonProperty("max_cashback_value_referral")
	public float maxCashbackValueReferral;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "category_id")
	public Category category;
	
	@Transient
	@JsonGetter("category_name")
	public String getCategoryName() {
		return category.name;
	}

    public static Finder<Long, CategoryLoyalty> find = new Finder<Long, CategoryLoyalty>(Long.class, CategoryLoyalty.class);
    
    public CategoryLoyalty() {
    	super();
    	loyaltyUsageType = CategoryLoyalty.PERCENTAGE;
    	cashbackType = CategoryLoyalty.PERCENTAGE;
    	loyaltyUsageValue = 0;
    	maxLoyaltyUsageValue = 0;
    	cashbackValue = 0;
    	maxCashbackValue = 0;
//    }
    
//    public CategoryLoyalty(int loyaltyUsageType, int cashbackType, float loyaltyUsageValue, 
//    		float maxLoyaltyUsageValue, float cashbackValue, float maxCashbackValue) {
    	cashbackTypeReferral = CategoryLoyalty.PERCENTAGE;
    	cashbackValueReferral = 0;
    	maxCashbackValueReferral = 0;
    }
    
    public CategoryLoyalty(int loyaltyUsageType, int cashbackType, float loyaltyUsageValue, 
    		float maxLoyaltyUsageValue, float cashbackValue, float maxCashbackValue, int cashbackTypeReferral, float cashbackValueReferral, float maxCashbackValueReferral ) {

    	super();
    	this.loyaltyUsageType = loyaltyUsageType;
    	this.cashbackType = cashbackType;
    	this.loyaltyUsageValue = loyaltyUsageValue;
    	this.maxLoyaltyUsageValue = maxLoyaltyUsageValue;
    	this.cashbackValue = cashbackValue;
    	this.maxCashbackValue = maxCashbackValue;
    	this.cashbackTypeReferral = cashbackTypeReferral;
    	this.cashbackValueReferral = cashbackValueReferral;
    	this.maxCashbackValueReferral = maxCashbackValueReferral;
    }
}
