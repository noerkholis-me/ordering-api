package com.hokeba.social.requests;

//import java.io.IOException;
import java.util.List;
//import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.http.HTTPRequest3;
import com.hokeba.http.Param;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.util.Constant;

//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;

import models.Member;
import play.libs.Json;
import play.libs.F.Promise;
//import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequestHolder;
//import play.libs.ws.WSResponse;
import play.libs.ws.WSResponse;

public class FirebaseNotificationHelper extends HTTPRequest3 {
	
private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final WSClient ws = WS.client();
	
	private final String application = "Whizliz-campaign";
	
	private String url = "https://fcm.googleapis.com/fcm/send";
	
	private String auth = "key=AAAA0hQs1dE:APA91bGvV_sUdO4TgNFUbF7Byc7yq3jGp7WtCtjU2MRutLnSjINtZSq0tOTgWNnPCFOfYTHfT8G8myJ8Ath5U14QtwXhY6XZEb5SY9ZxnU8eQhHVT0jTpVdtfAESztXgo2xNzqXWTROo";
	
	private String condition;
	
//	private static FirebaseNotificationHelper instance = null;
//	
//	public static FirebaseNotificationHelper getInstance() {
//        if (instance == null) {
//            instance = new FirebaseNotificationHelper();
//        }
//        return instance;
//    }
	
//	@Inject
//	public FirebaseNotificationHelper(WSClient ws) {
//		this.ws = ws;
//	}
	
	
//	private Member member;
//	
//	private ObjectMapper mapper;
	
//	public Boolean send(String title, String message, FireBaseType type, String email) {
	
	// use for sendToCondition
	// ---------
//	public Boolean sendToCondition(String title, String message, ObjectNode type, String email) {
//		List<Member> member = Member.find.where().eq("email", email).eq("is_deleted", false).findList();
//		if (member == null) {
//			return false;
//		}
//		
//		FirebaseNotification notification = new FirebaseNotification(title, message, application, type);
//		String conditionUse = condition+" && '"+ member.get(1).id + "' in topics";
//		FirebaseRequest request = new FirebaseRequest(conditionUse, notification);
//
//		try {
//			String contentBody = request.toJSON().toString();
//			WSRequestHolder reqTemplate = WS.url(url).setHeader("Authorization", auth).setContentType("application/json");
//			Promise<WSResponse> restTemplate = reqTemplate.post(contentBody);
//
//			System.out.println(restTemplate.get(1).getBody());
//			System.out.println(url);
//			System.out.println(auth);
//			
//			if (restTemplate.get(1).getStatus() == 200 ) {
//				logger.info("push notif successfully sent");
//				return true;
//			} else
//				logger.error("push notig not sent");
//				return false;
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		return false;
//	}
	
	// use sendToTopic
	// ---------------
	public ServiceResponse sendToTopic(String title, String message, ObjectNode type, String topic, String screenMobile) {
		String topicUse = "/topics/" + topic;
		String clickAction = "FLUTTER_NOTIFICATION_CLICK";
		String screenMobileUse = screenMobile;
		FirebaseRequest request = new FirebaseRequest(topicUse, title, message, topicUse, clickAction, screenMobileUse);
		System.out.println("HERE REQ: "+Json.toJson(request));
		return post(url, new Param("Authorization", auth), Json.toJson(request));
		
	}

}
