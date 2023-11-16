package dtos.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubsCategoryAppResponse {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("subscategory_name")
    public String subscategoryName;

    @JsonProperty("icon")
    public String imageWeb;

    @JsonProperty("banner")
    public String imageMobile;

}
