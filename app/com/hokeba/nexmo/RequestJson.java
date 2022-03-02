package com.hokeba.nexmo;

/**
 * Created by hendriksaragih on 12/19/16.
 */
public class RequestJson {
    private String api_key;
    private String api_secret;
    private String from;
    private String to;
    private String text;

    public RequestJson(String api_key, String api_secret, String from, String to, String text){
        this.api_key = api_key;
        this.api_secret = api_secret;
        this.from = from;
        this.to = to;
        this.text = text;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getApi_secret() {
        return api_secret;
    }

    public void setApi_secret(String api_secret) {
        this.api_secret = api_secret;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
