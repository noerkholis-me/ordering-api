package com.hokeba.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.http.HTTPRequest3;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.social.requests.FirebaseNotificationHelper;

import models.Promo;
import models.SettingExchangeRateCustomDiamond;
import models.UserCms;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import play.libs.Json;

public class ExchangeCurrencyRatesJob extends BaseJob {
	public ExchangeCurrencyRatesJob(String corn) {
		super(corn);
	}
	
	@Override
	public void doJob() {
		// TODO Auto-generated method stub
		saveLatestUsdToIdrRate();
	}
	
	private void saveLatestUsdToIdrRate() {
		Transaction txn = Ebean.beginTransaction();
    	try {
//    		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//    		Date currentDate = new Date();
//    		String currentTime = dateFormat.format(currentDate);
//	    	String saveTime= "18:30:00";
//	    	String latestUsdToIdrRate = getLatestUsdToIdrRate().getData().toString();
//	    	JsonNode jsonResponse = new ObjectMapper().readValue(latestUsdToIdrRate, JsonNode.class);
    		String url = "https://api.exchangeratesapi.io/latest?base=USD&symbols=IDR";
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url(url).get().build();
			Call call = client.newCall(request);
			Response responses = call.execute();
			System.out.println(responses.code());
			System.out.println(responses.body().source());
			JsonNode jsonResponse = new ObjectMapper().readValue(responses.body().string(), JsonNode.class);
			Date date = new Date(jsonResponse.findPath("date").toString());
			SettingExchangeRateCustomDiamond data = new SettingExchangeRateCustomDiamond();
			data.userCms = null;
			data.idrRate = jsonResponse.findPath("rates").findPath("IDR").asDouble();
			data.date = date;
			data.save();
	    	
	    	txn.commit();
    	} catch (Exception e) {
    		e.printStackTrace();
    		txn.rollback();
    	} finally {
    		txn.end();
    	}
	}

}
