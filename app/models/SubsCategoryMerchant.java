package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class SubsCategoryMerchant extends BaseModel {
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    public Long id;

    @JsonProperty("subscategory_name")
    public String subscategoryName;

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

    @JsonProperty("image_mobile")
    public String imageMobile;

    @JsonProperty("is_active")
    @Column(name = "is_active")
    public Boolean isActive;

    @JsonProperty("is_deleted")
    @Column(name = "is_deleted")
    public boolean isDeleted;

    @JsonProperty("sequence")
    public int sequence;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    public CategoryMerchant categoryMerchant;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "subcategory_id", referencedColumnName = "id")
    public SubCategoryMerchant subCategoryMerchant;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;
}
