package dtos.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TableMerchantResponse {

    private Long id;
    private String name;
    @JsonProperty("status")
    private Boolean isActive;
    @JsonProperty("store")
    private StoreRes storeResponse;
    @JsonProperty("table_type")
    private TableTypeRes tableTypeResponse;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class StoreRes {
        private Long id;
        private String name;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class TableTypeRes {
        private Long id;
        private String name;
    }





}
