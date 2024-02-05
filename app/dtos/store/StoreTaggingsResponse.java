package dtos.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StoreTaggingsResponse {
    private Long id;
    private String name;
}
