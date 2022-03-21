package dtos.feature;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FeatureResponse {
    private Long id;
    public String name;
    public String key;
    public String section;
    public String description;
    @JsonProperty("is_active")
    public boolean active;
    @JsonProperty("is_merchant")
    public boolean merchant;

}
