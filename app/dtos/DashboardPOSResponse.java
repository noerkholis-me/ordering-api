package dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashboardPOSResponse {;
    @JsonProperty("id")
    private Long id;
    @JsonProperty("is_new_order")
    private Boolean isNewOrder;
}
