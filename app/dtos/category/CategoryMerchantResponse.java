package dtos.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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


}
