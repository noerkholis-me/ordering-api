package models.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import models.*;

import javax.persistence.*;

@Entity
public class StoreAccess extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Getter
    @JsonProperty("id")
    public Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_merchant_id", referencedColumnName = "id")
    @Getter @Setter
    public UserMerchant userMerchant;

    @Getter @Setter
    @JsonProperty("is_active")
    public Boolean isActive;

    // @Getter @Setter
    // @JsonProperty("is_deleted")
    // public Boolean isDeleted;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="merchant_id", referencedColumnName = "id")
    @Getter @Setter
    public Merchant merchant;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="store_id", referencedColumnName = "id")
    @Getter @Setter
    public Store store;
}