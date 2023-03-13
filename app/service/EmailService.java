package service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.hokeba.util.Constant;
import com.hokeba.util.MailConfig;

import dtos.store.StoreWithdrawEmail;
import models.Merchant;
import models.Store;
import models.UserMerchant;
import models.store.StoreAccess;
import models.store.StoreAccessDetail;
import models.transaction.Order;
import models.transaction.OrderPayment;
import play.Logger;
import repository.OrderPaymentRepository;
import repository.StoreAccessRepository;

public class EmailService {

    final static Logger.ALogger logger = Logger.of(EmailService.class);

    
	
	public static void renderCallbackActivationMail(Merchant merchant) {
		Thread thread = new Thread( () -> {
        	try {
                MailConfig.sendmail(merchant.email, MailConfig.subjectSuccessActivation,
                		MailConfig.renderMailResendActivation(merchant), "michaelrahayaan19@gmail.com");
			} catch (Exception e) {
				e.printStackTrace();
			}
        });
        thread.start();
	}
	
	public static void renderCallbackActivationMail(UserMerchant userMerchant) {
		Thread thread = new Thread( () -> {
        	try {
                MailConfig.sendmail(userMerchant.getEmail(), MailConfig.subjectSuccessActivation,
                		MailConfig.renderMailResendActivation(userMerchant));
			} catch (Exception e) {
				e.printStackTrace();
			}
        });
        thread.start();
	}

	public static void handleCallbackAndSendEmail(Order order, Boolean toAdmin) {
	
	        logger.info(">>> incoming requet...  ");
	        System.out.println("order >>> " + order.id);
	
	        System.out.println("order member " + order.getMember().id);
	        
	        
	        System.out.println("email >>> " + order.getMember().email);
	
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
//	        if (("virtual_account").equalsIgnoreCase(orderPayment.getPaymentType())) {
//	        	metodePembayaran = "Virtual Account";
//	        	logoPembayaran = "VA.png";
//	        } else if("qr_code".equalsIgnoreCase(orderPayment.getPaymentType())) {
//	        	metodePembayaran = "QRIS";
//	        	logoPembayaran = "QRIS.png";
//	        } else if("gopay".equalsIgnoreCase(orderPayment.getPaymentType())) {
//	        	metodePembayaran = "Gopay";
//	        	logoPembayaran = "Gopay.png";
//	        }
	        
	        
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
	                    MailConfig.sendmail(emails.get(i), MailConfig.subjectInvoiceAdmin, MailConfig.renderMailInvoiceTemplateAdmin(Constant.getInstance().getImageUrl()
	                    		,order, orderPayment), order.getStore().getMerchant().email);
	            		}
	            	} else {
	                    MailConfig.sendmail(order.getMember().email, MailConfig.subjectInvoice, MailConfig.renderMailInvoiceTemplateNew(Constant.getInstance().getImageUrl(),order, orderPayment));
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

	public static void renderMailInformationWithdraw (StoreWithdrawEmail dto, Merchant merchant) {
		List<String> emails = new ArrayList<>();
	emails.add(merchant.email);
	emails.add("admin@wgshub.com");
	try {
		List<Store> stores = Store.findAllStoreIsActiveByMerchant(merchant);
		for (Store store : stores) {
		    List<StoreAccessDetail> details = StoreAccessRepository.getDetailData(
		        StoreAccessRepository.findDetail.where()
		            .eq("t0.store_id", store.id)
		            .eq("t0.is_deleted", false)
		            .order("t0.id")
		    );
		    for (StoreAccessDetail detail : details) {
		        List<StoreAccess> accesses = StoreAccessRepository.getDataStoreAccess(
		            StoreAccessRepository.find
		                .where()
		                .eq("t0.is_deleted", false)
		                .eq("t0.id", detail.storeAccess.id)
		                .order("t0.id"),
		            "", "", 0, 10
		        );
		        for (StoreAccess access : accesses) {
		            emails.add(access.getUserMerchant().getEmail());
		        }
		    }
		}
		System.out.println("Emails: " + emails);

		
	} catch (Exception e) {
		e.printStackTrace();
	}

      Thread thread = new Thread ( () -> {
      	try {
      		for (int i = 0; i < emails.size(); i++) {
              	MailConfig.sendmail(emails.get(i), MailConfig.subjectWithdrawInformation,
              			MailConfig.renderInformationMailWithdraw(dto));
			}
          } catch (Exception e) {
          	
          }
      });
      thread.start();
	}

	public static void renderMailSuccessWithdraw (StoreWithdrawEmail dto, Merchant merchant) {
		List<String> emails = new ArrayList<>();
		emails.add(merchant.email);
		emails.add("admin@wgshub.com");
		
		try {
			List<Store> stores = Store.findAllStoreIsActiveByMerchant(merchant);
			for (Store store : stores) {
			    List<StoreAccessDetail> details = StoreAccessRepository.getDetailData(
			        StoreAccessRepository.findDetail.where()
			            .eq("t0.store_id", store.id)
			            .eq("t0.is_deleted", false)
			            .order("t0.id")
			    );
			    for (StoreAccessDetail detail : details) {
			        List<StoreAccess> accesses = StoreAccessRepository.getDataStoreAccess(
			            StoreAccessRepository.find
			                .where()
			                .eq("t0.is_deleted", false)
			                .eq("t0.id", detail.storeAccess.id)
			                .order("t0.id"),
			            "", "", 0, 10
			        );
			        for (StoreAccess access : accesses) {
			            emails.add(access.getUserMerchant().getEmail());
			        }
			    }
			}
			System.out.println("Emails: " + emails);

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread thread = new Thread ( () -> {
			try {
				for (int i = 0; i < emails.size(); i++) {
					MailConfig.sendmail(emails.get(i), MailConfig.subjectWithdrawSuccess, 
							MailConfig.renderSuccessMailWithdraw(dto));
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}
}
