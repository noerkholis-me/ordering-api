package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.api.BaseResponse;
import com.hokeba.aws.s3.S3Service;
import com.hokeba.http.HTTPRequest3;
import com.hokeba.http.Param;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.payment.midtrans.request.MainTransaction;
import com.hokeba.shipping.rajaongkir.RajaOngkirService;
import com.hokeba.shipping.rajaongkir.mapping.ReqMapWaybill;
import com.hokeba.shipping.rajaongkir.mapping.ResMapRajaOngkir;
import com.hokeba.util.Constant;

import assets.Tool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import views.html.*;
import play.twirl.api.Html;

public class ApplicationController extends Controller {

	public static Result preflight(String all) {
		response().setHeader("Access-Control-Allow-Origin", "*");
		response().setHeader("Allow", "*");
		response().setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, PATCH, DELETE, OPTIONS");
		response().setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent");
		return ok();
	}

	public static Result image(String filename) {
		ByteArrayInputStream input = null;
		byte[] byteArray;

		try {
			File file = new File(Constant.getInstance().getImagePath().concat(filename));
			byteArray = IOUtils.toByteArray(new FileInputStream(file));
			input = new ByteArrayInputStream(byteArray);
			String[] fileType = filename.split("\\.");
			return ok(input).as("image/" + fileType[fileType.length - 1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return notFound();
	}

	public static Result imageByFolder(String folder, String filename) {
		ByteArrayInputStream input = null;
		byte[] byteArray;

		try {
			File file = new File(
					Constant.getInstance().getImagePath().concat(folder).concat(File.separator).concat(filename));
			System.out.println("Nama File : " + file.getAbsolutePath());
			byteArray = IOUtils.toByteArray(new FileInputStream(file));
			input = new ByteArrayInputStream(byteArray);
			String[] fileType = filename.split("\\.");
			return ok(input).as("image/" + fileType[fileType.length - 1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return notFound();
	}

	public static Result imageByFolder2(String folder, String folder2, String filename) {
		ByteArrayInputStream input = null;
		byte[] byteArray;

		try {
			File file = new File(Constant.getInstance().getImagePath().concat(folder).concat(File.separator)
					.concat(folder2).concat(File.separator).concat(filename));
			byteArray = IOUtils.toByteArray(new FileInputStream(file));
			input = new ByteArrayInputStream(byteArray);
			String[] fileType = filename.split("\\.");
			return ok(input).as("image/" + fileType[fileType.length - 1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return notFound();
	}

	public static Result index() {
//		ReqMapWaybill a = new ReqMapWaybill();
//		a.courier="jnt";
//		a.waybill="JP6564051500";
//		RajaOngkirService.getInstance().shipmentTracking(a);
//		RajaOngkirService.getInstance().getCities();
//		return ok(activationMail2.render("Sanji","http://www.google.com","http://localhost:9001/images/mail"));
//		try {
//			String body = "{ \"transaction_details\" : { \"order_id\" : \"HSO200400089\", \"gross_amount\" : 3360946 }, \"item_details\" : [ { \"id\" : \"LASCMJ008RMW-117062\", \"price\" : 3340946, \"quantity\" : 1, \"name\" : \"Callista Gold Rings 01 (Color: --none--) (Size: 15\" }, { \"id\" : \"ME20200000002-tiki\", \"price\" : 20000, \"quantity\" : 1, \"name\" : \"<Shipping>PT LOTUS LINGGA PRATAMA - Citra Van Titi\" } ], \"enabled_payments\" : [ \"credit_card\", \"bca_va\", \"gopay\" ], \"credit_card\" : { \"secure\" : true, \"channel\" : \"migs\", \"installment\" : { \"required\" : false, \"terms\" : { \"bca\" : [ 6, 12 ], \"danamon\" : [ 3, 6, 12 ], \"hsbc\" : [ 6, 12 ] } }, \"whitelist_bins\" : [ ] }, \"gopay\" : { \"enable_callback\" : true, \"callback_url\" : \"whizliz://\" }, \"customer_details\" : { \"first_name\" : null, \"last_name\" : null, \"email\" : \"aldy.ramadhan012@gmail.com\", \"phone\" : null, \"billing_address\" : { \"first_name\" : \"house \", \"last_name\" : null, \"email\" : null, \"phone\" : null, \"address\" : \"wbojong\", \"city\" : \"Bojong\", \"postal_code\" : null, \"country_code\" : \"IDN\" }, \"shipping_address\" : { \"first_name\" : \"house \", \"last_name\" : null, \"email\" : null, \"phone\" : null, \"address\" : \"wbojong\", \"city\" : \"Bojong\", \"postal_code\" : null, \"country_code\" : \"IDN\" } }, \"expiry\" : { \"start_time\" : \"2020-04-09 08:30:24 +0700\", \"unit\" : \"minutes\", \"duration\" : 2879 }, \"custom_field1\" : null, \"custom_field2\" : null, \"custom_field3\" : null }";
//			MainTransaction req = new ObjectMapper().readValue(body, MainTransaction.class);
//			HTTPRequest3 requestContainer = new HTTPRequest3();
//			String url = "https://app.midtrans.com/snap/v1/transactions";
//			String key = "Basic VlQtc2VydmVyLXE0aGwyX1dUSHY5dVF6WGpZZ0NMaWxGbTo=";
//			Param header1 = new Param("cache-control", "no-cache");
//			Param header2 = new Param("postman-token", "845506e6-abf4-182d-b17e-e10b2c4f724c");
//	        Param header3 = new Param("User-Agent",
////	        		"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
//	                "Midtrans-Java-Library");
////			ServiceResponse response = requestContainer.post(url, new Param("Authorization", key), body);
////			ServiceResponse response = requestContainer.get("https://api.whizliz.com/");
//			ServiceResponse response = requestContainer.get("https://app.midtrans.com/");
//			System.out.println("RESP : " + ((String)response.getData()));
			
		try {
//			String body = "{ \"transaction_details\" : { \"order_id\" : \"HSO200400089\", \"gross_amount\" : 3360946 }, \"item_details\" : [ { \"id\" : \"LASCMJ008RMW-117062\", \"price\" : 3340946, \"quantity\" : 1, \"name\" : \"Callista Gold Rings 01 (Color: --none--) (Size: 15\" }, { \"id\" : \"ME20200000002-tiki\", \"price\" : 20000, \"quantity\" : 1, \"name\" : \"<Shipping>PT LOTUS LINGGA PRATAMA - Citra Van Titi\" } ], \"enabled_payments\" : [ \"credit_card\", \"bca_va\", \"gopay\" ], \"credit_card\" : { \"secure\" : true, \"channel\" : \"migs\", \"installment\" : { \"required\" : false, \"terms\" : { \"bca\" : [ 6, 12 ], \"danamon\" : [ 3, 6, 12 ], \"hsbc\" : [ 6, 12 ] } }, \"whitelist_bins\" : [ ] }, \"gopay\" : { \"enable_callback\" : true, \"callback_url\" : \"whizliz://\" }, \"customer_details\" : { \"first_name\" : null, \"last_name\" : null, \"email\" : \"aldy.ramadhan012@gmail.com\", \"phone\" : null, \"billing_address\" : { \"first_name\" : \"house \", \"last_name\" : null, \"email\" : null, \"phone\" : null, \"address\" : \"wbojong\", \"city\" : \"Bojong\", \"postal_code\" : null, \"country_code\" : \"IDN\" }, \"shipping_address\" : { \"first_name\" : \"house \", \"last_name\" : null, \"email\" : null, \"phone\" : null, \"address\" : \"wbojong\", \"city\" : \"Bojong\", \"postal_code\" : null, \"country_code\" : \"IDN\" } }, \"expiry\" : { \"start_time\" : \"2020-04-09 08:30:24 +0700\", \"unit\" : \"minutes\", \"duration\" : 2879 }, \"custom_field1\" : null, \"custom_field2\" : null, \"custom_field3\" : null }";
			//full ^
//			String body = "{ \"transaction_details\" : { \"order_id\" : \"HSO200400089\", \"gross_amount\" : 3360946 }, \"item_details\" : [ { \"id\" : \"LASCMJ008RMW-117062\", \"price\" : 3340946, \"quantity\" : 1, \"name\" : \"Callista Gold Rings 01 (Color: --none--) (Size: 15\" }, { \"id\" : \"ME20200000002-tiki\", \"price\" : 20000, \"quantity\" : 1, \"name\" : \"<Shipping>PT LOTUS LINGGA PRATAMA - Citra Van Titi\" } ]}";
//			//ada <>
//			String body = "{ \"transaction_details\" : { \"order_id\" : \"HSO2004 - 00089\", \"gross_amount\" : 3360946 }}";
			//tidak ada <>
			
			String body = "{\"transaction_details\":{\"order_id\":\"HSO200400023\",\"gross_amount\":49000},\"item_details\":[{\"id\":\"JDD1220200402131253-119539\",\"price\":11000,\"quantity\":1,\"name\":\"Cincin Merchant WGS Herry 002 (Color: White) (Size\"},{\"id\":\"ME20200000012-pos\",\"price\":21000,\"quantity\":1,\"name\":\"(Shipping)Merchant WGS Herry - POS Indonesia\"},{\"id\":\"JDD1620200406101720-119439\",\"price\":9000,\"quantity\":1,\"name\":\"Lino Lab (Color: Rose) (Size: 8)\"},{\"id\":\"ME20200000001-jne\",\"price\":8000,\"quantity\":1,\"name\":\"(Shipping)Whizliz - JNE\"}],\"enabled_payments\":[\"credit_card\",\"bca_va\",\"gopay\"],\"credit_card\":{\"secure\":true,\"channel\":\"migs\",\"installment\":{\"required\":false,\"terms\":{\"bca\":[6,12],\"danamon\":[3,6,12],\"hsbc\":[6,12]}},\"whitelist_bins\":[]},\"gopay\":{\"enable_callback\":true,\"callback_url\":\"whizliz://\"},\"customer_details\":{\"first_name\":\"Heri\",\"last_name\":\"kuswanto Registrasi\",\"email\":\"heri.kkuswanto.qa@gmail.com\",\"phone\":\"+628727819022\",\"billing_address\":{\"first_name\":\"Rumah Paman\",\"last_name\":null,\"email\":null,\"phone\":null,\"address\":\"Jln. M. Toha No.105\",\"city\":\"Batununggal\",\"postal_code\":null,\"country_code\":\"IDN\"},\"shipping_address\":{\"first_name\":\"Rumah Paman\",\"last_name\":null,\"email\":null,\"phone\":null,\"address\":\"Jln. M. Toha No.105\",\"city\":\"Batununggal\",\"postal_code\":null,\"country_code\":\"IDN\"}},\"expiry\":{\"start_time\":\"2020-04-16 08:08:03 +0700\",\"unit\":\"minutes\",\"duration\":2879},\"custom_field1\":null,\"custom_field2\":null,\"custom_field3\":null}";
			
			MainTransaction req = new ObjectMapper().readValue(body, MainTransaction.class);
			JsonNode node = Json.toJson(req); 
			String elasticUrl = "https://app.sandbox.midtrans.com/snap/v1/transactions";
//			String key = "Basic VlQtc2VydmVyLXE0aGwyX1dUSHY5dVF6WGpZZ0NMaWxGbTo=";
			String key = "Basic U0ItTWlkLXNlcnZlci1tS0RsNUZ1dkFYLW9hQkt3cHdPYUZSTmw6";
			String bodyRequest = node.toString();
			bodyRequest = bodyRequest.replace("<", "(");
			bodyRequest = bodyRequest.replace(">", ")");
			
			String command = "curl -X POST " 
					+ "'" + elasticUrl + "' "
					+ "-H 'Content-Type:application/json' "
					+ "-H 'Accept:application/json' "
					+ "-H 'Authorization:" + key +"' "
//					+ "-d '" + body + "'";
					+ "-d '" + bodyRequest + "'";
//					+ "-d '{\"name\" : \"Shipping\"}'";

//			Process process = Runtime.getRuntime().exec(new String[]{"curl", "-X", "POST", elasticUrl,
//					"-H", "Content-Type:application/json", 
//					"-H", "Accept:application/json", 
//					"-H", "Authorization:"+key , 
//					"-d", "'"+node.toString()+"'"});
//			ProcessBuilder processBuilder = new ProcessBuilder("curl", "-X", "POST", elasticUrl,
//					"-H", "Content-Type:application/json", 
//					"-H", "Accept:application/json", 
//					"-H", "Authorization:"+key, 
//					"-d", node.toString());
			
//			Process process = processBuilder.start();
			Process process = Runtime.getRuntime().exec(command);
			
			System.out.println("Command : " + command);
//			System.out.println("Request : " + processBuilder.command().toString());
			
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
				System.out.println(output);
				reader.close();
//					System.exit(0);
			} else {
				//abnormal...
				System.out.println("ERROR!");
				System.out.println(outputError);
				reader.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
//		} catch (Exception e) {
//			
//		}
//		
		return ok("index");
	}

	public static Result index3() {
		try {
			String body = "{ \"transaction_details\" : { \"order_id\" : \"HSO200400089\", \"gross_amount\" : 3360946 }, \"item_details\" : [ { \"id\" : \"LASCMJ008RMW-117062\", \"price\" : 3340946, \"quantity\" : 1, \"name\" : \"Callista Gold Rings 01 (Color: --none--) (Size: 15\" }, { \"id\" : \"ME20200000002-tiki\", \"price\" : 20000, \"quantity\" : 1, \"name\" : \"<Shipping>PT LOTUS LINGGA PRATAMA - Citra Van Titi\" } ], \"enabled_payments\" : [ \"credit_card\", \"bca_va\", \"gopay\" ], \"credit_card\" : { \"secure\" : true, \"channel\" : \"migs\", \"installment\" : { \"required\" : false, \"terms\" : { \"bca\" : [ 6, 12 ], \"danamon\" : [ 3, 6, 12 ], \"hsbc\" : [ 6, 12 ] } }, \"whitelist_bins\" : [ ] }, \"gopay\" : { \"enable_callback\" : true, \"callback_url\" : \"whizliz://\" }, \"customer_details\" : { \"first_name\" : null, \"last_name\" : null, \"email\" : \"aldy.ramadhan012@gmail.com\", \"phone\" : null, \"billing_address\" : { \"first_name\" : \"house \", \"last_name\" : null, \"email\" : null, \"phone\" : null, \"address\" : \"wbojong\", \"city\" : \"Bojong\", \"postal_code\" : null, \"country_code\" : \"IDN\" }, \"shipping_address\" : { \"first_name\" : \"house \", \"last_name\" : null, \"email\" : null, \"phone\" : null, \"address\" : \"wbojong\", \"city\" : \"Bojong\", \"postal_code\" : null, \"country_code\" : \"IDN\" } }, \"expiry\" : { \"start_time\" : \"2020-04-09 08:30:24 +0700\", \"unit\" : \"minutes\", \"duration\" : 2879 }, \"custom_field1\" : null, \"custom_field2\" : null, \"custom_field3\" : null }";
			//full ^
//			String body = "{ \"transaction_details\" : { \"order_id\" : \"HSO200400089\", \"gross_amount\" : 3360946 }, \"item_details\" : [ { \"id\" : \"LASCMJ008RMW-117062\", \"price\" : 3340946, \"quantity\" : 1, \"name\" : \"Callista Gold Rings 01 (Color: --none--) (Size: 15\" }, { \"id\" : \"ME20200000002-tiki\", \"price\" : 20000, \"quantity\" : 1, \"name\" : \"<Shipping>PT LOTUS LINGGA PRATAMA - Citra Van Titi\" } ]}";
//			//ada <>
//			String body = "{ \"transaction_details\" : { \"order_id\" : \"HSO2004 - 00089\", \"gross_amount\" : 3360946 }}";
			//tidak ada <>
			
//			String body = "{\"transaction_details\":{\"order_id\":\"HSO200400023\",\"gross_amount\":49000},\"item_details\":[{\"id\":\"JDD1220200402131253-119539\",\"price\":11000,\"quantity\":1,\"name\":\"Cincin Merchant WGS Herry 002 (Color: White) (Size\"},{\"id\":\"ME20200000012-pos\",\"price\":21000,\"quantity\":1,\"name\":\"(Shipping)Merchant WGS Herry - POS Indonesia\"},{\"id\":\"JDD1620200406101720-119439\",\"price\":9000,\"quantity\":1,\"name\":\"Lino Lab (Color: Rose) (Size: 8)\"},{\"id\":\"ME20200000001-jne\",\"price\":8000,\"quantity\":1,\"name\":\"(Shipping)Whizliz - JNE\"}],\"enabled_payments\":[\"credit_card\",\"bca_va\",\"gopay\"],\"credit_card\":{\"secure\":true,\"channel\":\"migs\",\"installment\":{\"required\":false,\"terms\":{\"bca\":[6,12],\"danamon\":[3,6,12],\"hsbc\":[6,12]}},\"whitelist_bins\":[]},\"gopay\":{\"enable_callback\":true,\"callback_url\":\"whizliz://\"},\"customer_details\":{\"first_name\":\"Heri\",\"last_name\":\"kuswanto Registrasi\",\"email\":\"heri.kkuswanto.qa@gmail.com\",\"phone\":\"+628727819022\",\"billing_address\":{\"first_name\":\"Rumah Paman\",\"last_name\":null,\"email\":null,\"phone\":null,\"address\":\"Jln. M. Toha No.105\",\"city\":\"Batununggal\",\"postal_code\":null,\"country_code\":\"IDN\"},\"shipping_address\":{\"first_name\":\"Rumah Paman\",\"last_name\":null,\"email\":null,\"phone\":null,\"address\":\"Jln. M. Toha No.105\",\"city\":\"Batununggal\",\"postal_code\":null,\"country_code\":\"IDN\"}},\"expiry\":{\"start_time\":\"2020-04-16 08:08:03 +0700\",\"unit\":\"minutes\",\"duration\":2879},\"custom_field1\":null,\"custom_field2\":null,\"custom_field3\":null}";
			
			MainTransaction req = new ObjectMapper().readValue(body, MainTransaction.class);
			JsonNode node = Json.toJson(req); 
			String elasticUrl = "https://app.midtrans.com/snap/v1/transactions";
			String key = "Basic VlQtc2VydmVyLXE0aGwyX1dUSHY5dVF6WGpZZ0NMaWxGbTo=";
//			String key = "Basic U0ItTWlkLXNlcnZlci1tS0RsNUZ1dkFYLW9hQkt3cHdPYUZSTmw6";
			String bodyRequest = node.toString();
			bodyRequest = bodyRequest.replace("<", "(");
			bodyRequest = bodyRequest.replace(">", ")");
			
			String command = "curl -X POST " 
					+ "'" + elasticUrl + "' "
					+ "-H 'Content-Type:application/json' "
					+ "-H 'Accept:application/json' "
					+ "-H 'Authorization:" + key +"' "
					+ "-d '" + bodyRequest + "'";

//			Process process = Runtime.getRuntime().exec(new String[]{"curl", "-X", "POST", elasticUrl,
//					"-H", "Content-Type:application/json", 
//					"-H", "Accept:application/json", 
//					"-H", "Authorization:"+key , 
//					"-d", "'"+node.toString()+"'"});
//			ProcessBuilder processBuilder = new ProcessBuilder("curl", "-X", "POST", elasticUrl,
//					"-H", "Content-Type:application/json", 
//					"-H", "Accept:application/json", 
//					"-H", "Authorization:"+key, 
//					"-d", node.toString());
			
//			Process process = processBuilder.start();
			Process process = Runtime.getRuntime().exec(command);
			
			System.out.println("Command : " + command);
//			System.out.println("Request : " + processBuilder.command().toString());
			
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
				System.out.println(output);
				reader.close();
			} else {
				//abnormal...
				System.out.println("ERROR!");
				System.out.println(outputError);
				reader.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok("index2");
	}
	
	public static Result index2() {
		try {
//			String bodyReq = "{ \"transaction_details\" : { \"order_id\" : \"HSO200400089\", \"gross_amount\" : 3360946 }, \"item_details\" : [ { \"id\" : \"LASCMJ008RMW-117062\", \"price\" : 3340946, \"quantity\" : 1, \"name\" : \"Callista Gold Rings 01 (Color: --none--) (Size: 15\" }, { \"id\" : \"ME20200000002-tiki\", \"price\" : 20000, \"quantity\" : 1, \"name\" : \"<Shipping>PT LOTUS LINGGA PRATAMA - Citra Van Titi\" } ], \"enabled_payments\" : [ \"credit_card\", \"bca_va\", \"gopay\" ], \"credit_card\" : { \"secure\" : true, \"channel\" : \"migs\", \"installment\" : { \"required\" : false, \"terms\" : { \"bca\" : [ 6, 12 ], \"danamon\" : [ 3, 6, 12 ], \"hsbc\" : [ 6, 12 ] } }, \"whitelist_bins\" : [ ] }, \"gopay\" : { \"enable_callback\" : true, \"callback_url\" : \"whizliz://\" }, \"customer_details\" : { \"first_name\" : null, \"last_name\" : null, \"email\" : \"aldy.ramadhan012@gmail.com\", \"phone\" : null, \"billing_address\" : { \"first_name\" : \"house \", \"last_name\" : null, \"email\" : null, \"phone\" : null, \"address\" : \"wbojong\", \"city\" : \"Bojong\", \"postal_code\" : null, \"country_code\" : \"IDN\" }, \"shipping_address\" : { \"first_name\" : \"house \", \"last_name\" : null, \"email\" : null, \"phone\" : null, \"address\" : \"wbojong\", \"city\" : \"Bojong\", \"postal_code\" : null, \"country_code\" : \"IDN\" } }, \"expiry\" : { \"start_time\" : \"2020-04-09 08:30:24 +0700\", \"unit\" : \"minutes\", \"duration\" : 2879 }, \"custom_field1\" : null, \"custom_field2\" : null, \"custom_field3\" : null }";
			//full ^
	//		String body = "{ \"transaction_details\" : { \"order_id\" : \"HSO200400089\", \"gross_amount\" : 3360946 }, \"item_details\" : [ { \"id\" : \"LASCMJ008RMW-117062\", \"price\" : 3340946, \"quantity\" : 1, \"name\" : \"Callista Gold Rings 01 (Color: --none--) (Size: 15\" }, { \"id\" : \"ME20200000002-tiki\", \"price\" : 20000, \"quantity\" : 1, \"name\" : \"<Shipping>PT LOTUS LINGGA PRATAMA - Citra Van Titi\" } ]}";
	//		//ada <>
	//		String body = "{ \"transaction_details\" : { \"order_id\" : \"HSO2004 - 00089\", \"gross_amount\" : 3360946 }}";
			//tidak ada <>
			
			String bodyReq = "{\"transaction_details\":{\"order_id\":\"HSO200400023\",\"gross_amount\":49000},\"item_details\":[{\"id\":\"JDD1220200402131253-119539\",\"price\":11000,\"quantity\":1,\"name\":\"Cincin Merchant WGS Herry 002 (Color: White) (Size\"},{\"id\":\"ME20200000012-pos\",\"price\":21000,\"quantity\":1,\"name\":\"(Shipping)Merchant WGS Herry - POS Indonesia\"},{\"id\":\"JDD1620200406101720-119439\",\"price\":9000,\"quantity\":1,\"name\":\"Lino Lab (Color: Rose) (Size: 8)\"},{\"id\":\"ME20200000001-jne\",\"price\":8000,\"quantity\":1,\"name\":\"(Shipping)Whizliz - JNE\"}],\"enabled_payments\":[\"credit_card\",\"bca_va\",\"gopay\"],\"credit_card\":{\"secure\":true,\"channel\":\"migs\",\"installment\":{\"required\":false,\"terms\":{\"bca\":[6,12],\"danamon\":[3,6,12],\"hsbc\":[6,12]}},\"whitelist_bins\":[]},\"gopay\":{\"enable_callback\":true,\"callback_url\":\"whizliz://\"},\"customer_details\":{\"first_name\":\"Heri\",\"last_name\":\"kuswanto Registrasi\",\"email\":\"heri.kkuswanto.qa@gmail.com\",\"phone\":\"+628727819022\",\"billing_address\":{\"first_name\":\"Rumah Paman\",\"last_name\":null,\"email\":null,\"phone\":null,\"address\":\"Jln. M. Toha No.105\",\"city\":\"Batununggal\",\"postal_code\":null,\"country_code\":\"IDN\"},\"shipping_address\":{\"first_name\":\"Rumah Paman\",\"last_name\":null,\"email\":null,\"phone\":null,\"address\":\"Jln. M. Toha No.105\",\"city\":\"Batununggal\",\"postal_code\":null,\"country_code\":\"IDN\"}},\"expiry\":{\"start_time\":\"2020-04-16 08:08:03 +0700\",\"unit\":\"minutes\",\"duration\":2879},\"custom_field1\":null,\"custom_field2\":null,\"custom_field3\":null}";
			
			MainTransaction req = new ObjectMapper().readValue(bodyReq, MainTransaction.class);
			JsonNode node = Json.toJson(req); 
			String elasticUrl = "https://app.midtrans.com/snap/v1/transactions";
			String key = "Basic VlQtc2VydmVyLXE0aGwyX1dUSHY5dVF6WGpZZ0NMaWxGbTo=";
	//		String key = "Basic U0ItTWlkLXNlcnZlci1tS0RsNUZ1dkFYLW9hQkt3cHdPYUZSTmw6";
			String bodyRequest = node.toString();
	//		bodyRequest = bodyRequest.replace("<", "(");
	//		bodyRequest = bodyRequest.replace(">", ")");
			
			OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(bodyRequest, MediaType.get("application/json; charset=utf-8"));
			Request request = new Request.Builder()
				.url(elasticUrl)
				.addHeader("Accept", "application/json")
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", key)
				.post(body)
				.build();
		
			Response response = client.newCall(request).execute();
//			return ok(response.code() + " --- " + response.body().string());
			JsonNode jsonResponse = new ObjectMapper().readValue(response.body().string(), JsonNode.class);
			return ok(Tool.prettyPrint(jsonResponse));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ok("ERROR");
	}

	public static Result catalog(String filename) {

		try {
			File file = new File(Constant.getInstance().getCatalogPath().concat(filename));
//			File file = new File("public/google-catalog.csv");
			return ok(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return notFound();
	}

    
    public static Result migrateImagesToS3() {
    	// get media directory
    	File folder = new File("/home/whizliz-dev/whiz-media/");
    	for (File file : folder.listFiles()) {
    		//get media sub directory
    		if (file.isDirectory()) {
    			for (File file2 : file.listFiles()) {
    				if (file2.isDirectory()) {
    					for (File file3 : file2.listFiles()) {
    						S3Service.getInstance().saveObject(file.getName() + "/" + file2.getName() + "/" + file3.getName(), file3);
						}
    				}
    				else
    					S3Service.getInstance().saveObject(file.getName() + "/" + file2.getName(), file2);
				}
    		}
    		else
    			S3Service.getInstance().saveObject(file.getName(), file);
		}
    	return ok("Image migration Complete");
    }
    
	public static Result getImage(String folder, String filename) throws FileNotFoundException {
		ByteArrayInputStream input = null;
		byte[] byteArray = null;
		try {
//			File file = play.api.Play.getFile("/public/images/" + folder + "/" + filename, play.api.Play.current());
			File file = play.api.Play.getFile(Constant.getInstance().getImagePath() + folder + "/" + filename, play.api.Play.current());
			
			if (!file.exists())
				file = play.api.Play.getFile("/public/images/default/no_image.png", play.api.Play.current());
			
			String ext = FilenameUtils.getExtension(file.getPath()); 
			if (ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")) {
				response().setContentType("image");
			} else if (ext.equalsIgnoreCase("pdf")) {
				response().setContentType("application/pdf");
			} else if (ext.equalsIgnoreCase("docx")) {
				response().setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			} else if (ext.equalsIgnoreCase("xlsx")) {
				response().setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			} 
			
			byteArray = IOUtils.toByteArray(new FileInputStream(file));
			input = new ByteArrayInputStream(byteArray);
		} catch (IOException e) {
			Logger.error("Error", e);
			return notFound();
		}
		return ok(input);
	}
	
    @SuppressWarnings("rawtypes")
	public Result getStarted() {
		BaseResponse response = new BaseResponse();
        response.setBaseResponse(1, 0, 1, "Success", "Selamat, anda sudah terhubung dengan hellobisnis.");
		return ok(Json.toJson(response));
	}

}
