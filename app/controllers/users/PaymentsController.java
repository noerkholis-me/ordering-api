package controllers.users;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.payment.kredivo.KredivoService;
import com.hokeba.payment.kredivo.response.KredivoCallbackResponse;
import com.hokeba.payment.kredivo.response.KredivoUpdateResponse;
import com.hokeba.payment.midtrans.MidtransService;
import com.hokeba.payment.midtrans.response.TransactionStatus;
import com.hokeba.social.requests.MailchimpCustomerRequest;
import com.hokeba.social.requests.MailchimpOrderLineRequest;
import com.hokeba.social.requests.MailchimpOrderRequest;
import com.hokeba.social.service.MailchimpService;

import assets.Tool;
import controllers.BaseController;
import models.Cart;
import models.CartAdditionalDetail;
import models.Member;
import models.SOrder;
import models.SOrderPayment;
import models.SalesOrder;
import models.SalesOrderDetail;
import models.SalesOrderPayment;
import models.SalesOrderSeller;
import models.SalesOrderSellerStatus;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Result;

public class PaymentsController extends BaseController {
	public static final String URL_KREDIVO_PUSH = Play.application().configuration().getString("payment.url.callback.kredivo", "");;
	public static final String URL_MIDTRANS_PUSH = Play.application().configuration().getString("payment.url.callback.midtrans", "");;
	public static final String URL_TXN_SUCCESS_REDIRECT = Play.application().configuration().getString("payment.url.success", "");;
	public static final String URL_TXN_CANCEL_REDIRECT = Play.application().configuration().getString("payment.url.cancel", "");;
	
	
	/**
	 * Route untuk handling push notification Midtrans
	 * 
	 * @return respons push notification dari Midtrans
	 */
	public static Result midTransPushNotification() {
		Logger.debug("MIDTRANS NOTIFICATIONS");
		try {
			JsonNode jsonString = request().body().asJson();
			System.out.println("LOG REQUEST \n" + jsonString);
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
			TransactionStatus transactionStatus = mapper.readValue(jsonString.toString(), TransactionStatus.class);
			System.out.println("LOG MAP REQUEST " + Tool.prettyPrint(Json.toJson(transactionStatus)));
			if (!MidtransService.validateResponse(transactionStatus.signature_key, transactionStatus.order_id, transactionStatus.status_code, transactionStatus.gross_amount)) {
				return unauthorized();
			}
			SOrder order = SOrder.find.where().eq("order_number", transactionStatus.order_id).setMaxRows(1).findUnique();
			if (order == null) return notFound();
			
			return midTransNotificationHandling(order, transactionStatus);
		} catch (Exception e) {
			Logger.error("midTransPushNotification", e);
		}
		return badRequest();
	}
	
