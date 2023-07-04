package dtos.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.CategoryMerchant;
import models.SubCategoryMerchant;
import models.SubsCategoryMerchant;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryMerchantResponse {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("category_name")
    public String categoryName;

    @JsonProperty("image_web")
    public String imageWeb;

    @JsonProperty("image_mobile")
    public String imageMobile;

    @JsonProperty("is_deleted")
    public Boolean isDeleted;

    @JsonProperty("is_active")
    public Boolean isActive;

    @JsonProperty("merchant_id")
    private Long merchantId;

    @JsonProperty("total_product")
    private Integer totalProduct;

    private List<SubCategoryMerchantResponse> subCategory;

    public CategoryMerchantResponse(CategoryMerchant category) {
        this.setId(category.id);
        this.setCategoryName(category.getCategoryName());
        this.setImageWeb(category.getImageWeb());
        this.setImageMobile(category.getImageMobile());
        this.setIsDeleted(category.isDeleted);
        this.setIsActive(category.getIsActive());
        this.setMerchantId(category.getMerchant() == null ? null : category.getMerchant().id);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class SubCategoryMerchantResponse {
        private Long id;

        @JsonProperty("subcategory_name")
        private String subcategoryName;

        @JsonProperty("image_web")
        private String imageWeb;

        @JsonProperty("image_mobile")
        private String imageMobile;

        @JsonProperty("is_deleted")
        private Boolean isDeleted;

        @JsonProperty("is_active")
        private Boolean isActive;

        @JsonProperty("total_product")
        private Integer totalProduct;

        private List<SubsCategoryMerchantResponse> subsCategory;

        public SubCategoryMerchantResponse(SubCategoryMerchant subCategory) {
            this.setId(subCategory.id);
            this.setSubcategoryName(subCategory.getSubcategoryName());
            this.setImageWeb(subCategory.getImageWeb());
            this.setImageMobile(subCategory.getImageMobile());
            this.setIsActive(subCategory.getIsActive());
            this.setIsDeleted(subCategory.isDeleted);
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        @Builder
        public static class SubsCategoryMerchantResponse {

            @JsonProperty("id")
            public Long id;

            @JsonProperty("subscategory_name")
            public String subscategoryName;

            @JsonProperty("image_web")
            public String imageWeb;

            @JsonProperty("image_mobile")
            public String imageMobile;

            @JsonProperty("is_deleted")
            public Boolean isDeleted;

            @JsonProperty("is_active")
            public Boolean isActive;

            @JsonProperty("sequence")
            public int sequence;

            @JsonProperty("total_product")
            private Integer totalProduct;

            public SubsCategoryMerchantResponse(SubsCategoryMerchant subsCategory) {
                this.setId(subsCategory.id);
                this.setSubscategoryName(subsCategory.getSubscategoryName());
                this.setImageWeb(subsCategory.getImageWeb());
                this.setImageMobile(subsCategory.getImageMobile());
                this.setIsActive(subsCategory.getIsActive());
                this.setIsDeleted(subsCategory.isDeleted);
                this.setSequence(subsCategory.getSequence());
            }
        }
    }

}
