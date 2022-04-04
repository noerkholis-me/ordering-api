package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateStatusRequest {

    @JsonProperty("status")
    private Boolean isActive;
    @JsonProperty("is_customizable")
    private Boolean isCustomizable;
}
