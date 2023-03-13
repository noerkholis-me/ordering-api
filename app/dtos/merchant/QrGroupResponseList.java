package dtos.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QrGroupResponseList {

    private Long id;
    @JsonProperty("group_name")
    private String groupName;
    @JsonProperty("group_code")
    private String groupCode;
    @JsonProperty("total_store")
    private Integer totalStore;

}
