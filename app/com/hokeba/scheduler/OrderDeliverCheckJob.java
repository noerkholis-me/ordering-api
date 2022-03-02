package com.hokeba.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.shipping.rajaongkir.RajaOngkirService;
import com.hokeba.shipping.rajaongkir.mapping.ReqMapWaybill;
import com.hokeba.shipping.rajaongkir.mapping.ResMapWaybill;
import com.hokeba.social.requests.FirebaseNotificationHelper;
import com.hokeba.util.Helper;

import assets.Tool;
import models.SalesOrder;
import models.SalesOrderSeller;
import models.SalesOrderSellerStatus;
import play.Logger;
import play.libs.Json;

public class OrderDeliverCheckJob extends BaseJob {
	private static final String STATUS_DELIVERED = "DELIVERED";
	private static final String POD_DATE_FORMAT = "yyyy-MM-dd";
	
	public OrderDeliverCheckJob(String corn) {
		super(corn);
	}

	@Override
	public void doJob() {
		// TODO Auto-generated method stub
		checkForDeliveredOrder();
	}
	
	public void checkForDeliveredOrder() {
    	try {
    		List<SalesOrderSeller> salesOrderSellerList = SalesOrderSeller.find.where()
    				.eq("t0.is_deleted", false).eq("t0.status", SalesOrder.ORDER_STATUS_ON_DELIVERY).findList();
    		Date currentDate = new Date();
    		RajaOngkirService roService = RajaOngkirService.getInstance();
    		FirebaseNotificationHelper notificationHelper = new FirebaseNotificationHelper();
    		for (SalesOrderSeller salesOrderSeller : salesOrderSellerList) {
				if (salesOrderSeller.trackingNumber != null && !salesOrderSeller.trackingNumber.isEmpty()) {
					Transaction txn = Ebean.beginTransaction();
					try {
						ReqMapWaybill trackingReq = new ReqMapWaybill();
						trackingReq.courier = salesOrderSeller.courierCode;
						trackingReq.waybill = salesOrderSeller.trackingNumber;
						ResMapWaybill trackingRes = roService.shipmentTracking(trackingReq);
//						Logger.info(Tool.prettyPrint(Json.toJson(trackingRes)));
						
						Calendar expiredDelivery = Calendar.getInstance();
						expiredDelivery.setTime(salesOrderSeller.deliveredDate);
						expiredDelivery.add(Calendar.DATE, 14);
						
						//tracking resi (JNE only)
						if (trackingRes != null && STATUS_DELIVERED.equals(trackingRes.deliveryStatus.status) 
								&& trackingRes.deliveryStatus.podDate != null 
								&& !trackingRes.deliveryStatus.podDate.isEmpty()) {
							Date podDate = Helper.parseStringToDate(trackingRes.deliveryStatus.podDate, POD_DATE_FORMAT);
							Calendar cal = Calendar.getInstance();
							cal.setTime(podDate);
							cal.add(Calendar.DATE, 7);
							if(currentDate.after(cal.getTime())) {
								
								salesOrderSeller.status = SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER;
								salesOrderSeller.paymentStatus = SalesOrderSeller.UNPAID_HOKEBA;
								salesOrderSeller.update();
								
								SalesOrderSellerStatus sosStatus = new SalesOrderSellerStatus(salesOrderSeller, new Date(), 6, "Your orders has arrived at the destination. Thank you for shopping at Whizliz");
			                    sosStatus.save();
			                    
			                    salesOrderSeller.salesOrderDetail.forEach(sod -> {
			                        sod.status = salesOrderSeller.status;
			                        sod.update();
			                    });
			                    
			                    SalesOrderSeller.finishAndGetPoint(salesOrderSeller);
			                    SalesOrder.checkOrderComplete(salesOrderSeller.salesOrder);
			                    //send received order status notification
			                    String title = "Pesanan sudah sampai";
		    					String message = "Pesananmu sudah sampai nih!. Terima kasih sudah berbelanja di Whizliz.";
		    					ObjectNode ob = Json.newObject();
		    					ob.put("data", salesOrderSeller.status);
		    	    			ObjectNode type = ob;
		    	    			String topic = "order-"+salesOrderSeller.member.id;
		    	    			String screenMobile = "/RecentrderPage";
		                        notificationHelper.sendToTopic(title, message, type, topic,screenMobile);
		                        
					    		txn.commit();
							}
						}
						//delivery date > 14 hari
						else if (currentDate.after(expiredDelivery.getTime())){
							salesOrderSeller.status = SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER;
							salesOrderSeller.paymentStatus = SalesOrderSeller.UNPAID_HOKEBA;
							salesOrderSeller.update();
							
							SalesOrderSellerStatus sosStatus = new SalesOrderSellerStatus(salesOrderSeller, new Date(), 6, "Your orders has arrived at the destination. Thank you for shopping at Whizliz");
		                    sosStatus.save();
		                    
		                    salesOrderSeller.salesOrderDetail.forEach(sod -> {
		                        sod.status = salesOrderSeller.status;
		                        sod.update();
		                    });

		                    SalesOrderSeller.finishAndGetPoint(salesOrderSeller);
		                    SalesOrder.checkOrderComplete(salesOrderSeller.salesOrder);
		                    
		                  //send received order status notification
		                    String title = "Pesanan sudah sampai";
	    					String message = "Pesananmu sudah sampai nih!. Terima kasih sudah berbelanja di Whizliz.";
	    					ObjectNode ob = Json.newObject();
	    					ob.put("data", salesOrderSeller.status);
	    	    			ObjectNode type = ob;
	    	    			String topic = "order-"+salesOrderSeller.member.id;
	    	    			String screenMobile = "/RecentrderPage";
	                        notificationHelper.sendToTopic(title, message, type, topic,screenMobile);
	                        
				    		txn.commit();
						}
					} catch (Exception e) {
						System.out.println("ORDER COMPLETE CHECKING SCHEDULER ERROR");
						e.printStackTrace();
			    		txn.rollback();
					} finally {
			    		txn.end();
			    	}
				}
			}
    	} catch (Exception e) {
    		System.out.println("MAIN ORDER COMPLETE CHECKING SCHEDULER ERROR");
    		e.printStackTrace();
    	} 
	}

}
