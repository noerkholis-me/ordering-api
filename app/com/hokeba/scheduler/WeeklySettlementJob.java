package com.hokeba.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.hokeba.util.Helper;

import models.Merchant;
import models.Settlement;
import models.SettlementDetail;

public class WeeklySettlementJob extends BaseJob {

	public WeeklySettlementJob(String corn) {
		super(corn);
	}

	@Override
	public void doJob() {
		// TODO Auto-generated method stub
		createWeeklySettlementReport();
	}

	private void createWeeklySettlementReport() {
		Transaction txn = Ebean.beginTransaction();
    	try {
    		Settlement settlement = new Settlement();
    		Date currentDate = new Date();
    		settlement.endDate = Helper.fetchStartOfDate(currentDate);
	    	Date dateWeekBefore = Helper.addDate(currentDate, Calendar.DATE, -7);
	    	settlement.startDate = dateWeekBefore;
	    	settlement.totalSettlement = 0D;
	    	settlement.totalMerchant = 0;
	    	settlement.save();
    		
	    	Double totalSettlement = 0D;
	    	int totalMerchant = 0;
	    	
    		Merchant ownMerchant = Merchant.fetchOwnMerchant();
    		List<Merchant> merchantList = Merchant.find.where().eq("t0.is_deleted", false).ne("t0.id", ownMerchant.id).orderBy("t0.name asc").findList();
    		for (Merchant merchant : merchantList) {
				if (merchant.unpaidHokeba != null && merchant.unpaidHokeba > 0 
						&& merchant.accountAlias != null && !merchant.accountAlias.isEmpty()
						&& merchant.accountNumber != null && !merchant.accountNumber.isEmpty()) {
					SettlementDetail merchantPayDetail = new SettlementDetail();
					merchantPayDetail.accountAlias = merchant.accountAlias;
					merchantPayDetail.accountNumber = merchant.accountNumber;
					merchantPayDetail.amount = merchant.unpaidHokeba;
					merchantPayDetail.merchant = merchant;
					merchantPayDetail.settlement = settlement;
					merchantPayDetail.save();
					
					totalSettlement += merchant.unpaidHokeba;
					totalMerchant++;
					
					merchant.paidHokeba += merchant.unpaidHokeba;
					merchant.unpaidHokeba = 0D;
					merchant.update();
				}
			}
	    	
    		settlement.totalMerchant = totalMerchant;
    		settlement.totalSettlement = totalSettlement;
    		settlement.update();
	    	
	    	txn.commit();
    	} catch (Exception e) {
    		e.printStackTrace();
    		txn.rollback();
    	} finally {
    		txn.end();
    	}
	}
	
}