	/**
	 * Proses handling notifikasi Midtrans
	 * 
	 * @param order
	 * @param notif
	 * @return respon notifikasi Midtrans
	 */
	private static Result midTransNotificationHandling(SOrder order, TransactionStatus notif) {
		int status = Integer.parseInt(notif.status_code);
		SOrderPayment targetPayment = order.orderPayment == null ? null : order.orderPayment;
		
		if (targetPayment == null) {
			//create new payment data
			targetPayment = new SOrderPayment();
			targetPayment.order = order;
			targetPayment.totalTransfer = 0D;
			targetPayment.status = SOrderPayment.PAYMENT_VERIFY;
		}
		
//		if (targetPayment == null || targetPayment.paymentGatewayCode != MapPaymentToken.paymentGatewayCodeMidtrans ||
//				(targetPayment.transactionId != null && !targetPayment.transactionId.equals(notif.transaction_id))) {
//			targetPayment = Payment.find.where().eq("t0.is_deleted", false)
//					.eq("t0.transaction_id", notif.transaction_id).eq("t0.payment_gateway_code", MapPaymentToken.paymentGatewayCodeMidtrans)
//					.eq("t0.sales_order_id", order.id).order("t0.id desc").setMaxRows(1).findUnique();
//			if (targetPayment == null) {
//				return notFound();
//			}
//		}
		
		Transaction txn = Ebean.beginTransaction();
		try {
//			boolean triggerPaid = false;
//			boolean triggerAccept = false;
//			boolean triggerLog = true;
			switch(status) {
				//TODO PERHATIKAN PROSES VALIDASI CANCEL DAN EXPIRED
				case 200 : {
					//SUCCESS
					//if status < paid -> upd status paid, time, id transaksi, nomor va
					if (order.isDeleted) {
						return badRequest();
					}
					
					if (order.status.equals(SOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION) ||
							order.status.equals(SOrder.ORDER_STATUS_CHECKOUT)) {
						
						targetPayment.confirmAt = new Date();
						targetPayment.transactionId = notif.transaction_id;
						targetPayment.eciCode = notif.eci;
						targetPayment.paymentInstalment = notif.installment_term;
						targetPayment.vaNumber = notif.fetchVaNumber();
						targetPayment.companyCode = notif.biller_code;
						targetPayment.settlement = "settlement".equals(notif.transaction_status) ? true : false;
						
						targetPayment.paymentType = notif.payment_type;
						targetPayment.bank = notif.fetchBankName();
						targetPayment.cardType = notif.cardType;
						
						targetPayment.totalTransfer += Double.parseDouble(notif.gross_amount);
						
						if (targetPayment.totalTransfer >= order.totalPrice) {
							targetPayment.status = SOrderPayment.VERIFY;
							order.status = SOrder.ORDER_STATUS_VERIFY;

							if (order.member != null) {
								List<Cart> carts = Cart.find.where().eq("member.id", order.member.id).findList();
								
								for (Cart c : carts) {
									for (CartAdditionalDetail ca : c.additionalDetails) {
										ca.delete();
									}
									c.delete();
								}
							}
							
//							triggerPaid = true;
						} else {
							targetPayment.status = SOrderPayment.PAYMENT_VERIFY;
							order.status = SOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION;
						}
						
//						if (order.status == SalesOrder.ORDER_STATUS_VERIFY) {
//							triggerPaid = true;
//						}
//						order.stockOut();
					} else if (order.status.equals(SOrder.ORDER_STATUS_EXPIRE_PAYMENT) || 
								order.status.equals(SOrder.ORDER_STATUS_CANCEL) || 
								order.status.equals(SOrder.ORDER_STATUS_CANCEL_BY_CUSTOMER_SERVICE)){
						return status(202);
					} else if ("refund".equals(notif.transaction_status)) {
						order.status = SOrder.ORDER_STATUS_CANCEL;
//						targetPayment.status = SalesOrderPayment.VERIFY;
						
						targetPayment.transactionId = notif.transaction_id;
						targetPayment.eciCode = notif.eci;
						targetPayment.paymentInstalment = notif.installment_term;
						targetPayment.vaNumber = notif.fetchVaNumber();
						targetPayment.companyCode = notif.biller_code;
						targetPayment.voidAt = new Date(System.currentTimeMillis());
					} else if ("settlement".equals(notif.transaction_status)) {
						targetPayment.settlement = true;
//						triggerLog = false;
					} else {
						return badRequest();
					}
					break;
				}
				case 201 : {
					//PENDING
					//if status != paid -> upd status waiting, time, id, va number
					if (order.isDeleted) {
						return badRequest();
					}
					
					if (order.status.equals(SOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION) 
							|| order.status.equals(SOrder.ORDER_STATUS_CHECKOUT)) {
//						String oldVaNumber = targetPayment.vaNumber;
						order.status = SOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION;
						
						targetPayment.transactionId = notif.transaction_id;
						targetPayment.eciCode = notif.eci;
						targetPayment.paymentInstalment = notif.installment_term;
						targetPayment.vaNumber = notif.fetchVaNumber();
						targetPayment.companyCode = notif.biller_code;
						
						targetPayment.paymentType = notif.payment_type;
						targetPayment.bank = notif.fetchBankName();
						targetPayment.cardType = notif.cardType;
						
						targetPayment.status = SOrderPayment.PAYMENT_VERIFY;
						
//						if (targetPayment.vaNumber != null && !(targetPayment.vaNumber.equals(oldVaNumber))) {
//							triggerAccept = true;
//						}
					} else {
						return badRequest();
					}
					break;
				}
				case 202 : {
					//CANCELED
					//if status != success? -> upd status cancel, time, id, va number
					if (order.status.equals(SOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION)
							|| order.status.equals(SOrder.ORDER_STATUS_VERIFY)
							|| order.status.equals(SOrder.ORDER_STATUS_CANCEL_BY_CUSTOMER_SERVICE)
							|| order.status.equals(SOrder.ORDER_STATUS_CANCEL) 
							|| order.status.equals(SOrder.ORDER_STATUS_CHECKOUT) 
							|| order.status.equals(SOrder.ORDER_STATUS_EXPIRE_PAYMENT)) {
						
						if (notif.transaction_status.equals("expire")) {
							if (!order.isDeleted && order.status.equals(SOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION)) {
								order.status = SOrder.ORDER_STATUS_EXPIRE_PAYMENT;
								targetPayment.status = SOrderPayment.PAYMENT_REJECT;

								targetPayment.transactionId = notif.transaction_id;
								targetPayment.eciCode = notif.eci;
								targetPayment.paymentInstalment = notif.installment_term;
								targetPayment.vaNumber = notif.fetchVaNumber();
								targetPayment.companyCode = notif.biller_code;
								targetPayment.voidAt = new Date(System.currentTimeMillis());
								
							} else if ((order.isDeleted && order.status.equals(SOrder.ORDER_STATUS_CHECKOUT)) 
								|| order.status.equals(SOrder.ORDER_STATUS_EXPIRE_PAYMENT)) {
								// do nothing
							} else {
								return badRequest();
							}
							
						} else if (order.isDeleted 
								|| order.status.equals(SOrder.ORDER_STATUS_CHECKOUT) 
								|| order.status.equals(SOrder.ORDER_STATUS_EXPIRE_PAYMENT)) {
							return badRequest();
							
						} else if (!notif.transaction_status.equals("deny")) {
							order.status = SOrder.ORDER_STATUS_CANCEL;
							targetPayment.status = SOrderPayment.PAYMENT_REJECT;
		
							targetPayment.transactionId = notif.transaction_id;
							targetPayment.eciCode = notif.eci;
							targetPayment.paymentInstalment = notif.installment_term;
							targetPayment.vaNumber = notif.fetchVaNumber();
							targetPayment.companyCode = notif.biller_code;
							targetPayment.voidAt = new Date(System.currentTimeMillis());
							
						}
					} else {
						return badRequest();
					}
					break;
				}
				case 407 : {
					//EXPIRED
					if (order.isDeleted) {
						return badRequest();
					}
					if (order.status.equals(SOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION)) {
						order.status = SOrder.ORDER_STATUS_EXPIRE_PAYMENT;
						targetPayment.status = SOrderPayment.PAYMENT_REJECT;

						targetPayment.transactionId = notif.transaction_id;
						targetPayment.eciCode = notif.eci;
						targetPayment.paymentInstalment = notif.installment_term;
						targetPayment.vaNumber = notif.fetchVaNumber();
						targetPayment.companyCode = notif.biller_code;
						targetPayment.voidAt = new Date(System.currentTimeMillis());
						
					} else {
						return badRequest();
					}
					break;
				}
				default : {
					if (order.isDeleted) {
						return badRequest();
					}
					if (status >= 500) {
						return ok();
					} else if (status >= 400) {
						return ok();
					} else if (status >= 300) {
						return ok();
					} else {
						return badRequest();
					}
				}
			}
//			if (triggerPaid) {
//				for (SalesOrderSeller sellerData : order.salesOrderSellers) {
//					sellerData.status = SalesOrder.ORDER_STATUS_VERIFY;
//					sellerData.update();
//					SalesOrderSellerStatus sosStatus = new SalesOrderSellerStatus(sellerData, new Date(), 3, "Order succesfully paid");
//                    sosStatus.save();
//
//					//mailchimp
//					mailchimpAddOrder(order);
//				}
//			}
			order.paymentType = targetPayment.paymentType;
			order.save();
//			if (triggerLog) {
//				SalesOrderLog log = new SalesOrderLog(order.status, order);
//				log.save();
//			}
			if (targetPayment.id == null) {
				targetPayment.save();
			} else {
				targetPayment.update();
			}
			txn.commit();
			
			//email notification
//			if (triggerPaid) {
//				SalesOrder orderTarget = SalesOrder.find.byId(order.id);
//				Payment paymentTarget = Payment.find.where().eq("t0.id", targetPayment.id).eq("t0.sales_order_id", order.id).setMaxRows(1).findUnique();
//				if (orderTarget != null && paymentTarget != null) {
//					Member memberTarget = orderTarget.member;
//					if (memberTarget != null && memberTarget.email != null && !memberTarget.email.isEmpty()) {
//						String emailContent = MailOrderConfirm.renderMailTemplate(
//								null, memberTarget.fullName == null ? "" : memberTarget.fullName, 
//								orderTarget, paymentTarget);
//						MailConfig.sendEmailWithThread(memberTarget.email, 
//								MailConfig.generateSubjectWithOrderNumber(MailConfig.subjectOrderConfirm, orderTarget.orderNumber), 
//								emailContent);
//						
//						SmsUtils.sendSMSOrderPaid(memberTarget.phone, memberTarget.firstName, Long.toString(orderTarget.fetchRoundedGrandTotal()), orderTarget.orderNumber);
//						
//					}
//					
//					String recipientWhs = InternalMailHelper.getRecipientEmailOrderWarehouse();
//					InternalMailHelper.sendEmailOrderProcessWarehouse(orderTarget, paymentTarget, null, recipientWhs);
//				}
//			} else if (triggerAccept) {
//				SalesOrder orderTarget = SalesOrder.find.byId(order.id);
//				Payment paymentTarget = Payment.find.where().eq("t0.id", targetPayment.id).eq("t0.sales_order_id", order.id).setMaxRows(1).findUnique();
//				if (orderTarget != null && paymentTarget != null) {
//					Member memberTarget = orderTarget.member;
//					if (memberTarget != null && memberTarget.email != null && !memberTarget.email.isEmpty()) {
//						String emailContent = MailOrderAccept.renderMailTemplate(
//								null, memberTarget.fullName == null ? "" : memberTarget.fullName, 
//								orderTarget, paymentTarget);
//						MailConfig.sendEmailWithThread(memberTarget.email, 
//								MailConfig.generateSubjectWithOrderNumber(MailConfig.subjectOrderAccept, orderTarget.orderNumber), 
//								emailContent);
//					}
//				}
//			}
			
			return ok();
		} catch (Exception e) {
			Logger.error("midTransNotificationHandling", e);
			txn.rollback();
		} finally {
			txn.end();
		}
		return badRequest();
	}
	
