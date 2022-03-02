package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hendriksaragih on 7/3/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapReturStatus {
    private MapReturStatusDetail processing;
    private MapReturStatusDetail shipped;
    private MapReturStatusDetail completed;

    public MapReturStatus(){

    }

    public MapReturStatus(MapReturStatusDetail processing, MapReturStatusDetail shipped, MapReturStatusDetail completed) {
        this.processing = processing;
        this.shipped = shipped;
        this.completed = completed;
    }

    public MapReturStatusDetail getProcessing() {
        return processing;
    }

    public void setProcessing(MapReturStatusDetail processing) {
        this.processing = processing;
    }

    public MapReturStatusDetail getShipped() {
        return shipped;
    }

    public void setShipped(MapReturStatusDetail shipped) {
        this.shipped = shipped;
    }

    public MapReturStatusDetail getCompleted() {
        return completed;
    }

    public void setCompleted(MapReturStatusDetail completed) {
        this.completed = completed;
    }
}
