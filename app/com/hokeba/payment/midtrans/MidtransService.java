package com.hokeba.payment.midtrans;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.http.HTTPRequest3;
import com.hokeba.http.Param;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.payment.midtrans.request.MainTransaction;
import com.hokeba.payment.midtrans.request.MainTransactionSimple;
import com.hokeba.util.Encryption;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import play.Logger;
import play.Play;
import play.libs.Json;

public class MidtransService extends HTTPRequest3 {
	public static final String PAYMENT_METHOD_MIDTRANS = "MIDTRANS";

	private static final boolean ENV_PRODUCTION = Play.application().configuration().getBoolean("midtrans.to_production", false);
	private static final String serverKey = Play.application().configuration().getString("midtrans.serverkey", "");
	private static final String clientKey = Play.application().configuration().getString("midtrans.clientkey", "");
	private static final String merchantId = Play.application().configuration().getString("midtrans.merchant_id", "");
	
	private static final String midTransKey = "Basic " + Base64.encodeBase64String(
			(Play.application().configuration().getString("midtrans.serverkey", "") + ":").getBytes());
	
	private static final String midTransCheckoutUrl = ENV_PRODUCTION ?  "https://app.midtrans.com/snap/v1/transactions"
													: "https://app.sandbox.midtrans.com/snap/v1/transactions";
	private static final String midTransBaseUrl = ENV_PRODUCTION ? "https://api.midtrans.com/v2" 
													: "https://api.sandbox.midtrans.com/v2";
	private static final String midTransBaseGetUrl = midTransBaseUrl + "/:transactionId";
	private static final String midTransGetStatusRoute =  "/status";
	private static final String midTransApproveTranRoute =  "/approve";
	private static final String midTransCancelTranRoute =  "/cancel";
	private static final String midTransExpireTranRoute =  "/expire";
	private static final String midTransRefundTranRoute =  "/refund";
	
	private static MidtransService instance;
	
	public static MidtransService getInstance() {
		if (instance == null) {
			instance = new MidtransService();
		}
		return instance;
	}
	
	private String getOrderUrl(String transactionId, String command) {
		String result = new String(midTransBaseGetUrl + command);
		return result.replaceFirst(":transactionId", transactionId);
	}
	
	public ServiceResponse checkout(MainTransaction transactionData) {
//		return post(midTransCheckoutUrl, new Param("Authorization", midTransKey), transactionData);
//		return checkoutCurl(transactionData);
		return checkoutOkHttp(transactionData);
	}
	
	public ServiceResponse checkout(MainTransactionSimple transactionData) {
		return checkoutOkHttp(transactionData);
	}
	