	public static Result kredivoHandlingNotification() {
		Logger.debug("KREDIVO NOTIFICATIONS");
		try {
			JsonNode jsonString = request().body().asJson();
			//convert single string json notif from Kredivo
//			System.out.println("LOG REQUEST \n" + jsonString);
			ObjectMapper mapper = new ObjectMapper();
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
			KredivoCallbackResponse notif = mapper.readValue(jsonString.toString(), KredivoCallbackResponse.class);
			System.out.println("LOG MAP REQUEST " + Tool.prettyPrint(Json.toJson(notif)));
			if (!KredivoService.getInstance().checkSignatureKey(notif)) { //validate signatureKey
				System.out.println("SIGNATURE KEY FAILED");
				return ok(Json.toJson(KredivoService.getInstance().buildFailedKredivoResponse("Invalid signature key")));
			}
			SalesOrder orderTarget = SalesOrder.find.where().eq("t0.is_deleted", false).eq("t0.order_number", notif.orderId).setMaxRows(1).findUnique();
			if (orderTarget == null || !KredivoService.PAYMENT_METHOD_KREDIVO.equals(orderTarget.paymentType)) { //get order data
				System.out.println("ORDER DATA NOT FOUND");
				return ok(Json.toJson(KredivoService.getInstance().buildFailedKredivoResponse("Order data not found")));
			}
			
			//start processing
			return kredivoHandlingNotificationProcess(orderTarget, notif);
		} catch (Exception e) {
			Logger.error("kredivoPushNotification", e);
		}
		return badRequest();
	}
	
