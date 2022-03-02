package com.hokeba.social.service;

import com.hokeba.http.HTTPRequest3;
import com.hokeba.http.Param;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.social.requests.MailchimpCartRequest;
import com.hokeba.social.requests.MailchimpCustomerRequest;
import com.hokeba.social.requests.MailchimpOrderRequest;
import com.hokeba.social.requests.MailchimpProductRequest;
import com.hokeba.social.requests.MailchimpProductVariantRequest;
import com.hokeba.social.requests.MailchimpSubscriberRequest;

import assets.Tool;
import play.Logger;
import play.Play;
import play.libs.Json;

public class MailchimpService extends HTTPRequest3 {

	private String url;
	private String apikey;
	private String storeId;
	private String listId;

	private static MailchimpService instance;
	
	public MailchimpService() {
		url = Play.application().configuration().getString("whizliz.social.mailchimp.url");
		apikey = Play.application().configuration().getString("whizliz.social.mailchimp.apikey");
		storeId = Play.application().configuration().getString("whizliz.social.mailchimp.store_id");
		listId = Play.application().configuration().getString("whizliz.social.mailchimp.list_id");
	}
	
	public static MailchimpService getInstance() {
		if (instance == null) {
			instance = new MailchimpService();
		}
		return instance;
	}
	
	public ServiceResponse AddSubscriber(MailchimpSubscriberRequest request) { 
//		Logger.info(Tool.prettyPrint(Json.toJson(request)));
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = post(url.concat("lists/" + listId + "/members"), auth, request, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
	
	public ServiceResponse AddOrUpdateCustomer(MailchimpCustomerRequest request) { 
//		Logger.info(Tool.prettyPrint(Json.toJson(request)));
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = put(url.concat("ecommerce/stores/" + storeId + "/customers/" + request.id), auth, request, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
	
	public ServiceResponse GetProduct(String id) { 
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = get(url.concat("ecommerce/stores/" + storeId + "/products/" + id), auth, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
	
	public static boolean isEnabled() {
		return Play.application().configuration().getBoolean("whizliz.social.mailchimp.enabled");
	}
	
	public ServiceResponse AddProduct(MailchimpProductRequest request) { 
//		Logger.info(Tool.prettyPrint(Json.toJson(request)));
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = post(url.concat("ecommerce/stores/" + storeId + "/products"), auth, request, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
	
	public ServiceResponse UpdateProduct(MailchimpProductRequest request) { 
//		Logger.info(Tool.prettyPrint(Json.toJson(request)));
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = patch(url.concat("ecommerce/stores/" + storeId + "/products/" + request.id), auth, request, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
	
	public ServiceResponse DeleteProduct(String id) { 
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = delete(url.concat("ecommerce/stores/" + storeId + "/products/" + id), auth, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
	
	public ServiceResponse GetProductVariant(String productId, String productVariantId) { 
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = get(url.concat("ecommerce/stores/" + storeId + "/products/" + productId + "/variants/" + productVariantId), auth, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
	
	public ServiceResponse AddOrUpdateProductVariant(MailchimpProductVariantRequest request) { 
//		Logger.info(Tool.prettyPrint(Json.toJson(request)));
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = put(url.concat("ecommerce/stores/" + storeId + "/products/" + request.productId + "/variants/" + request.id), auth, request, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
	
	public ServiceResponse DeleteProductVariant(String productId, String productVariantId) { 
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = delete(url.concat("ecommerce/stores/" + storeId + "/products/" + productId + "/variants/" + productVariantId), auth, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
	
	public ServiceResponse AddOrder(MailchimpOrderRequest request) { 
//		Logger.info(Tool.prettyPrint(Json.toJson(request)));
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = post(url.concat("ecommerce/stores/" + storeId + "/orders"), auth, request, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
	
	public ServiceResponse DeleteOrder(String orderId) { 
//		Logger.info(Tool.prettyPrint(Json.toJson(request)));
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = delete(url.concat("ecommerce/stores/" + storeId + "/orders/" + orderId), auth, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
	
	public ServiceResponse ListCart() {
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = get(url.concat("ecommerce/stores/" + storeId + "/carts"), auth,  new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
	
	public ServiceResponse AddCart(MailchimpCartRequest request) {
//		Logger.info(Tool.prettyPrint(Json.toJson(request)));
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = post(url.concat("ecommerce/stores/" + storeId + "/carts"), auth, request, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
	
	public ServiceResponse GetCart(String cartId) {
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = get(url.concat("ecommerce/stores/" + storeId + "/carts/" + cartId), auth, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}

	public ServiceResponse UpdateCart(MailchimpCartRequest request) {
//		Logger.info(Tool.prettyPrint(Json.toJson(request)));
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = patch(url.concat("ecommerce/stores/" + storeId + "/carts/" + request.id), auth, request, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
	
	public ServiceResponse DeleteCart(String cartId) {
//		Logger.info(Tool.prettyPrint(Json.toJson(request)));
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = delete(url.concat("ecommerce/stores/" + storeId + "/carts/" + cartId), auth, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}

	public ServiceResponse DeleteCartLine(String cartId, String lineId) {
		Param auth = new Param("apikey", apikey);
		ServiceResponse sresponse = delete(url.concat("ecommerce/stores/" + storeId + "/carts/" + cartId + "/lines/" + lineId), auth, new Param());
//		Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
		return sresponse;
	}
}
