package com.hokeba.scheduler;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Transaction;
import models.*;
import play.Logger;

import java.util.Date;

/**
 * Created by hendriksaragih on 7/2/17.
 */
public class ServiceJob extends BaseJob {

    public ServiceJob(String cron) {
        super(cron);
    }

    @Override
    public void doJob() {
        checkOrderExpired();
        checkPushNotifBroadcast();
        checkActiveDiscountProduct();
        checkExpiredProduct();
        checkPendingAuthorization();
    }

    //odoo
    private void checkOrderExpired(){
        Transaction txn = Ebean.beginTransaction();
        try {
            SalesOrder.find.where().eq("t0.is_deleted", false)
            		.or(Expr.eq("status", SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION), 
            				Expr.eq("status", SalesOrder.ORDER_STATUS_CHECKOUT))
//                    .eq("status", SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION)
                    .le("expiredDate", new Date())
                    .setOrderBy("expiredDate ASC").findList().forEach(so->{
                
                if (SalesOrder.ORDER_STATUS_CHECKOUT.equals(so.status)) {
                	so.isDeleted = true;
                } else {
                	so.status = SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT;
                	SalesOrder.revertPointExpiredPayment(so); // revert point from expired payment order;
                }
                so.update();

                so.salesOrderSellers.forEach(sos->{
                    sos.status = SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT;
                    sos.update();

                    Merchant merchant = sos.merchant;
                    if (merchant != null && !merchant.isHokeba()){
                        merchant.unpaidCustomer = merchant.getUnpaidCustomer() - sos.paymentSeller;
                        merchant.update();
                    }

                    sos.salesOrderDetail.forEach(sod->{
                        sod.status = SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT;
                        sod.update();

                        ProductDetailVariance product = sod.productVar;
                        product.totalStock = product.totalStock + sod.quantity;
                        product.update();

                        sod.voucherDetails.forEach(voucherDetail->{
                        	voucherDetail.status = 0;
                            voucherDetail.orderNumber = "";
                            voucherDetail.member = null;
                            voucherDetail.usedAt = null;
                            voucherDetail.update();
                        });
                    });
                });
            });

            txn.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
        }
        finally {
            txn.end();
        }
    }

    private void checkPushNotifBroadcast(){

        Transaction txn = Ebean.beginTransaction();
        try {
            SMSBlast.find.where().eq("t0.is_sent", false)
                    .le("date", new Date())
                    .setOrderBy("date ASC").findList().forEach(notif -> {
                notif.isSent = true;
                notif.sentAt = new Date();
                notif.update();

                notif.sendNotif();
            });

            txn.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
        }
        finally {
            txn.end();
        }
    }

    //odoo
    private void checkExpiredProduct(){
        Transaction txn = Ebean.beginTransaction();
        try {
            Product.find.where().eq("t0.is_deleted", false)
                    .gt("discountType", 0)
                    .le("discountActiveTo", new Date())
                    .setOrderBy("discountActiveTo ASC").findList().forEach(data->{

            	System.out.println("Inactivate Discount price for " + data.name);   
                data.discountType = 0;
                data.discount = 0D;
                data.discountActiveFrom = null;
                data.discountActiveTo = null;
                data.priceDisplay = data.price;
                Double price = data.priceDisplay;
                data.buyPrice = price - Math.floor(data.category.getShareProfit()/100 * price);
                data.update();
            });

            txn.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
        }
        finally {
            txn.end();
        }
    }
    
    private void checkActiveDiscountProduct(){
        Transaction txn = Ebean.beginTransaction();
        try {
        	Date currentDate = new Date();
            Product.find.where().eq("t0.is_deleted", false)
                    .gt("discountType", 0)
                    .le("discountActiveFrom", currentDate)
                    .ge("discountActiveTo", currentDate)
//                    .eq("t0.odoo_id", 1)
                    .setOrderBy("discountActiveFrom ASC").findList().forEach(data->{

//                data.discountType = 0;
//                data.discount = 0D;
//                data.discountActiveFrom = null;
//                data.discountActiveTo = null;
//                data.priceDisplay = data.price;
                System.out.println("Activate Discount price for " + data.name);
            	if(data.discountType == 1){
                    data.priceDisplay = data.price - data.discount;
                }else{
                    data.priceDisplay = data.price - Math.floor((data.price * (data.discount/100)));
                }
//            	data.odooId = 0;
                Double price = data.priceDisplay;
                data.buyPrice = price - Math.floor(data.category.getShareProfit()/100 * price);
                data.update();
            });

            txn.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
        }
        finally {
            txn.end();
        }
    }

    private void checkPendingAuthorization(){
        Transaction txn = Ebean.beginTransaction();
        try {
            int count = ProductReview.find.where().eq("approvedStatus", Product.PENDING).findRowCount();
            ConfigSettings configSumProductReview = ConfigSettings.find.where().eq("key","sum_pending_product_review")
                    .findUnique();
            configSumProductReview.value = String.valueOf(count);
            configSumProductReview.update();

            count = ArticleComment.find.where().eq("article.isDeleted", false).eq("status", ArticleComment.PENDING).findRowCount();
            ConfigSettings configSumArticleComment = ConfigSettings.find.where().eq("key","sum_pending_article_comment")
                    .findUnique();
            configSumArticleComment.value = String.valueOf(count);
            configSumArticleComment.update();

            count = ProductStockTmp.find.where().eq("approvedStatus", Product.PENDING).findRowCount();
            ConfigSettings configSumProductStock = ConfigSettings.find.where().eq("key","sum_pending_stock")
                    .findUnique();
            configSumProductStock.value = String.valueOf(count);
            configSumProductStock.update();

            count = ProductTmp.find.where().eq("approvedStatus", ProductTmp.PENDING).findRowCount();
            ConfigSettings configSumProduct = ConfigSettings.find.where().eq("key","sum_pending_product")
                    .findUnique();
            configSumProduct.value = String.valueOf(count);
            configSumProduct.update();

            count = MerchantPromoRequestProduct.find.where().eq("status", MerchantPromoRequestProduct.STATUS_PENDING)
                    .eq("request.promo.isDeleted",false).findRowCount();
            ConfigSettings configSumPromo = ConfigSettings.find.where().eq("key","sum_pending_promo_request")
                    .findUnique();
            configSumPromo.value = String.valueOf(count);
            configSumPromo.update();

            count = SalesOrderPayment.find.where().eq("status", SalesOrderPayment.PAYMENT_VERIFY).findRowCount();
            ConfigSettings configSumPayment = ConfigSettings.find.where().eq("key","sum_pending_payment_confirmation")
                    .findUnique();
            configSumPayment.value = String.valueOf(count);
            configSumPayment.update();

            count = Product.find.where().eq("productType", 3).eq("approvedStatus", Product.PENDING).findRowCount();
            ConfigSettings configSumProductMarketplace = ConfigSettings.find.where().eq("key","sum_pending_product_marketplace")
                    .findUnique();
            configSumProductMarketplace.value = String.valueOf(count);
            configSumProductMarketplace.update();

            txn.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
        }
        finally {
            txn.end();
        }
    }
}