	//TODO
	private static Result kredivoHandlingNotificationProcess(SalesOrder order, KredivoCallbackResponse notif) {
		System.out.println("BEGIN PROCESSING KREDIVO ORDER");
		SalesOrderPayment targetPayment = order.salesOrderPayment;
		
		Transaction txn = Ebean.beginTransaction();
		try {
			boolean triggerPaid = false;
			boolean triggerAccept = false;
			boolean triggerUpdate = false;
			boolean triggerLog = true;
			if (targetPayment == null) {
				//create new payment data
				targetPayment = new SalesOrderPayment();
				targetPayment.salesOrder = order;
				targetPayment.totalTransfer = 0D;
				targetPayment.status = SalesOrderPayment.PAYMENT_VERIFY;
			}
			
			if (KredivoService.STATUS_OK.equals(notif.status) && notif.transactionStatus != null) {
				switch(notif.transactionStatus) {
					case KredivoService.TXN_STATUS_PENDING: {
						System.out.println("UPDATE STATUS PENDING");
						if (order.status.equals(SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION) 
								|| order.status.equals(SalesOrder.ORDER_STATUS_CHECKOUT)) {
							System.out.println("UPDATE ORDER DATA");
							order.status = SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION;
							
							targetPayment.transactionId = notif.transactionId;
							targetPayment.paymentType = notif.paymentType;
							
							targetPayment.status = SalesOrderPayment.PAYMENT_VERIFY;
							Double newAmount = Double.parseDouble(notif.amount.trim());
							if (!newAmount.equals(order.subtotal)) {
								targetPayment.instalmentCost = newAmount - order.subtotal;
							}
							triggerUpdate = true;
						} else {
							System.out.println("ORDER PENDING NOT EXIST ANYMORE");
							return ok(Json.toJson(KredivoService.getInstance().buildFailedKredivoResponse("Order data has been processed already")));
						}
						break;
					}
					case KredivoService.TXN_STATUS_EXPIRE: {
						if (order.status.equals(SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION)) {
							order.status = SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT;
							targetPayment.status = SalesOrderPayment.PAYMENT_REJECT;
	
							targetPayment.transactionId = notif.transactionId;
							
							targetPayment.voidAt = new Date(System.currentTimeMillis());
							
							SalesOrder.revertItemStockFromOrder(order);
							SalesOrder.revertPointExpiredPayment(order);
						} else {
							return ok(Json.toJson(KredivoService.getInstance().buildFailedKredivoResponse("Order data has been processed already")));
						}
						break;
					}
					case KredivoService.TXN_STATUS_SETTLEMENT: {
						if (order.status.equals(SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION) ||
								order.status.equals(SalesOrder.ORDER_STATUS_CHECKOUT)) {
							
							targetPayment.confirmAt = new Date();
							targetPayment.transactionId = notif.transactionId;
							targetPayment.settlement = true;
							
							targetPayment.paymentType = notif.paymentType;
							
							targetPayment.totalTransfer = Double.parseDouble(notif.amount);
							
							if (targetPayment.totalTransfer >= order.getTotal()) {
								targetPayment.status = SalesOrderPayment.VERIFY;
								order.status = SalesOrder.ORDER_STATUS_VERIFY;
								triggerPaid = true;
							} else {
								targetPayment.status = SalesOrderPayment.PAYMENT_VERIFY;
								order.status = SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION;
							}
							
//							order.stockOut();
						} else {
							return ok(Json.toJson(KredivoService.getInstance().buildFailedKredivoResponse("Order data has been changed already")));
						}
						break;
					}
					case KredivoService.TXN_STATUS_DENY: {
	
//						break;
						return ok(Json.toJson(KredivoService.getInstance().buildSuccessKredivoResponse("Payment deny")));
					}
					case KredivoService.TXN_STATUS_CANCEL: {
	
//						break;
						return ok(Json.toJson(KredivoService.getInstance().buildSuccessKredivoResponse("Payment cancel")));
					}
					default : {
						return ok(Json.toJson(KredivoService.getInstance().buildFailedKredivoResponse("Invalid transaction status")));
					}
				}
				
				
				//TODO posthandling
				if (triggerPaid) {
					for (SalesOrderSeller sellerData : order.salesOrderSellers) {
						sellerData.status = SalesOrder.ORDER_STATUS_VERIFY;
						sellerData.update();
						SalesOrderSellerStatus sosStatus = new SalesOrderSellerStatus(sellerData, new Date(), 3, "Order succesfully paid");
	                    sosStatus.save();
						
						//mailchimp
						mailchimpAddOrder(order);
					}
				}
				order.save();
//				if (triggerLog) {
//					SalesOrderLog log = new SalesOrderLog(order.status, order);
//					log.save();
//				}
				if (targetPayment.id == null) {
					targetPayment.save();
				} else {
					targetPayment.update();
				}
				txn.commit();
				
				if (triggerUpdate) {
					updateOrderWithThread(notif.transactionId, notif.signatureKey);
				}
				return ok(Json.toJson(KredivoService.getInstance().buildSuccessKredivoResponse("Order data succesfully updated")));
			} else {
				System.out.println("ERROR KREDIVO");
				//handling error kredivo
				return ok(Json.toJson(KredivoService.getInstance().buildFailedKredivoResponse("KREDIVO Error - " + notif.message)));
			}
		} catch (Exception e) {
			Logger.error("kredivoNotificationHandling", e);
			txn.rollback();
		} finally {
			txn.end();
		}
		return ok(Json.toJson(KredivoService.getInstance().buildFailedKredivoResponse("We're sorry something went wrong")));
	}
	
	
	private static void updateOrderWithThread(String transactionId, String signatureKey) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(10000);
					ServiceResponse updateResponse = KredivoService.getInstance().update(transactionId, signatureKey);
					System.out.println("LOG MAP KREDIVO UPDATE " + Tool.prettyPrint(Json.toJson(updateResponse)));
					
