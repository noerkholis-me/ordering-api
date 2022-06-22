package com.hokeba.util;

import models.Member;
import models.Merchant;
import models.SalesOrder;
import models.UserCms;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import play.data.Form;

public class MailConfig {

	public static String user = Constant.getInstance().getEmailUser();
	public static String pass = Constant.getInstance().getEmailPassword();
	public static String smtp = Constant.getInstance().getEmailSmtp();
	public static String sender = Constant.getInstance().getEmailSender();

	public static String subjectForgotPassword = "[Sandbox] Forgot Password Verification";
	public static final String subjectActivation = "[Sandbox] Email Activation";
	public static final String subjectConfirmOrder = "[Sandbox] Order Confirmation";

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
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", smtp);
		props.put("mail.smtp.port", "587");
		props.put("mail.debug", "true");
		props.put("mail.smtp.sendpartial", "true");
		props.put("mail.smtp.ssl.enable", "false");
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
                .render(name, url + "/" + resetToken, Constant.getInstance().getImageUrl().concat("mail"))
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
			String html = views.html.verificationEmail.render(fullName, url).toString();
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
			String html = views.html.verificationEmail.render(fullName, url).toString();
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
			String html = views.html.verificationEmailChange.render(fullName, url).toString();
			return html;
		} catch (Exception ignored) {

		}
		return "";
	}

}