	private ServiceResponse checkoutOkHttp(MainTransaction transactionData) {
		ServiceResponse response = new ServiceResponse();
		try {
			JsonNode node = Json.toJson(transactionData);
			String bodyRequest = node.toString();
			
			//build request
			OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(bodyRequest, MediaType.get("application/json; charset=utf-8"));
			Request request = new Request.Builder()
				.url(midTransCheckoutUrl)
				.addHeader("Accept", "application/json")
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", midTransKey)
				.post(body)
				.build();
			System.out.println("MIDTRANS CHECKOUT REQUEST : \n" + bodyRequest);
			Response wsResponse = client.newCall(request).execute();
			
			//get response
			int resCode = wsResponse.code();
			String resBody = wsResponse.body().string();
			System.out.println("MIDTRANS CHECKOUT RESPONSE : \n" 
					+ "CODE : " + resCode + "\n" 
					+ "BODY : " + resBody + "\n");
			
			//handling response as json
			if (resBody != null) {
				JsonNode jsonResponse = new ObjectMapper().readValue(resBody, JsonNode.class);
				response.setCode(resCode);
				response.setData(jsonResponse);
			} else {
				response.setCode(408);
				response.setData("We're sorry but something went wrong");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(408);
			response.setData("We're sorry but something went wrong");
		}
		return response;
	}
	
	private ServiceResponse checkoutOkHttp(MainTransactionSimple transactionData) {
		ServiceResponse response = new ServiceResponse();
		try {
			JsonNode node = Json.toJson(transactionData);
			String bodyRequest = node.toString();
			
			//build request
			OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(bodyRequest, MediaType.get("application/json; charset=utf-8"));
			Request request = new Request.Builder()
					.url(midTransCheckoutUrl)
					.addHeader("Accept", "application/json")
					.addHeader("Content-Type", "application/json")
					.addHeader("Authorization", midTransKey)
					.post(body)
					.build();
			System.out.println("MIDTRANS CHECKOUT REQUEST : \n" + bodyRequest);
			Response wsResponse = client.newCall(request).execute();
			
			//get response
			int resCode = wsResponse.code();
			String resBody = wsResponse.body().string();
			System.out.println("MIDTRANS CHECKOUT RESPONSE : \n" 
					+ "CODE : " + resCode + "\n" 
					+ "BODY : " + resBody + "\n");
			
			//handling response as json
			if (resBody != null) {
				JsonNode jsonResponse = new ObjectMapper().readValue(resBody, JsonNode.class);
				response.setCode(resCode);
				response.setData(jsonResponse);
			} else {
				response.setCode(408);
				response.setData("We're sorry but something went wrong");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(408);
			response.setData("We're sorry but something went wrong");
		}
		return response;
	}
	
	private ServiceResponse checkoutCurl(MainTransaction transactionData) {
		ServiceResponse response = new ServiceResponse();
		String responseString = null;
		try {
			JsonNode reqNode = Json.toJson(transactionData); 
			String bodyRequest = reqNode.toString();
			bodyRequest = bodyRequest.replace("<", "(");
			bodyRequest = bodyRequest.replace(">", ")");
			String command = "curl -X POST " 
					+ "'" + midTransCheckoutUrl + "' "
					+ "-H 'Content-Type:application/json' "
					+ "-H 'Accept:application/json' "
					+ "-H 'Authorization:" + midTransKey +"' "
					+ "-d '" + bodyRequest + "'";
			
			System.out.println("MIDTRANS CHECKOUT REQUEST : \n" + command);
			
			Process process = Runtime.getRuntime().exec(command);
			
			StringBuilder output = new StringBuilder();
			StringBuilder outputError = new StringBuilder();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			BufferedReader readerError = new BufferedReader(
					new InputStreamReader(process.getErrorStream()));
			
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			String lineError;
			while ((lineError = readerError.readLine()) != null) {
				outputError.append(lineError + "\n");
			}

			int exitVal = process.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
				System.out.println("MIDTRANS CHECKOUT RESPONSE : \n" + output);
				responseString = output.toString();
			} else {
				System.out.println("ERROR!");
				System.out.println("ERROR RESPONSE : \n" + outputError);
			}
			reader.close();
			readerError.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (responseString != null) {
			try {
				JsonNode jsonResponse = new ObjectMapper().readValue(responseString, JsonNode.class);
				if (jsonResponse.has("error_messages")) {
					response.setCode(400);
					response.setData(jsonResponse);
				} else {
					response.setCode(201);
					response.setData(jsonResponse);
				}
			} catch (Exception e) {
				response.setCode(408);
				response.setData("We're sorry but something went wrong");
				e.printStackTrace();
			}
		} else {
			response.setCode(408);
			response.setData("We're sorry but something went wrong");
		}
		return response;
	}
	
	public ServiceResponse getTransactionStatus(String transactionId) {
		return get(getOrderUrl(transactionId, midTransGetStatusRoute), new Param("Authorization", midTransKey));
	}
	
	public ServiceResponse approveTransaction(String transactionId) {
		return post(getOrderUrl(transactionId, midTransApproveTranRoute), new Param("Authorization", midTransKey), Json.newObject());
	}
	
	public ServiceResponse cancelTransaction(String transactionId) {
		return post(getOrderUrl(transactionId, midTransCancelTranRoute), new Param("Authorization", midTransKey), Json.newObject());
	}
	
	public ServiceResponse expireTransaction(String transactionId) {
//		return post(getOrderUrl(transactionId, midTransExpireTranRoute), new Param("Authorization", midTransKey), Json.newObject());
		return expireTransactionOkHttp(transactionId);
	}
	
	private ServiceResponse expireTransactionOkHttp(String transactionId) {
		ServiceResponse response = new ServiceResponse();
		try {
			JsonNode node = Json.newObject();
			String bodyRequest = node.toString();
			
			//build request
			OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(bodyRequest, MediaType.get("application/json; charset=utf-8"));
			Request request = new Request.Builder()
				.url(getOrderUrl(transactionId, midTransExpireTranRoute))
				.addHeader("Accept", "application/json")
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", midTransKey)
				.post(body)
				.build();
			System.out.println("RECREATING ORDER, FORCE EXPIRE ORDER : " + transactionId);
			Response wsResponse = client.newCall(request).execute();
			
			//get response
			int resCode = wsResponse.code();
			String resBody = wsResponse.body().string();
			System.out.println("MIDTRANS FORCE EXPIRE RESPONSE : \n" 
					+ "CODE : " + resCode + "\n" 
					+ "BODY : " + resBody + "\n");
			
			//handling response as json
			if (resBody != null) {
				JsonNode jsonResponse = new ObjectMapper().readValue(resBody, JsonNode.class);
				response.setCode(resCode);
				response.setData(jsonResponse);
			} else {
				response.setCode(408);
				response.setData("We're sorry but something went wrong");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(408);
			response.setData("We're sorry but something went wrong");
		}
		return response;
	}
	
	public ServiceResponse refundTransaction(String transactionId, long roundedOrderAmount, String reason) {
		ObjectNode reqBody = Json.newObject();
		reqBody.put("amount", Long.toString(roundedOrderAmount));
		reqBody.put("reason", reason);
		return post(getOrderUrl(transactionId, midTransRefundTranRoute), new Param("Authorization", midTransKey), reqBody);
	}
	
	
	
	/**
	 * Method ini digunakan untuk validasi respon dari MidTrans dari field-field yang dikirim oleh MidTrans
	 * 
	 * @param signatureKey
	 * @param orderId
	 * @param statusCode
	 * @param grossAmount
	 * @return hasil validasi benar/tidak-nya respon berasal dari MidTrans
	 */
	public static boolean validateResponse(String signatureKey, String orderId, String statusCode, String grossAmount) {
		String signatureAttempt = createSignatureKey(orderId, statusCode, grossAmount);
		return signatureAttempt == null ? false : signatureAttempt.equals(signatureKey);
	}
	
	/**
	 * Method ini digunakan untuk membuat String signatureKey yang digunakan oleh MidTrans untuk proses validasi
	 * 
	 * @param orderId
	 * @param statusCode
	 * @param grossAmount
	 * @return signatureKey yang digunakan MidTrans untuk proses autentikasi
	 */
	public static String createSignatureKey(String orderId, String statusCode, String grossAmount) {
		try {
			return Encryption.hash(orderId + statusCode + grossAmount + serverKey, Encryption.KEY_SHA512);
		} catch (NoSuchAlgorithmException e) {
			Logger.error("createSignatureKey", e);
		}
		return null;
	}
	
	public static String fetchClientKey() {
		return clientKey;
	}
}
