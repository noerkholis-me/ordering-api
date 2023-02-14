package service;

import com.hokeba.util.MailConfig;

import models.Merchant;
import models.UserMerchant;

public class CallbackActivationService {
	
	public static void renderCallbackActivationMail(Merchant merchant) {
		Thread thread = new Thread( () -> {
        	try {
                MailConfig.sendmail(merchant.email, MailConfig.subjectSuccessActivation,
                		MailConfig.renderMailResendActivation(merchant));
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
}
