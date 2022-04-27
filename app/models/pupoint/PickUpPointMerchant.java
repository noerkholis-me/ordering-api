package models.pupoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import models.*;

import javax.persistence.*;

@Entity
@Table(name = "pick_up_point_merchant")
public class PickUpPointMerchant extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Getter
    @JsonProperty("id")
    public Long id;

    @Getter @Setter
    @JsonProperty("pupoint_name")
    public String pupointName;

    @Getter @Setter
    @JsonProperty("is_active")
    public Boolean isActive;

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