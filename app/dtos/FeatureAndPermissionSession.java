package dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FeatureAndPermissionSession {

    @JsonProperty("feature_name")
    private String featureName;
    @JsonProperty("is_view")
    private Boolean isView;
    @JsonProperty("is_add")
    private Boolean isAdd;
    @JsonProperty("is_edit")
    private Boolean isEdit;
    @JsonProperty("is_delete")
    private Boolean isDelete;

}
