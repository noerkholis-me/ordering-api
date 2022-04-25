package dtos.table;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TableTypeResponse {

    private Long id;
    private String name;
    @JsonProperty("total_person")
    private String totalPerson;

}
