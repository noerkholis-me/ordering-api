package service.firebase;

import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.transaction.Order;
import play.Play;
import service.firebase.request.FirebaseDataRequest;
import service.firebase.request.FirebaseNotificationRequest;
import service.firebase.request.FirebaseRequest;

public class FirebaseService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static String pushNotifUrl = Play.application().configuration().getString("firebase.push-notif.url");
	private static String pushNotifKey = Play.application().configuration().getString("firebase.push-notif.key");
	
	private static FirebaseService instance;
	
	public static FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }
	
	private String getPushNotifAuthorization() {
		return "key=" + pushNotifKey;
	}
	
	private String getPushNotifUrl() {
		return pushNotifUrl;
	}
	
    public String buildFirebaseTopic(String topicName) {
    	return "//topics//" + topicName;
    }
    
    //TODO services method
    public FirebaseRequest buildFirebaseRequest(String to, String title, String message) {
    	FirebaseDataRequest data = new FirebaseDataRequest(message, title);
    	FirebaseNotificationRequest notification = new FirebaseNotificationRequest(message, title);
    	return new FirebaseRequest(to, notification, data);
    }
    
    public void sendPushNotif(FirebaseRequest request) throws Exception {
    	JsonNode jsonRequest = new ObjectMapper().valueToTree(request);
		URIBuilder builder = new URIBuilder(getPushNotifUrl());
		HttpPost firebaseRequest = new HttpPost(builder.build());
		firebaseRequest.setHeader("Authorization", getPushNotifAuthorization());
		firebaseRequest.setHeader("Content-Type", "application/json");
		firebaseRequest.setEntity(new StringEntity(jsonRequest.toString()));
		
		CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
		try {
			client.start();
			Future<HttpResponse> future = client.execute(firebaseRequest, null);
			HttpResponse response = future.get();
	
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, "UTF-8");		
			logger.info("Response : " + responseString);
		} finally {
			client.close();
		}
    }
    
    //TODO custom method
    public void sendFirebaseNotifOrderToStore(Order orderData) {
    	try {
    		String storeCode = orderData.getStore().storeCode;
    		String title = "Pesanan Baru";
    		String message = "Pesanan baru atas nama " + orderData.getMemberName();
    		String to = buildFirebaseTopic("store" + storeCode);
        	FirebaseRequest request = buildFirebaseRequest(to, title, message);
        	sendPushNotif(request);
    	} catch (Exception e) {
    		logger.error(e.getMessage());
    	}
    }
    
}
