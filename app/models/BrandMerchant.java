package models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class BrandMerchant extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    @JsonProperty("brand_name")
    public String brandName;

    @Getter @Setter
    @JsonProperty("image_web")
    public String imageWeb;

    @Getter @Setter
    @JsonProperty("image_mobile")
    public String imageMobile;

    @Getter @Setter
    @JsonProperty("icon_web")
    public String iconWeb;

    @Getter @Setter
    @JsonProperty("icon_mobile")
    public String iconMobile;

    @Getter @Setter
    @JsonProperty("brand_type")
    public String brandType;

    @Getter @Setter
    @JsonProperty("brand_description")
    public String brandDescription;

    @Setter @Getter
    @JsonProperty("is_active")
    @Column(name = "is_active")
    public boolean isActive;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="merchant_id", referencedColumnName = "id")
    @Getter @Setter
    public Merchant merchant;
}