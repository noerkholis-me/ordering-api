package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapAttribute {
    @JsonProperty("group_attribute_id")
    private Long groupAttributeId;
    @JsonProperty("group_attribute_name")
    private String groupAttributeName;
    @JsonProperty("attribute_id")
    private Long attributeId;
    @JsonProperty("attribute_name")
    private String attributeName;

    public MapAttribute(){

    }

    public MapAttribute(Long groupAttributeId, Long attributeId, String groupAttributeName, String attributeName){
        this.groupAttributeId = groupAttributeId;
        this.attributeId = attributeId;
        this.groupAttributeName = groupAttributeName;
        this.attributeName = attributeName;
    }

    public Long getGroupAttributeId() {
        return groupAttributeId;
    }

    public void setGroupAttributeId(Long groupAttributeId) {
        this.groupAttributeId = groupAttributeId;
    }

    public String getGroupAttributeName() {
        return groupAttributeName;
    }

    public void setGroupAttributeName(String groupAttributeName) {
        this.groupAttributeName = groupAttributeName;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}
