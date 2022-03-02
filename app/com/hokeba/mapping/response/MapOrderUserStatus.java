package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nugraha on 5/25/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderUserStatus {
    private boolean processing;
    private boolean shipped;
    private boolean completed;

    @JsonProperty("processing_detail")
    private MapOrderUserStatusDetail processingDetail;
    @JsonProperty("shipped_detail")
    private MapOrderUserStatusDetail shippedDetail;
    @JsonProperty("completed_detail")
    private MapOrderUserStatusDetail completedDetail;

    public MapOrderUserStatus(){

    }

    public MapOrderUserStatus(boolean processing, boolean shipped, boolean completed, MapOrderUserStatusDetail processingDetail, MapOrderUserStatusDetail shippedDetail, MapOrderUserStatusDetail completedDetail) {
        this.processing = processing;
        this.shipped = shipped;
        this.completed = completed;
        this.processingDetail = processingDetail;
        this.shippedDetail = shippedDetail;
        this.completedDetail = completedDetail;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public boolean isShipped() {
        return shipped;
    }

    public void setShipped(boolean shipped) {
        this.shipped = shipped;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public MapOrderUserStatusDetail getProcessingDetail() {
        return processingDetail;
    }

    public void setProcessingDetail(MapOrderUserStatusDetail processingDetail) {
        this.processingDetail = processingDetail;
    }

    public MapOrderUserStatusDetail getShippedDetail() {
        return shippedDetail;
    }

    public void setShippedDetail(MapOrderUserStatusDetail shippedDetail) {
        this.shippedDetail = shippedDetail;
    }

    public MapOrderUserStatusDetail getCompletedDetail() {
        return completedDetail;
    }

    public void setCompletedDetail(MapOrderUserStatusDetail completedDetail) {
        this.completedDetail = completedDetail;
    }
}
