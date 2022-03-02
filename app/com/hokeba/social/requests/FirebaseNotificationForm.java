package com.hokeba.social.requests;

/**
 * Created by hendriksaragih on 7/25/17.
 */
public class FirebaseNotificationForm {
    private String to;
//    private String condition;
    private FirebaseNotificationMessageForm data;
    private FirebaseNotificationMessage notification;

    public FirebaseNotificationForm(){

    }

    public FirebaseNotificationForm(String to, String title, String message){
        this.to = to;
        this.data = new FirebaseNotificationMessageForm(message, title);
        this.notification = new FirebaseNotificationMessage(message, title);
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

//    public String getCondition() {
//        return condition;
//    }
//
//    public void setCondition(String condition) {
//        this.condition = condition;
//    }

    public FirebaseNotificationMessageForm getData() {
        return data;
    }

    public void setData(FirebaseNotificationMessageForm data) {
        this.data = data;
    }

    public FirebaseNotificationMessage getNotification() {
        return notification;
    }

    public void setNotification(FirebaseNotificationMessage notification) {
        this.notification = notification;
    }
}
