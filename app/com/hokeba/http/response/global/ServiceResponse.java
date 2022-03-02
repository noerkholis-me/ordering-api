package com.hokeba.http.response.global;

/**
 * Created by hendriksaragih on 3/19/17.
 */
public class ServiceResponse {
    private int code;
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ServiceResponse{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
