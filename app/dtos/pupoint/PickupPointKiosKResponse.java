package dtos.pupoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PickupPointKiosKResponse {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("image_pup")
    public String imagePUPSetup;
    
    @JsonProperty("store_id")
    public Long storeId;

    @JsonProperty("merchant_id")
    public Long merchantId;

    private List<PickupPointMerchant> pickUpList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class PickupPointMerchant {

        @JsonProperty("id")
        private Long id;

        @JsonProperty("pupoint_name")
        private String puPointName;
    }

}
