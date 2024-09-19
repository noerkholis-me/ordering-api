package com.hokeba.util;

import java.util.Properties;
import java.math.BigDecimal;
import com.hokeba.util.Helper;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import dtos.store.StoreWithdrawEmail;
import models.Member;
import models.Merchant;
import models.SalesOrder;
import models.UserMerchant;
import models.transaction.Order;
import models.transaction.OrderPayment;
import play.data.Form;
import repository.OrderPaymentRepository;

public class MailConfig {

	public static String user = Constant.getInstance().getEmailUser();
	public static String pass = Constant.getInstance().getEmailPassword();
	public static String smtp = Constant.getInstance().getEmailSmtp();
	public static String sender = Constant.getInstance().getEmailSender();

	public static String subjectForgotPassword = "[Sandbox] Forgot Password Verification";
	public static final String subjectActivation = "[Sandbox] Email Activation";
	public static final String subjectConfirmOrder = "[Sandbox] Order Confirmation";
	public static final String subjectInvoice = "[Sandbox] Invoice";
	public static final String subjectInvoiceAdmin = "[Sandbox] New Order Succcessfull";
	public static final String subjectSuccessActivation = "[Sandbox] Account Activation Success";
	public static final String subjectWithdrawSuccess = "[Sandbox] Withdraw Succsess";
	public static final String subjectWithdrawInformation = "[Sandbox] Withdraw Will Be Processed";

