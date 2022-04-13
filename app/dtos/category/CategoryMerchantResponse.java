package dtos.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryMerchantResponse  {

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
    
    private List<SubCategoryMerchant> subCategory;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class SubCategoryMerchant {
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

        private List<SubsCategoryMerchant> subsCategory;

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        @Builder
        public static class SubsCategoryMerchant {

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
        }
    }

}
