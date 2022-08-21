package dtos.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class DisplayTableResponse {

    private Long id;
    @JsonProperty("table_name")
    private String tableName;
    @JsonProperty("table_type")
    private String tableType;
    @JsonProperty("minimum_table_count")
    private Integer minimumTableCount;
    @JsonProperty("maximum_table_count")
    private Integer maximumTableCount;
    @JsonProperty("is_available")
    private Boolean isAvailable;

}