	// Using send grid api
	public static boolean sendmail2(String recipients, String subject, String contentTemplate) {
		try {
			EmailSendGrid emailGrid = new EmailSendGrid();
            emailGrid.create(recipients, subject, contentTemplate);
            emailGrid.send();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// Using Mail Gun Mail Server
	public static boolean sendmail(String recipients, String subject, String contentTemplate) {
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", smtp);
		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.store.protocol", "pop3");
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.debug.auth", "true");
		props.setProperty( "mail.pop3.socketFactory.fallback", "false");
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.starttls.enable", "true");
//		props.put("mail.smtp.host", smtp);
//		props.put("mail.smtp.port", "465");
//		props.put("mail.debug", "true");
//		props.put("mail.smtp.sendpartial", "true");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.trust", "*");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
			message.setSubject(subject);
			message.setContent(contentTemplate, "text/html; charset=utf-8");
			Transport.send(message, message.getAllRecipients());

			return true;
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean sendmail(String recipients, String subject, String contentTemplate, String emailCC) {
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", smtp);
		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.store.protocol", "pop3");
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.debug.auth", "true");
		props.setProperty( "mail.pop3.socketFactory.fallback", "false");
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.starttls.enable", "true");
//		props.put("mail.smtp.host", smtp);
//		props.put("mail.smtp.port", "465");
//		props.put("mail.debug", "true");
//		props.put("mail.smtp.sendpartial", "true");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.trust", "*");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailCC));
			message.setSubject(subject);
			message.setContent(contentTemplate, "text/html; charset=utf-8");
			Transport.send(message, message.getAllRecipients());

			return true;
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String renderMailInvoiceTemplate(String url, String urlEmailLogo, Order order) {
		return views.html.invoiceMail.render(url, urlEmailLogo, order)
				.toString();
	}
	
//	public static String renderMailInvoiceTemplateNew(String orderDate,String customerName, String nameStore, String phoneStore, String addressStore, String amount,
//			String url, String storeUrl, String metodePembayaran, String logoPembayaran, String invoiceUrl) {
//		String html = views.html.invoiceMailNew.render(orderDate, customerName, nameStore, phoneStore, addressStore, amount, url, 
//				storeUrl, metodePembayaran, logoPembayaran,invoiceUrl).toString();
//		return html;
//	}
	
	public static String renderMailInvoiceTemplateNew(String imagePath,Order order, OrderPayment orderPayment) {
		BigDecimal totalPrice = order.getTotalPrice(); // Get total price as BigDecimal
		double platformFeeValue = 0.0; // Initialize platformFee as an integer
		if(totalPrice.compareTo(BigDecimal.valueOf(10000)) <= 0) {
				platformFeeValue = totalPrice.multiply(BigDecimal.valueOf(0.05)).doubleValue();
		} else if(totalPrice.compareTo(BigDecimal.valueOf(25000)) <= 0) {
				platformFeeValue = 500.0;
		} else if(totalPrice.compareTo(BigDecimal.valueOf(150000)) <= 0) {
				platformFeeValue = 1000.0;
		} else if(totalPrice.compareTo(BigDecimal.valueOf(500000)) <= 0) {
				platformFeeValue = 1500.0;
		} else {
				platformFeeValue = 5000.0;
		}
		String platformFee = Helper.getRupiahFormat(platformFeeValue); 
		String html = views.html.invoiceMailNew.render(imagePath,order, orderPayment, platformFee).toString();
		return html;
	}
	
//	public static String renderMailInvoiceTemplateAdmin(String orderDate,String customerName, String nameStore, String phoneStore, String addressStore, String amount,
//			String url, String storeUrl, String metodePembayaran, String logoPembayaran, String merchantName, String invoiceUrl) {
//		String html = views.html.invoiceMailNewAdmin.render(orderDate, customerName, nameStore, phoneStore, addressStore, amount, url, 
//				storeUrl, metodePembayaran, logoPembayaran, merchantName, invoiceUrl).toString();
//		return html;
//	}
	
	public static String renderMailInvoiceTemplateAdmin(String imagePath,Order order, OrderPayment orderPayment) {
		String html = views.html.invoiceMailNewAdmin.render(imagePath,order, orderPayment).toString();
		return html;
	}
	
	public static String renderMailResendActivation(UserMerchant user) {
		String urlMiniPos = Constant.getInstance().getPosUrl();
		String urlCms = Constant.getInstance().getMerchantUrl();
		String imageUrl = Constant.getInstance().getImageUrl();
		return views.html.aktivationMailResend.render(imageUrl, user.getEmail(), user.getFullName(), urlMiniPos, urlCms).toString();
	}
	
	public static String renderMailResendActivation(Merchant user) {
		String urlMiniPos = Constant.getInstance().getPosUrl();
		String urlCms = Constant.getInstance().getMerchantUrl();
		String imageUrl = Constant.getInstance().getImageUrl();
		return views.html.aktivationMailResend.render(imageUrl, user.email, user.fullName, urlMiniPos, urlCms).toString();
	}

    public static String renderMailForgotPasswordTemplate(Member member, String url) {
        return views.html.forgotPasswordMail
                .render(member.fullName, url + "/" + member.resetToken, Constant.getInstance().getImageUrl().concat("mail"))
                .toString();

    }

    public static String renderMailConfirmOrder(Member member, String url, String hash, SalesOrder so) {
        return views.html.orderConfirm
                .render(member.fullName, url + "/" + hash, Constant.getInstance().getImageUrl().concat("mail"), so)
                .toString();

    }

    public static String renderMailForgotPasswordMerchantTemplate(String resetToken, String name, String url) {
        return views.html.ForgotPasswordEmail
                .render(name, url, Constant.getInstance().getImageUrl())
                .toString();

    }

	public static String renderMailActivationTemplate(Merchant merchant, String url) {
		try {
			return views.html.activationMail
					.render(merchant.fullName, url + "" + merchant.activationCode, Constant.getInstance().getImageUrl().concat("mail")).toString();
		} catch (Exception ignored) {

		}
		return "";
	}
	
	public static String renderMailActivationMember(Member member, String url) {
		try {
			return views.html.activationMail
					.render(member.fullName, url + "" + member.activationCode, Constant.getInstance().getImageUrl().concat("mail")).toString();
		} catch (Exception ignored) {

		}
		return "";
	}

	public static String renderMailSendCreatePasswordCMSTemplate(String activationCode, String fullName) {
		Merchant dt = new Merchant();
		Form<Merchant> formData = Form.form(Merchant.class).fill(dt);
		String url = Helper.MERCHANT_URL + "/account-activation/" + activationCode;

		try {
			String html = views.html.verificationEmail.render(fullName, url, Constant.getInstance().getImageUrl()).toString();
			return html;
		} catch (Exception ignored) {

		}
		return "";
	}

	public static String renderMailSendCreatePasswordBackendTemplate(String activationCode, String fullName) {
		Merchant dt = new Merchant();
		Form<Merchant> formData = Form.form(Merchant.class).fill(dt);
		String url = Helper.BACKEND_URL + "/account-activation/" + activationCode;

		try {
			String html = views.html.verificationEmail.render(fullName, url, Constant.getInstance().getImageUrl()).toString();
			return html;
		} catch (Exception ignored) {

		}
		return "";
	}

	public static String renderVerificationAccount(String activationCode, String fullName) {
		Merchant dt = new Merchant();
		Form<Merchant> formData = Form.form(Merchant.class).fill(dt);
		String url = Helper.API_URL + "/re/account-activation?token=" + activationCode;

		try {
			String html = views.html.verificationEmailChange.render(fullName, url, Constant.getInstance().getImageUrl()).toString();
			return html;
		} catch (Exception ignored) {

		}
		return "";
	}

	public static String renderVerificationAccountUser(String activationCode, String fullName, String deviceType) {
		Merchant dt = new Merchant();
		Form<Merchant> formData = Form.form(Merchant.class).fill(dt);
		String url = Helper.API_URL + "/re/account-activation?token=" + activationCode;
		if(deviceType != null && deviceType != ""){
			url = url+"&device=" + deviceType;
		}

		try {
			String html = views.html.verificationEmailChange.render(fullName, url, Constant.getInstance().getImageUrl()).toString();
			return html;
		} catch (Exception ignored) {

		}
		return "";
	}
	
	public static String renderSuccessMailWithdraw (StoreWithdrawEmail store) {
		try {
			String imageURL = Constant.getInstance().getImageUrl().concat("/assets");
			String html = views.html.withdrawMailSuccess.render(imageURL, store).toString();
			return html;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String renderInformationMailWithdraw (StoreWithdrawEmail store) {
		try {
			String imageURL = Constant.getInstance().getImageUrl().concat("/assets");
			String html = views.html.withdrawMailInformation.render(imageURL, store).toString();
			return html;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