					if (updateResponse.getCode() == 200) {
						ObjectMapper mapper = new ObjectMapper();
						KredivoUpdateResponse mappedResponse = mapper.convertValue(updateResponse.getData(), KredivoUpdateResponse.class);
						
						if (KredivoService.STATUS_OK.equals(mappedResponse.status) && 
								KredivoService.TXN_STATUS_SETTLEMENT.equals(mappedResponse.transactionStatus)) {
							SalesOrder orderTarget = SalesOrder.find.where().eq("t0.is_deleted", false)
									.eq("t0.order_number", mappedResponse.orderId)
									.eq("t0.status", SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION)
									.setMaxRows(1).findUnique();
							
							if (orderTarget != null && orderTarget.salesOrderPayment != null) {
								Transaction txn = Ebean.beginTransaction();
								try {
									SalesOrderPayment targetPayment = orderTarget.salesOrderPayment;
									targetPayment.confirmAt = new Date();
									targetPayment.transactionId = mappedResponse.transactionId;
									targetPayment.settlement = true;
									
									targetPayment.paymentType = mappedResponse.paymentType;
									
									targetPayment.totalTransfer = Double.parseDouble(mappedResponse.amount);
									
									targetPayment.status = SalesOrderPayment.VERIFY;
									orderTarget.status = SalesOrder.ORDER_STATUS_VERIFY;
										
									for (SalesOrderSeller sellerData : orderTarget.salesOrderSellers) {
										sellerData.status = SalesOrder.ORDER_STATUS_VERIFY;
										sellerData.update();
										SalesOrderSellerStatus sosStatus = new SalesOrderSellerStatus(sellerData, new Date(), 3, "Order succesfully paid");
					                    sosStatus.save();
									}
									orderTarget.save();
									targetPayment.update();
									txn.commit();
								} catch (Exception e) {
									e.printStackTrace();
									txn.rollback();
								} finally {
									txn.end();
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
	
	public static void mailchimpAddOrder(SalesOrder so) {
//        boolean mailchimpEnabled = Play.application().configuration().getBoolean("whizliz.social.mailchimp.enabled");
        if (MailchimpService.isEnabled()) {
            try {
                Logger.info("Mailchimp - Add Order started");
                Member member = Member.find.byId(so.member.id);
                MailchimpCustomerRequest mailchimpCustomer = new MailchimpCustomerRequest(member);
                List<MailchimpOrderLineRequest> mailchimpLines = new ArrayList<MailchimpOrderLineRequest>();
                for (SalesOrderSeller sos : so.salesOrderSellers) {
                	for (SalesOrderDetail sod : sos.salesOrderDetail) {
                		mailchimpLines.add(new MailchimpOrderLineRequest(sod.id.toString(), sod.getProductId().toString(), sod.getProductVarianceId().toString(), sod.quantity, sod.price, sod.priceDiscount));
					}
				}
                MailchimpOrderRequest request = new MailchimpOrderRequest(so.orderNumber, mailchimpCustomer, so.totalPrice, mailchimpLines, so.discount, so.shipping);
                ServiceResponse mailchimpResult = MailchimpService.getInstance().AddOrder(request);
                Logger.info("Mailchimp - Add Order finished");
			} catch (Exception e) {
				// TODO: handle exception
                Logger.info("Mailchimp - Add Order failed");
			}
        }
	}
	
}
