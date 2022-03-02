package com.hokeba.payment.kredivo;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.hokeba.http.HTTPRequest3;
import com.hokeba.http.Param;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.payment.kredivo.request.KredivoRequest;
import com.hokeba.payment.kredivo.request.KredivoRequestPayment;
import com.hokeba.payment.kredivo.request.KredivoRequestUpdate;
import com.hokeba.payment.kredivo.response.KredivoCallbackResponse;
import com.hokeba.payment.kredivo.response.KredivoResponse;

import play.Play;

public class KredivoService extends HTTPRequest3 {
	public static final String PAYMENT_METHOD_KREDIVO = "KREDIVO";
	
	private static final boolean ENV_PRODUCTION = Play.application().configuration().getBoolean("kredivo.to_production", false);
	private static final String SERVER_KEY_SANDBOX = Play.application().configuration().getString("kredivo.serverkey.sandbox", "");
	private static final String SERVER_KEY_PRODUCTION = Play.application().configuration().getString("kredivo.serverkey.production", "");
	private static final String CIPHER_KEY_SANDBOX = Play.application().configuration().getString("kredivo.cipherkey.sandbox", "dc4667c3d3506a50f8ad1e51d7524560");
	private static final String CIPHER_KEY_PRODUCTION = Play.application().configuration().getString("kredivo.cipherkey.production", "12a7517c292566174e8156e17d2526d4");
	
	private static final String KREDIVO_BASE_URL_SANDBOX = "https://sandbox.kredivo.com/kredivo";
	private static final String KREDIVO_BASE_URL_PRODUCTION = "https://api.kredivo.com/kredivo";
	private static final String KREDIVO_CHECKOUT_URL = "/v2/checkout_url";
	private static final String KREDIVO_UPDATE_URL = "/v2/update";
	private static final String KREDIVO_PAYMENT_LIST_URL = "/v2/payments";
	
	private static final String[] KREDIVO_PAYMENT_TYPES = new String[]{"30_days", "3_months", "6_months", "12_months"};
	public static final String KREDIVO_DEFAULT_PAYMENT_TYPE = "30_days";

	public static final String STATUS_OK = "OK";
	public static final String STATUS_ERROR = "ERROR";
	
	public static final String TXN_STATUS_SETTLEMENT = "settlement";
	public static final String TXN_STATUS_PENDING = "pending";
	public static final String TXN_STATUS_DENY = "deny";
	public static final String TXN_STATUS_CANCEL = "cancel";
	public static final String TXN_STATUS_EXPIRE = "expire";
	
	public static final String ITEM_PARENT_TYPE_SELLER = "SELLER";
	public static final String ITEM_PARENT_TYPE_ITEM = "ITEM";
	
	public static final String ITEM_ID_SHIPPING = "shipping";
	public static final String ITEM_ID_DISCOUNT = "discount";
	public static final String ITEM_ID_ADDITIONAL = "additionalfee";
	public static final String ITEM_ID_MIXPAYMENT = "mixpayment";
	
	private static KredivoService instance;
	
	public static KredivoService getInstance() {
		if (instance == null) {
			instance = new KredivoService();
		}
		return instance;
	}
	
	private String buildUrl(String urlRoute) {
		return fetchBaseUrl() + urlRoute;
	}

	private String fetchBaseUrl() {
		return ENV_PRODUCTION ? KREDIVO_BASE_URL_PRODUCTION : KREDIVO_BASE_URL_SANDBOX;
	}
	
	private String fetchServerKey() {
		return ENV_PRODUCTION ? SERVER_KEY_PRODUCTION : SERVER_KEY_SANDBOX;
	}
	
	private String fetchCipherKey() {
		return ENV_PRODUCTION ? CIPHER_KEY_PRODUCTION : CIPHER_KEY_SANDBOX;
	}
	
	
	public ServiceResponse checkout(KredivoRequest request) {
		request.serverKey = fetchServerKey();
		request.paymentType = KREDIVO_DEFAULT_PAYMENT_TYPE;
		return post(buildUrl(KREDIVO_CHECKOUT_URL), new Param("", ""), request);
	}
	
