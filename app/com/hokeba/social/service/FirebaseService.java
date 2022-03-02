package com.hokeba.social.service;

import com.hokeba.http.HTTPRequest3;
import com.hokeba.http.Param;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.social.requests.FirebaseNotificationForm;

/**
 * Created by hendriksaragih on 7/25/17.
 */
public class FirebaseService extends HTTPRequest3 {
    private static final String fcmUrl = "https://fcm.googleapis.com/fcm/send";
    private static final String fcmKey = "AAAA9BxW5SU:APA91bHDLAH5A6T0KKWy_8_Y3H_kT8NWC-ClDpOoETnCT8kmGU4v0fKFZzeqxNELS2hXT416pvAZ8hA_hO7xCtCoHhc0HN56OugWQRTffDVtmDO548yMCbVngbF3QAKpdPYm4JessjK6";
    private static FirebaseService instance;

    public static FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }

    public static Param getFcmAuth(){
        return new Param("Authorization", "key="+fcmKey);
    }

    public ServiceResponse sendNotificationTo(String destination, String title, String message){
        return post(fcmUrl, getFcmAuth(), new FirebaseNotificationForm(destination, title, message));
    }
}
