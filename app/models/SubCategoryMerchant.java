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
public class SubCategoryMerchant extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    @JsonProperty("subcategory_name")
    public String subcategoryName;

    @Getter @Setter
    @JsonProperty("image_web")
    public String imageWeb;

    // @Getter @Setter
    // @JsonProperty("image_name")
    // public String imageName;

    // @Getter @Setter
    // @JsonProperty("meta_title")
    // public String metaTitle;

    // @Getter @Setter
    // @JsonProperty("meta_keyword")
    // public String metaKeyword;

    // @Getter @Setter
    // @JsonProperty("meta_description")
    // public String metaDescription;

    @Getter @Setter
    @JsonProperty("image_mobile")
    public String imageMobile;

    @Setter @Getter
    @JsonProperty("is_active")
    @Column(name = "is_active")
    public boolean isActive;

    @Setter @Getter
    @JsonProperty("is_deleted")
    @Column(name = "is_deleted")
    public boolean isDeleted;

    @JsonIgnore
    @ManyToOne
    @Getter @Setter
    @JoinColumn(name="category_id", referencedColumnName = "id")
    public CategoryMerchant categoryMerchant;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="merchant_id", referencedColumnName = "id")
    @Getter @Setter
    public Merchant merchant;
}