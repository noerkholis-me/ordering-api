package dtos.feature;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FeatureAssignRequest {

    @JsonProperty("feature_id")
    private Long featureId;
    @JsonProperty("feature_name")
    private String featureName;
    @JsonProperty("key")
    private String key;
    @JsonProperty("is_view")
    private Boolean isView;
    @JsonProperty("is_add")
    private Boolean isAdd;
    @JsonProperty("is_edit")
    private Boolean isEdit;
    @JsonProperty("is_delete")
    private Boolean isDelete;

}
