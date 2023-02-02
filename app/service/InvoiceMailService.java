package service;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.hokeba.util.Constant;
import com.hokeba.util.MailConfig;
import models.Store;
import models.store.StoreAccess;
import models.store.StoreAccessDetail;
import models.transaction.Order;
import models.transaction.OrderPayment;
import play.Logger;
import repository.OrderPaymentRepository;
import repository.OrderRepository;
import repository.StoreAccessRepository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class InvoiceMailService {

    private final static Logger.ALogger logger = Logger.of(InvoiceMailService.class);

    private static InvoiceMailService instance;

    public static InvoiceMailService getInstance() {
        if (instance == null) {
            instance = new InvoiceMailService();
        }
        return instance;
    }
    

    public static String email = "";
    public static String emailCC = "";
    public static String subject = "";
    public static String metodePembayaran = "";
    public static String logoPembayaran = "";

    public static void handleCallbackAndSendEmail(Order order, Boolean toAdmin) {

        logger.info(">>> incoming requet...  ");
        System.out.println("order >>> " + order.id);

        System.out.println("order member " + order.getMember().id);
        if (toAdmin) {
        	emailCC = order.getStore().getMerchant().email;
        	subject = MailConfig.subjectInvoiceAdmin;
        } else {
        	email = order.getMember().email;
        	subject = MailConfig.subjectInvoice;
        }
        
        System.out.println("email >>> " + email);
        System.out.println("subject >>> " + subject);

//        String urlLogo = Constant.getInstance().getImageUrl() + "/" + "assets/images/hellobisnisnewlogo.png";
//        String urlEmailLogo = Constant.getInstance().getImageUrl() + "/" + "assets/images/email.png";
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy - HH : mm : ss", new Locale("id", "ID"));
        List<String> emails = new ArrayList<>();
        String orderDate = formatter.format(order.getOrderDate());
        String storeUrl = Constant.getInstance().getFrontEndUrl().concat(order.getStore().storeCode);
        String invoiceUrl = Constant.getInstance().getFrontEndUrl().concat(order.getStore().storeCode).concat("/")
        		.concat(order.getOrderNumber());
        Optional<OrderPayment> optionalOrderPayment = OrderPaymentRepository.findByOrderId(order.id);
        OrderPayment orderPayment = optionalOrderPayment.get();
        if (("virtual_account").equalsIgnoreCase(orderPayment.getPaymentType())) {
        	metodePembayaran = "Virtual Account";
        	logoPembayaran = "VA.png";
        } else if("qr_code".equalsIgnoreCase(orderPayment.getPaymentType())) {
        	metodePembayaran = "QRIS";
        	logoPembayaran = "QRIS.png";
        } else if("gopay".equalsIgnoreCase(orderPayment.getPaymentType())) {
        	metodePembayaran = "Gopay";
        	logoPembayaran = "Gopay.png";
        }
        
        
        if(toAdmin) {
        	Query<StoreAccessDetail> queryDetail = StoreAccessRepository.findDetail.where().
                    eq("t0.store_id", order.getStore().id).eq("t0.is_deleted", false).order("t0.id");
        	
            try {
                List<StoreAccessDetail> dataDetail = StoreAccessRepository.getDetailData(queryDetail);
                System.out.println("list Detail >>> "+dataDetail.size());
                for(StoreAccessDetail data1 : dataDetail) {
                	Query<StoreAccess> query = StoreAccessRepository.find.fetch("userMerchant").where()
                            .eq("t0.is_deleted", false).eq("t0.is_active", true).eq("t0.id", data1.storeAccess.id)
                            .order("t0.id desc");
                	List<StoreAccess> responseIndex = StoreAccessRepository.getDataStoreAccess(query, "", "", 0,
                            10);
                	System.out.println("Store Access >>> "+responseIndex.size());
                	for(StoreAccess data : responseIndex) {
                		System.out.println("User Merchant Email >>>>> " +data.getUserMerchant().email);
                		emails.add(data.getUserMerchant().email);
                	}
                	
                }

    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }
        
        Thread thread = new Thread(() -> {
            try {
            	if(toAdmin) {
            		for(int i = 0; i < emails.size(); i++) {
                    MailConfig.sendmail(emails.get(i), subject, MailConfig.renderMailInvoiceTemplateAdmin(orderDate, order.getStore().storeName,
                    		order.getStore().storeName, order.getStore().storePhone, order.getStore().storeAddress, order.getTotalBayar(),
                    		Constant.getInstance().getImageUrl(), storeUrl, metodePembayaran, logoPembayaran, order.getStore().getMerchant().fullName, invoiceUrl), emailCC);
            		}
            	} else {
                    MailConfig.sendmail(email, subject, MailConfig.renderMailInvoiceTemplateNew(Constant.getInstance().getImageUrl(),order, orderPayment));
            	}
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

        Transaction trx = Ebean.beginTransaction();
        try {
            if(toAdmin) {
            	orderPayment.setMailStatusCode("200");
	            orderPayment.setMailStatus("Success [ADMIN]");
	            orderPayment.setMailMessage("SENT TO ADMIN");
	            orderPayment.update();
            } else {
	            orderPayment.setMailStatusCode("200");
	            orderPayment.setMailStatus("Success");
	            orderPayment.setMailMessage("SENT");
	            orderPayment.update();
            }
            
            trx.commit();
        } catch (Exception e) {
            logger.error("Error saat mengirim invoice ke customer", e);
            e.printStackTrace();
            trx.rollback();
        } finally {
            trx.end();
        }
    }
    
}
