package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
@Table(name = "address")
public class Address extends BaseModel {

    public static Integer SHIPPING_ADDRESS = 1;
    public static Integer BILLING_ADDRESS = 2;
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @JsonProperty("is_primary")
    public Boolean isPrimary;
    @JsonProperty("name")
    public String name;
    public Integer type;

    public String phone;
    public String address;
    @ManyToOne
    public District district;
    @ManyToOne
    public Township township;
    @ManyToOne
    public Region region;
    @ManyToOne
    public Village village;
    @JsonProperty("postal_code")
    public String postalCode;

    @Transient
    public String city;
    
    @Transient
    public String districts;

    @Transient
    public String province;

    @Transient
    public String regions;

    @Transient
    public String villages;

    @JsonGetter("city")
    public String getCity(){
        return township == null ? "" : township.name;
    }

    @JsonGetter("city_id")
    public Long getCityId(){
        return township == null ? 0L : township.id;
    }

    @JsonGetter("province")
    public String getProvince(){
        return district == null ? "" : district.name;
    }

    @JsonGetter("province_id")
    public Long getProvinceId(){
        return district == null ? 0L : district.id;
    }
    
    @JsonGetter("districts")
    public String getDistricts(){
        return district == null ? "" : district.name;
    }

    @JsonGetter("district_id")
    public Long getDistrictId(){
        return district == null ? 0L : district.id;
    }

    @JsonGetter("regions")
    public String getRegions(){
        return region == null ? "" : region.name;
    }

    @JsonGetter("region_id")
    public Long getRegionId(){
        return region == null ? 0L : region.id;
    }
    
    @JsonGetter("vilages")
    public String getVilages(){
        return village == null ? "" : village.name;
    }

    @JsonGetter("vilage_id")
    public Long getVilageId(){
        return village == null ? 0L : village.id;
    }

    @ManyToOne
    @JsonBackReference
    public Member member;

    // @JsonIgnore
    // @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    // public List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();

    // @JsonProperty("member_id")
    // private Long memberId;

    public static Finder<Long, Address> find = new Finder<>(Long.class, Address.class);

    public Address(boolean isPrimary, String name, String address, District district, Township township, Integer type,
                   String postalCode, String phone) {
        super();
        this.isPrimary = isPrimary;
        this.address = address;
        this.district = district;
        this.township = township;
        this.type = type;
        this.name = name;
        this.postalCode = postalCode;
        this.phone = phone;
    }

    public static String validation(Address model) {
		if (model.address.equals("")) {
			return "Address must not empty.";
		}
		// if (model.township == null) {
		// return "City must not empty.";
		// }
		if (model.region == null) {
			return "Province must not empty.";
		}
		if (model.district == null) {
			return "City must not empty.";
		}
		if (!model.phone.matches("[0-9]+$")) {
			return "Phone format not valid.";
		}

		return null;
	}

    public static Address getPrimaryAddress(Long user, Integer type){
        return Address.find.where()
                .eq("member_id", user)
                .eq("is_deleted", false)
                .eq("is_primary", true)
                .eq("type", type)
                .setMaxRows(1)
                .findUnique();
    }
}