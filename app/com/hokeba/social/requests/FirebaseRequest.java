package com.hokeba.social.requests;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;

public class FirebaseRequest {
	private String to;
//  private String condition;
  private FirebaseNotification data;
  private FirebaseNotificationMessage notification;

  public FirebaseRequest(){

  }

  public FirebaseRequest(String to, String title, String message, String topic, String clickAction, String screenMobile){
      this.to = to;
      this.data = new FirebaseNotification(message, title, topic, clickAction, screenMobile);
      this.notification = new FirebaseNotificationMessage(message, title);
  }

  public String getTo() {
      return to;
  }

  public void setTo(String to) {
      this.to = to;
  }

//  public String getCondition() {
//      return condition;
//  }
//
//  public void setCondition(String condition) {
//      this.condition = condition;
//  }

  public FirebaseNotification getData() {
      return data;
  }

  public void setData(FirebaseNotification data) {
      this.data = data;
  }

  public FirebaseNotificationMessage getNotification() {
      return notification;
  }

  public void setNotification(FirebaseNotificationMessage notification) {
      this.notification = notification;
  }

}
