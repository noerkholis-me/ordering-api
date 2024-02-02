package dtos.feature;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeatureRequest {

    public String name;

    public String key;

    public String section;

    public String description;

    @JsonProperty("is_active")
    public boolean active;

    @JsonProperty("is_merchant")
    public boolean merchant;

}
