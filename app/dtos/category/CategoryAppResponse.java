package dtos.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryAppResponse {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("category_name")
    public String categoryName;

    @JsonProperty("icon")
    public String imageWeb;

    @JsonProperty("banner")
    public String imageMobile;

}
