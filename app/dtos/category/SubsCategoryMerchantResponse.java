package dtos.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubsCategoryMerchantResponse  {

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

    @JsonProperty("category_id")
    private Long categoryId;
    
    @JsonProperty("subcategory_id")
    private Long subCategoryId;

    @JsonProperty("merchant_id")
    private Long merchantId;


}
