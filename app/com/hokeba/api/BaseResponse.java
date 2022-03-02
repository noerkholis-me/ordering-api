package com.hokeba.api;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@ApiModel(value = "BaseResponse<T>")
public class BaseResponse<T> {
    private MetaInfo meta;
    private String message;
    private Object data;

    public BaseResponse() {
        super();
    }

    public void setBaseResponse(int total, int offset, int limit, String message, Object data) {
        MetaInfo metaInfo = new MetaInfo(total, offset, limit);
        this.meta = metaInfo;
        this.message = message;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MetaInfo getMeta() {
        return meta;
    }

    public void setMeta(int total, int offset, int limit) {
        this.meta = new MetaInfo(total, offset, limit);
    }
}

class MetaInfo {
    private int total;
    private int offset;
    private int limit;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public MetaInfo(int total, int offset, int limit) {
        super();
        this.total = total;
        this.offset = offset;
        this.limit = limit;
    }

}