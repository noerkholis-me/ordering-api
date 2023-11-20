package dtos.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubCategoryAppResponse {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("subcategory_name")
    public String subcategoryName;

    @JsonProperty("banner")
    public String imageWeb;

    @JsonProperty("icon")
    public String imageMobile;

}
