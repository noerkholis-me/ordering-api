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
import service.firebase.request.FirebaseOrderDataRequest;
import service.firebase.request.FirebaseRequest;
import service.firebase.request.FirebaseMessageRequest;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.AccessToken;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.FirebaseApp; // Import for FirebaseApp
import com.google.firebase.FirebaseOptions;

public class FirebaseService {
    private final static Logger.ALogger logger = Logger.of(FirebaseService.class);
	private static String pushNotifUrl = Play.application().configuration().getString("firebase.push-notif.url");
	private static String pushNotifKey = Play.application().configuration().getString("firebase.push-notif.key");
	private static String pathServiceAccountJson = Play.application().configuration().getString("firebase.path-service-account-json");
	
	private static FirebaseService instance;
	
	private void initializeFirebase() throws IOException {
		if (FirebaseApp.getApps().isEmpty()) {
				FileInputStream serviceAccount = new FileInputStream(pathServiceAccountJson);
				FirebaseOptions options = new FirebaseOptions.Builder()
						.setCredentials(GoogleCredentials.fromStream(serviceAccount))
						.build();
				FirebaseApp.initializeApp(options);
		}
	}

	public static FirebaseService getInstance() {
		if (instance == null) {
				instance = new FirebaseService();
				try {
						instance.initializeFirebase(); // Initialize Firebase here
				} catch (IOException e) {
						logger.error("Failed to initialize Firebase: " + e.getMessage());
				}
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
    public FirebaseRequest buildFirebaseRequest(String to, String title, String message, FirebaseOrderDataRequest data) {
			// FirebaseDataRequest data = new FirebaseDataRequest(message, title);
			FirebaseNotificationRequest notification = new FirebaseNotificationRequest(message, title);
			return new FirebaseRequest(to, notification, data);
    }
    
    public void sendPushNotif(FirebaseRequest request) throws Exception {
			// obtain OAuth 2.0 access token
			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(pathServiceAccountJson))
				.createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));

			AccessToken token = credentials.refreshAccessToken();

			FirebaseMessageRequest messageRequest = new FirebaseMessageRequest();
			messageRequest.setMessage(request);

			System.out.println("Token : " + token.getTokenValue());

    	JsonNode jsonRequest = new ObjectMapper().valueToTree(messageRequest);
			URIBuilder builder = new URIBuilder(getPushNotifUrl());
			HttpPost firebaseRequest = new HttpPost(builder.build());
			firebaseRequest.setHeader("Authorization", "Bearer " + token.getTokenValue());
			firebaseRequest.setHeader("Content-Type", "application/json");
			firebaseRequest.setEntity(new StringEntity(jsonRequest.toString()));
			System.out.println("Firebase Request : " + jsonRequest.toString());
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
    		String storeCode = orderData.getStore().getStoreCode();
    		String title = "Pesanan Baru";
    		String message = "Pesanan baru atas nama " + orderData.getMemberName();
    		String to = "store" + storeCode;
				FirebaseOrderDataRequest data = new FirebaseOrderDataRequest(orderData);
				System.out.println("to : " + to);
        	FirebaseRequest request = buildFirebaseRequest(to, title, message, data);
        	sendPushNotif(request);
    	} catch (Exception e) {
    		logger.error("Firebase ERROR : " +e.getMessage());
    	}
    }

		public void sendNotification(String device_token, String title, String body, FirebaseOrderDataRequest order) throws Exception {
			if (FirebaseApp.getApps().isEmpty()) {
				logger.error("FirebaseApp is not initialized. Cannot send notification.");
				throw new IllegalStateException("FirebaseApp is not initialized.");
		}
			try {
				System.out.println("Sending message: ");


				ObjectMapper ObjectMapper = new ObjectMapper();
				String bodyJson = ObjectMapper.writeValueAsString(order);

				Notification notification = Notification.builder().setBody(body).setTitle(title).build();

				Message message = Message.builder()
									.setToken(device_token)
									.setNotification(notification)
									.putData("body", bodyJson)
									.build();

				System.out.println("Sending message: " + message);


				// Send the message and return the response
				FirebaseMessaging.getInstance().send(message);
			} catch (Exception e) {
				// TODO: handle exception
				logger.error("Firebase ERROR : " + e.getMessage());
				System.out.println("Firebase ERROR : " +e.toString());
			}
		}
}
