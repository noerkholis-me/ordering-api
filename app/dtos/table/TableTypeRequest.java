package dtos.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TableTypeRequest {

    private String name;

    @JsonProperty("minimum_table_count")
    private Integer minimumTableCount;

    @JsonProperty("maximum_table_count")
    private Integer maximumTableCount;

}
