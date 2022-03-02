package com.hokeba.scheduler;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.social.requests.FirebaseNotificationHelper;

import models.Promo;
import play.libs.Akka;
import play.libs.Json;
import play.libs.Time.CronExpression;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class CampaignPushNotificationJob extends BaseJob {
	public CampaignPushNotificationJob(String corn) {
		super(corn);
	}
	
	@Override
	public void doJob() {
		// TODO Auto-generated method stub
		sendCampaignNotification();
	}
	
	private void sendCampaignNotification() {
		Transaction txn = Ebean.beginTransaction();
    	try {
    		FirebaseNotificationHelper notificationHelper = new FirebaseNotificationHelper();
    		DateTime now = new DateTime();
    		List<Promo> datas = Promo.find.where().eq("is_deleted", false).eq("status", true).ge("active_to", now).ne("notification_time", null).findList();
    		if(!(datas == null)) {
    			for (Promo data : datas) {
    				DateTime currentDate = new DateTime();
    				String currentTime = currentDate.getHourOfDay() + ":"+currentDate.getMinuteOfHour() + ":" + currentDate.getSecondOfMinute();
    				String payload[] = data.notificationTime.toString().split(" ");
    				if(currentTime.equals(payload[1])) {
    					ObjectNode ob = Json.newObject();
    					ob.put("data", data.description);
    					ObjectNode type = ob;
    					String topic = "campaign-" + data.slug;
    					String screenMobile = "/Promo";
    					notificationHelper.sendToTopic(data.name, data.description, type, topic, screenMobile);
    				}
    				System.out.println("outer akka");
    				// end of time notification
    			}
    		}
	    	
	    	txn.commit();
    	} catch (Exception e) {
    		e.printStackTrace();
    		txn.rollback();
    	} finally {
    		txn.end();
    	}
	}

}
