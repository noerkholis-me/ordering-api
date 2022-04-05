package dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ImageRequest {

    @JsonProperty("image_url_1")
    private String imageUrl1;
    @JsonProperty("image_url_2")
    private String imageUrl2;
    @JsonProperty("image_url_3")
    private String imageUrl3;
    @JsonProperty("image_url_4")
    private String imageUrl4;
    @JsonProperty("image_url_5")
    private String imageUrl5;


}
