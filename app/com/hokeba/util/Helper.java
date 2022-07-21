package com.hokeba.util;

import com.hokeba.nexmo.Nexmo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import play.Logger;
import play.Play;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//import org.apache.commons.mail.DefaultAuthenticator;
//import org.apache.commons.mail.HtmlEmail;

/**
 * Created by hendriksaragih on 11/8/16.
 */
public class Helper {
    private final static Logger.ALogger logger = Logger.of(Helper.class);
    private final static String SMTP = Play.application().configuration().getString("whizliz.email.smtp");
    private final static String SMTP_USER = Play.application().configuration().getString("whizliz.email.user");
    private final static String SMTP_PASSWORD = Play.application().configuration().getString("whizliz.email.password");
    private final static String APP_NAME = Play.application().configuration().getString("whizliz.name");
    private final static String APP_URL = Play.application().configuration().getString("whizliz.app_url");
    public final static String BASE_URL = Play.application().configuration().getString("whizliz.baseurl");
    public final static String CONTACT_EMAIL = Play.application().configuration().getString("whizliz.contact_email");
    public final static String CONTACT_NAME = Play.application().configuration().getString("whizliz.contact_name");
    public final static String BASE_IMAGE = Play.application().configuration().getString("whizliz.base_image");
    public final static String API_URL = Play.application().configuration().getString("whizliz.api.url");
    public final static String MERCHANT_URL = Play.application().configuration().getString("whizliz.merchant.url");
    public final static String BACKEND_URL = Play.application().configuration().getString("whizliz.cms.url");
    
    /*public static void email(String to, String name, String htmlMsg, String subject) throws Exception {
        // Create the email message
        HtmlEmail email = new HtmlEmail();
        email.setHostName(SMTP);
        email.setFrom(SMTP_USER, APP_NAME);
        email.setSubject(subject);
        email.addTo(to, name);
        email.setSmtpPort(465);
        email.setSSLOnConnect(true);
        email.setAuthenticator(new DefaultAuthenticator(SMTP_USER, SMTP_PASSWORD));
        email.setHtmlMsg(htmlMsg);

        // set the alternative message
        email.setTextMsg("Your email client does not support HTML messages");

        // send the email
        email.send();
        logger.debug("email sent");
        logger.info("email was sent");

    }*/

    public static String activateUserTemplate(String account_number, String type){
        return "<!DOCTYPE html> "
                +" <html> "
                +" <head> "
                +" 	<title>Email Template Kredit Plus</title> "
                +" 	<link href='https://fonts.googleapis.com/css?family=Roboto:400,100,100italic,300,300italic,700italic,400italic,500italic,500,700,900,900italic' rel='stylesheet' type='text/css'> "
                +" 	<link href='https://fonts.googleapis.com/css?family=Poppins:400,500,700' rel='stylesheet' type='text/css'> "
                +" 	<link href='https://fonts.googleapis.com/css?family=Montserrat:400,700' rel='stylesheet' type='text/css'> "
                +" </head> "
                +" <body style=\"background: #eee; margin: 0; font-family: 'Roboto', sans-serif;\"> "
                +" 	<table style=\"max-width: 600px; width: 100%; margin: auto; background: #fff; padding: 20px 35px;\" cellpadding=\"0\" cellspacing=\"0\"> "
                +" 		<tbody> "
                +" 			<tr> "
                +" 				<td align=\"center\"> "
                +" 					<div style=\"background: #f7f7f7; border: 5px solid #eeeeee; margin: 20px 0; padding: 20px 0; border-radius: 4px\"> "
                +" 						<p style=\"font-size: 15px; color: #666666; margin: 0 0 10px 0;\">Terimakasih anda telah mendaftar di Kredit Plus.</p> "
                +" 						<p style=\"font-size: 15px; color: #666666; font-weight: 600; margin: 0 0 10px 0;\">Untuk mengaktifkan akun Kredit Plus Anda, silahkan klik tombol berikut:</p> "
                +" 						</br> "
                +" 						</br> "
                +" 						<a href=" + APP_URL + "/#/verify-email/" + account_number + " target=\"_blank\" style=\"background: #ed1b24; font-family: 'Poppins', sans-serif; color: #fff; font-size: 14px; font-weight: 400; text-transform: uppercase; letter-spacing: 1px; padding: 10px 20px; border: none; border-radius: 4px; margin-top: 50px; cursor: pointer; text-decoration:none;\">Aktivasi</a> "
                +" 					</div> "
                +" 				</td> "
                +" 			</tr> "
                +" 			<tr> "
                +" 				<td> "
                +" 					<span style=\"color: #777777; font-size: 14px;\">Salam Hormat,</span> "
                +" 				</td> "
                +" 			</tr> "
                +" 			<tr> "
                +" 				<td> "
                +" 					<span style=\"color: #777777; font-size: 14px; font-weight: 600\">Kredit Plus</span> "
                +" 				</td> "
                +" 			</tr> "
                +" 		</tbody> "
                +" 	</table> "
                +" </body> "
                +" </html>";
    }

