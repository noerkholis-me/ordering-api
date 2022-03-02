package com.hokeba.social.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.core.io.JsonEOFException;
//import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;

public class FirebaseNotification {
	private String message;
    private String title;
    private String type;
    private String sound;
    private String status;
    private String screenMobile;
    private String screenWeb;
    @JsonProperty("click_action")
    private String clickAction;
    private String topic;

    public FirebaseNotification(){

    }

    public FirebaseNotification(String message, String title, String topic, String clickAction, String screenMobile){
        this.message = message;
        this.title = title;
        this.topic = topic;
        this.clickAction = clickAction;
        this.screenMobile = screenMobile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getType() {
		return type;
	}
    
    public void setType(String type) {
		this.type = type;
	}
    
    public String getStatus() {
		return status;
	}
    
    public void setStatus(String status) {
		this.status = status;
	}
    
    public String getSound() {
		return sound;
	}
    
    public void setSound(String sound) {
		this.sound = sound;
	}
    
    public String getTopic() {
		return topic;
	}
    
    public void setTopic(String topic) {
		this.topic = topic;
	}
    
    public String getClickAction() {
		return clickAction;
	}
    
    public void setClickAction(String clickAction) {
		this.clickAction = clickAction;
	}
    
    public String getScreenMobile() {
		return screenMobile;
	}
    
    public void setScreenMobile(String screenMobile) {
		this.screenMobile = screenMobile;
	}
    
    public String getScreenWeb() {
		return screenWeb;
	}
    
    public void setScreenWeb(String screenWeb) {
		this.screenWeb = screenWeb;
	}

}
