package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@ToString
public class RoleMerchant extends BaseModel {

    @JsonProperty("name")
    @Getter @Setter
    private String name;

    @JsonProperty("key")
    @Getter @Setter
    private String key;

    @JsonProperty("description")
    @Getter @Setter
    private String description;

    @JsonProperty("is_active")
    @Getter @Setter
    @Column(name = "is_active")
    public boolean isActive;

    @JsonProperty("is_cashier")
    @Getter @Setter
    @Column(name = "is_cashier")
    public boolean isCashier;
    
    @JsonProperty("is_kitchen")
    @Getter @Setter
    @Column(name = "is_kitchen")
    public boolean isKitchen;
    
    @JsonProperty("is_waiters")
    @Getter @Setter
    @Column(name = "is_waiters")
    public boolean isWaiters;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    @JsonIgnore
    @Getter @Setter
    public Merchant merchant;

    @OneToMany(mappedBy = "roleMerchant")
    @Getter @Setter
    public List<RoleMerchantFeature> featureList;


    public RoleMerchant(){

    }

    public RoleMerchant(String name, String key, String description, boolean isActive, boolean isCashier, Merchant merchant) {
        this.name = name;
        this.key = key;
        this.description = description;
        this.isActive = isActive;
        this.merchant = merchant;
        this.isCashier = isCashier;
    }

    public RoleMerchant(String name, String key, String description, boolean isActive, boolean isCashier, Merchant merchant, boolean isKitchen) {
        this.name = name;
        this.key = key;
        this.description = description;
        this.isActive = isActive;
        this.merchant = merchant;
        this.isCashier = isCashier;
        this.isKitchen = isKitchen;
    }

    public RoleMerchant(String name, String key, String description, boolean isActive, Merchant merchant, List<RoleMerchantFeature> featureList) {
        this.name = name;
        this.key = key;
        this.description = description;
        this.isActive = isActive;
        this.merchant = merchant;
        this.featureList = featureList;
    }

    public static Finder<Long, RoleMerchant> find = new Finder<>(Long.class, RoleMerchant.class);
}