    public static String forgotPasswordUserTemplate(String reset_token){
        return "<html><p>Anda telah mengaktifkan lupa password.</p>" + "<a href=" + APP_URL
                + "/reset_password.html?TOKEN=" + reset_token + " target=\"_blank\">"
                + "Silahkan klik untuk merubah " + "password!</a></html>";
    }

    public static String forgotPasswordAdminTemplate(String reset_token){
        return "<html><p>Anda telah mengaktifkan lupa password.</p>" + "<a href=" + APP_URL
                + "/admin/reset_password.html?TOKEN=" + reset_token + " target=\"_blank\">"
                + "Silahkan klik untuk merubah " + "password!</a></html>";
    }

    public static String getVerificationCode(String reset_token){
        return "<html><p>Anda telah mengaktifkan request verifikasi kode.</p>" + "<a href=" + APP_URL
                + "/admin/aktivasi_registrasi.html?token=" + reset_token + " target=\"_blank\">"
                + "Silahkan klik untuk mengaktifkan user anda!</a></html>";
    }

    public static String getRandomString(){
        return RandomStringUtils.random(5, false, true).toUpperCase();
    }

    public static String getRandomString(int length){
        return getRandomString(length, false);
    }

    public static String getRandomString(int length, boolean letters){
        return RandomStringUtils.random(length, true, letters).toUpperCase();
    }

    public static void SendSMS(String nohp, String text){
        Nexmo.getInstance().post(convertPhone(nohp), text, Object.class);
    }

    private static String convertPhone(String nohp){
        if (nohp.startsWith("0")){
            return "62"+nohp.substring(1, nohp.length());
        }else{
            return nohp;
        }
    }

    public static String encodeImageToString(File file) {
        String imageString = null;
        try {
            byte[] imageBytes = IOUtils.toByteArray(file.toURI());
            imageString = Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

    public static String getDateFromTimeStamp(Date time) {
        SimpleDateFormat sdt = new SimpleDateFormat("MM/dd/yyyy");
        return sdt.format(time);
    }

    public static String getTimeFromTimeStamp(Date time) {
        SimpleDateFormat sdt = new SimpleDateFormat("HH:mm");
        return sdt.format(time);
    }

    public static Date getLast30day() {
        return getDate(addDate(nowFormat(), -30)+" 00:00:00");
    }

    public static Date getDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getDateYmd(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String nowFormat() {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(new Date());
    }

    public static String addDate(String date, int diff) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
            c.add(Calendar.DATE, diff);
            result = sdf.format(c.getTime());
        } catch (ParseException ignored) {

        }

        return result;
    }

    public static Date getEndCurrentDay() {
        return getDate(nowFormat()+" 23:59:59");
    }
    
    
    
    
    //TODO change class for helper?
    public static Date getCurrentDate() {
    	return new Date(System.currentTimeMillis());
    }
    
    public static Date fetchStartOfDate(Date date) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	cal.set(Calendar.HOUR, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	return cal.getTime();
    }
    
    public static Date fetchEndOfDate(Date date) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	cal.set(Calendar.HOUR, 23);
    	cal.set(Calendar.MINUTE, 59);
    	cal.set(Calendar.SECOND, 59);
    	cal.set(Calendar.MILLISECOND, 99);
    	return cal.getTime();
    }
    
    public static Date addDate(Date date, int dateField, int value) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	cal.add(dateField, value);
    	return cal.getTime();
    }
    
    public static String parseDateToString(Date date, String format) {
    	DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }
    
    public static Date parseStringToDate(String date, String format){
        DateFormat df = new SimpleDateFormat(format);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    //convert BigDecimal to Currency IDR
    public static String convertCurrencyIDR(BigDecimal value) {
        NumberFormat formatter = NumberFormat.getIntegerInstance(new Locale("id", "ID"));
        return formatter.format(value != null ? value : new BigDecimal(0) );
    }

    // is valid phone number with country code +62
    public static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^[0-9]{10,15}$";
        //String regex2 = "^(?:\\+?[0-9]){6,14}[0-9]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
    // get first name
    public static String getFirstName(String name) {
        String[] names = name.split(" ");
        if(names.length > 1) {
            return names[0];
        } else {
            return name;
        }
    }
    // get last name
    public static String getLastName(String fullName) {
        List<String> nameList = Arrays.stream(fullName.split(" ")).collect(Collectors.toList());
        final int[] nameIndex = {0};
        return nameList.stream().map(s-> {
            nameIndex[0]++;
            if(nameIndex[0] != 1 && nameIndex[0] < nameList.size()){
                return s.concat(" ");
            } else if(nameIndex[0] == nameList.size()){
                return s;
            } else {
                return "";
            }
        }).collect(Collectors.joining());
    }
}
