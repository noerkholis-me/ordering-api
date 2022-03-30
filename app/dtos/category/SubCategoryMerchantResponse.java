package dtos.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubCategoryMerchantResponse  {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("subcategory_name")
    public String subcategoryName;

    @JsonProperty("image_web")
    public String imageWeb;

    @JsonProperty("image_mobile")
    public String imageMobile;

    @JsonProperty("is_deleted")
    public Boolean isDeleted;

    @JsonProperty("is_active")
    public Boolean isActive;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("merchant_id")
    private Long merchantId;


}
