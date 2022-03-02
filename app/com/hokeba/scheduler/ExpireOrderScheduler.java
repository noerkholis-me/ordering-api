package com.hokeba.scheduler;

import java.util.concurrent.TimeUnit;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.Transaction;

import models.Member;
import models.SalesOrder;

public class ExpireOrderScheduler extends BaseJob2 {
	private Long orderId;

    public ExpireOrderScheduler(String corn, int scheduleInterval, TimeUnit scheduleIntervalTimeUnit, Long orderId) {
    	super(corn, scheduleInterval, scheduleIntervalTimeUnit);
    	this.orderId = orderId;
    }

    @Override
    public void doJob() {
        checkOrderExpired();
    }
    
    @Override
    public String getScheduleName() {
    	return "Expire Order Scheduler";
    }

    private void checkOrderExpired(){
    	Transaction txn = Ebean.beginTransaction();
        try {
			SqlUpdate updOrder = Ebean.createSqlUpdate(
					"UPDATE sales_order set status =:statusExpired "
					+ "where "
					+ "id =:paymentId AND "
					+ "is_deleted = false AND "
					+ "payment_status =:statusAwaiting AND "
					+ "active_payment = true");
			updOrder.setParameter("statusExpired", SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT);
			updOrder.setParameter("statusAwaiting", SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION);
			updOrder.setParameter("orderId", orderId);
			int resultUpd = updOrder.execute();
			
			if (resultUpd != 0) {
	        	SalesOrder.revertItemStockFromOrder(orderId);
	            txn.commit();
	            
	            //notif email
//        		SalesOrder orderTarget = SalesOrder.find.byId(orderId);
//        		Payment paymentTarget = Payment.find.where().eq("t0.id", paymentId).eq("t0.sales_order_id", orderId).setMaxRows(1).findUnique();
//        		if (orderTarget != null && paymentTarget != null) {
//        			Member memberTarget = orderTarget.member;
//        			if (memberTarget != null && memberTarget.email != null && !memberTarget.email.isEmpty()) {
//        				String emailContent = MailOrderExpired.renderMailTemplate(
//        						null, memberTarget.fullName == null ? "" : memberTarget.fullName, 
//        						orderTarget, paymentTarget);
//        				MailConfig.sendEmailWithThread(memberTarget.email, 
//								MailConfig.generateSubjectWithOrderNumber(MailConfig.subjectOrderExpired, orderTarget.orderNumber), 
//								emailContent);
//        			}
//        		}
	        	
			}
        } catch (Exception e) {
            txn.rollback();
        } finally {
            txn.end();
        }
    }
}
