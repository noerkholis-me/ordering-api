package com.hokeba.scheduler;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import models.MemberLog;
import models.MerchantLog;
import play.Logger;

import java.util.List;

/**
 * Created by hendriksaragih on 7/30/17.
 */
public class DailyJob extends BaseJob {

    public DailyJob(String corn) {
        super(corn);
    }

    @Override
    public void doJob() {
        cleanToken();
    }

    private void cleanToken(){
//        Transaction txn = Ebean.beginTransaction();
//        try {
////            List<MerchantLog> list = MerchantLog.find.where().eq("is_active", false).findList();
////            Ebean.delete(list);
//
//            List<MemberLog> listLog = MemberLog.find.where().eq("is_active", false).findList();
////            Ebean.delete(listLog);
//
//            txn.commit();
//        }
//        catch (Exception e) {
//            Logger.error("DAILY JOB", e);
//            txn.rollback();
//        }
//        finally {
//            txn.end();
//        }
    }

}
