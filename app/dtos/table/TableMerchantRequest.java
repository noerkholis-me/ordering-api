package dtos.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TableMerchantRequest {

    private String name;
    @JsonProperty("store_id")
    private Long storeId;
    @JsonProperty("table_type_id")
    private Long tableTypeId;
    @JsonProperty("status")
    private Boolean isActive;

}
