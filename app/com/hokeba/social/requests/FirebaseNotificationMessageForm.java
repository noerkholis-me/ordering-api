package com.hokeba.social.requests;

/**
 * Created by hendriksaragih on 7/25/17.
 */
public class FirebaseNotificationMessageForm {
    private String message;
    private String title;

    public FirebaseNotificationMessageForm(){

    }

    public FirebaseNotificationMessageForm(String message, String title){
        this.message = message;
        this.title = title;
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
}
