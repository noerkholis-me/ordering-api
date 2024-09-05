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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

import controllers.store.StoreAccessController;
import models.transaction.Order;
import play.Logger;
import play.Play;
import service.firebase.request.FirebaseDataRequest;
import service.firebase.request.FirebaseNotificationRequest;
import service.firebase.request.FirebaseRequest;
import service.firebase.request.FirebaseMessageRequest;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.AccessToken;
import java.io.FileInputStream;
import java.util.Collections;

public class FirebaseService {
    private final static Logger.ALogger logger = Logger.of(FirebaseService.class);
	private static String pushNotifUrl = Play.application().configuration().getString("firebase.push-notif.url");
	private static String pushNotifKey = Play.application().configuration().getString("firebase.push-notif.key");
	private static String pathServiceAccountJson = Play.application().configuration().getString("firebase.path-service-account-json");
	
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
    	return "/topics/" + topicName;
    }
    
    //TODO services method
    public FirebaseRequest buildFirebaseRequest(String to, String title, String message) {
			FirebaseDataRequest data = new FirebaseDataRequest(message, title);
			FirebaseNotificationRequest notification = new FirebaseNotificationRequest(message, title);
			return new FirebaseRequest(to, notification);
    }
    
    public void sendPushNotif(FirebaseRequest request) throws Exception {
			// obtain OAuth 2.0 access token
			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(pathServiceAccountJson))
				.createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));

			AccessToken token = credentials.refreshAccessToken();

			FirebaseMessageRequest messageRequest = new FirebaseMessageRequest();
			messageRequest.setMessage(request);

    	JsonNode jsonRequest = new ObjectMapper().valueToTree(messageRequest);
			URIBuilder builder = new URIBuilder(getPushNotifUrl());
			HttpPost firebaseRequest = new HttpPost(builder.build());
			firebaseRequest.setHeader("Authorization", "Bearer " + token.getTokenValue());
			firebaseRequest.setHeader("Content-Type", "application/json");
			firebaseRequest.setEntity(new StringEntity(jsonRequest.toString()));
			logger.info("Firebase Request : " + jsonRequest.toString());
			
			CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
			try {
				client.start();
				Future<HttpResponse> future = client.execute(firebaseRequest, null);
				HttpResponse response = future.get();
		
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");		
				logger.info("Firebase Response : " + responseString);
			} catch (Exception e) {
				logger.error("Firebase ERROR : " +e.getMessage());
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
    		String to = "store" + storeCode;
        	FirebaseRequest request = buildFirebaseRequest(to, title, message);
        	sendPushNotif(request);
    	} catch (Exception e) {
    		logger.error("Firebase ERROR : " +e.getMessage());
    	}
    }
    
}
