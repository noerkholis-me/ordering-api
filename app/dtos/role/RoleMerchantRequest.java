package dtos.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dtos.FeatureAndPermissionSession;
import dtos.feature.FeatureAssignRequest;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoleMerchantRequest {

    @JsonProperty("name")
    private String name;
    @JsonProperty("key")
    private String key;
    @JsonProperty("description")
    private String description;
    @JsonProperty("is_cashier")
    private Boolean isCashier;
    @JsonProperty("is_kitchen")
    private Boolean isKitchen = false;
    @JsonProperty("is_waiters")
    private Boolean isWaiters = false;
    private List<FeatureAssignRequest> features;
}