	public ServiceResponse update(String transactionId, String signatureKeyRaw) {
//		KredivoRequestUpdate request = new KredivoRequestUpdate();
//		request.transactionId = transactionId;
//		request.signatureKey = buildSignatureKey();
		String url = buildUrl(KREDIVO_UPDATE_URL);
		url += "?transaction_id=" + transactionId 
			 + "&signature_key=" + signatureKeyRaw;
		return get(url, new Param("", ""));
	}
	
	public ServiceResponse fetchPaymentList(KredivoRequestPayment request) {
		request.serverKey = fetchServerKey();
		return post(buildUrl(KREDIVO_PAYMENT_LIST_URL), new Param("", ""), request);
	}
	
	private KredivoResponse buildKredivoResponse(String status, String message) {
		KredivoResponse response = new KredivoResponse();
		response.status = status;
		response.message = message;
		return response;
	}
	
	public KredivoResponse buildSuccessKredivoResponse(String message) {
		return buildKredivoResponse(STATUS_OK, message);
	}
	
	public KredivoResponse buildFailedKredivoResponse(String message) {
		return buildKredivoResponse(STATUS_ERROR, message);
	}
	
	public String buildSignatureKey(String raw) {
		return encryptKredivo(raw, fetchCipherKey());
	}
	
	public boolean checkSignatureKey(KredivoCallbackResponse notif) throws Exception {
		String signatureKeyDec = URLDecoder.decode(notif.signatureKey, StandardCharsets.UTF_8.name());
		String decrypt = decryptKredivo(signatureKeyDec, fetchCipherKey());
		return notif.fetchSignatureKeyRaw().equals(decrypt);
	}
	
	private String encryptKredivo(String value, String key) {
		String result = null;
		try {
			Cipher ci;
			SecureRandom random = new SecureRandom();
			IvParameterSpec ivspec;
			SecretKeySpec skey;
			int ivSize = 16;
			
			String raw = value;
			byte[] rawByte = raw.getBytes();
			
			//iv
			byte[] iv = new byte[ivSize];
			random.nextBytes(iv);
			final IvParameterSpec ivSpec = new IvParameterSpec(iv);
			
			//skey
			skey = new SecretKeySpec(key.getBytes(), "AES");      
			
			ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
			ci.init(Cipher.ENCRYPT_MODE, skey, ivSpec);
			
			byte[] encrypted = ci.doFinal(rawByte);
//			System.out.println(encrypted.length);
			
			// Combine IV and encrypted part...
			byte[] encryptedIVAndText = new byte[ivSize + encrypted.length];
			System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
			System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.length);
			
			byte[] encoded = Base64.getEncoder().encode(encryptedIVAndText);
		
			result = new String(encoded);
			System.out.println("Encrypted : " + result);
		} catch (Exception e) {
			e.printStackTrace();      
		}
		return result;
	}
	
	private String decryptKredivo(String value, String key) {
		String result = null;
		try {
			Cipher ci;
			SecureRandom random;
			IvParameterSpec ivspec;
			SecretKeySpec skey;
			int ivSize = 16;      

			String enc = value;

			byte[] encrypted = Base64.getDecoder().decode(enc);
			byte[] cipherText = Arrays.copyOfRange(encrypted, ivSize, encrypted.length);
			byte[] ivValue = Arrays.copyOfRange(encrypted, 0, ivSize);

			//iv
			final IvParameterSpec ivSpec = new IvParameterSpec(ivValue);
			skey = new SecretKeySpec(key.getBytes(), "AES");

			//ci
			ci = Cipher.getInstance("AES/CBC/NoPadding");
			ci.init(Cipher.DECRYPT_MODE, skey, ivSpec);
			byte[] dec = ci.doFinal(cipherText);
			String decrypted = new String(dec, "UTF-8");

			// RESULT
			result = decrypted.trim();
			System.out.println("Decrypted : " + result);
		} catch (Exception e) {
			e.printStackTrace();      
		}
		return result;
	}
	
}
