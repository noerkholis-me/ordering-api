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
import play.libs.Json;
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
	private static String frontedOrderingURL = Play.application().configuration().getString("whizliz.frontend_ordering.url");
	
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
    public FirebaseRequest buildFirebaseRequest(String to, String title, String message, FirebaseOrderDataRequest order) {
			// Convert FirebaseOrderDataRequest to JSON string
			String bodyJson = "";
			try {
					ObjectMapper objectMapper = new ObjectMapper();
					bodyJson = objectMapper.writeValueAsString(order);
			} catch (IOException e) {
					logger.error("Failed to convert order to JSON: " + e.getMessage());
			}

			// Log the JSON payload for debugging
			System.out.println("Sending JSON payload: " + bodyJson);
			logger.info("Sending JSON payload: " + bodyJson);

			FirebaseNotificationRequest notification = new FirebaseNotificationRequest(message, title);
			return new FirebaseRequest(to, notification, order);
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
				System.out.println("Firebase Response : " + responseString);
				logger.info("Firebase Response : " + responseString);
			} catch (Exception e) {
				System.out.println("Firebase ERROR : " +e.getMessage());
				logger.error("Firebase ERROR : " +e.getMessage());
			} finally {
				client.close();
			}
    }
    
    //TODO custom method
    public void sendFirebaseNotifOrderToStore(Order orderData) {
			System.out.println("Send Firebase Notif Order To Store");
    	try {
				System.out.println("orderData : " + orderData);
				System.out.println("storeCode : " + orderData.getStore().getStoreCode());
    		String storeCode = orderData.getStore().getStoreCode();
    		String title = "Pesanan Baru";
    		String message = "Pesanan baru atas nama " + orderData.getMemberName() + ", dengan kode pesanan " + orderData.getOrderNumber();
    		String to = "store" + storeCode;
				System.out.println("memberName : " + orderData.getMemberName());
				System.out.println("orderNumber : " + orderData.getOrderNumber());
				FirebaseOrderDataRequest data = new FirebaseOrderDataRequest(orderData);
				System.out.println("to : " + to);
        	FirebaseRequest request = buildFirebaseRequest(to, title, message, data);
        	sendPushNotif(request);
    	} catch (Exception e) {
				System.out.println("Firebase ERROR : " +e.getMessage());
    		logger.error("Firebase ERROR : " +e.getMessage());
    	}
    }

		public void sendNotification(String device_token, String title, String body, Order order) throws Exception {
			if (FirebaseApp.getApps().isEmpty()) {
				logger.error("FirebaseApp is not initialized. Cannot send notification.");
				throw new IllegalStateException("FirebaseApp is not initialized.");
		}
			try {
				String storeName = order.getStore().getStoreName();
				String storeNamTemp = storeName.replaceAll("\\s","").toLowerCase() + "-1";
				String orderNumber = order.getOrderNumber();

				String status = order.getStatus();
				switch (status) {
					case "NEW_ORDER":
							status = "baru";
							break;
					case "CANCELED":
							status = "cancel";
							break;
					case "READY_TO_PICKUP":
							status = "ready";
							break;
					case "CLOSED":
							status = "done";
							break;
					default:
							status = status.toLowerCase();
							break;
			}

				String url = frontedOrderingURL + storeNamTemp + "/check-order/detail/" + status + "?order=" + orderNumber;

				System.out.println("url : " + url);

				Notification notification = Notification.builder().setBody(body).setTitle(title).build();

				Message message = Message.builder()
									.setToken(device_token)
									.setNotification(notification)
									.putData("url", url)
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
