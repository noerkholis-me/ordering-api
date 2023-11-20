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

    @JsonProperty("banner")
    public String imageWeb;

    @JsonProperty("icon")
    public String imageMobile;

}
