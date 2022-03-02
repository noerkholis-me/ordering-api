package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 4/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapProductFastSearch {
    @JsonProperty("name")
    public String name;
    @JsonProperty("id")
    public String id;
    @JsonProperty("slug")
    public String slug;
    @JsonProperty("image_url")
    public String imageUrl;

}