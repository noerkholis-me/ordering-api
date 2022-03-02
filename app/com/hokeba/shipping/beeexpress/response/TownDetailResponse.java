package com.hokeba.shipping.beeexpress.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 7/28/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class TownDetailResponse {
    private Integer id;
    private String shortcode;
    @JsonProperty("agent_name")
    private String agentName;
    @JsonProperty("agent_address")
    private String agentAddress;
    @JsonProperty("agent_pic")
    private String agentPic;
    private String agentPhone;
    private String latitude;
    private String longitude;
    private String locationCode;
    private String statediv;
    private String entityId;
    private String branchId;
    private String visible;
    private String type;
    private String agavatar;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortcode() {
        return shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getAgentPic() {
        return agentPic;
    }

    public void setAgentPic(String agentPic) {
        this.agentPic = agentPic;
    }

    public String getAgentPhone() {
        return agentPhone;
    }

    public void setAgentPhone(String agentPhone) {
        this.agentPhone = agentPhone;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getStatediv() {
        return statediv;
    }

    public void setStatediv(String statediv) {
        this.statediv = statediv;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAgavatar() {
        return agavatar;
    }

    public void setAgavatar(String agavatar) {
        this.agavatar = agavatar;
    }

    @Override
    public String toString() {
        return "TownDetailResponse{" +
                "id=" + id +
                ", shortcode='" + shortcode + '\'' +
                ", agentName='" + agentName + '\'' +
                ", agentAddress='" + agentAddress + '\'' +
                ", agentPic='" + agentPic + '\'' +
                ", agentPhone='" + agentPhone + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", locationCode='" + locationCode + '\'' +
                ", statediv='" + statediv + '\'' +
                ", entityId='" + entityId + '\'' +
                ", branchId='" + branchId + '\'' +
                ", visible='" + visible + '\'' +
                ", type='" + type + '\'' +
                ", agavatar='" + agavatar + '\'' +
                '}';
    }
}
