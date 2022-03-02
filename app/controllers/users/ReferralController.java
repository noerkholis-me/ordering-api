package controllers.users;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Result;
import models.Member;

import com.wordnik.swagger.annotations.Api;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;

import controllers.BaseController;
import models.Member;

@Api(value = "/users/memberreferral", description = "Member Referal")
	public class ReferralController extends BaseController {

	@SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }
    
    static String generateReferralCodeTemp() {
    	 String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
         StringBuilder salt = new StringBuilder();
         Random rnd = new Random();
         while (salt.length() < 4) { // length of the random string.
             int index = (int) (rnd.nextFloat() * SALTCHARS.length());
             salt.append(SALTCHARS.charAt(index));
         }
         
         String saltStr = salt.toString();
         //return saltStr;
         
         String SALTNUM = "123456789";
         StringBuilder saltNum = new StringBuilder();
         Random rndNum = new Random();
         while (saltNum.length() < 2) { // length of the random string.
             int index = (int) (rndNum.nextFloat() * SALTNUM.length());
             saltNum.append(SALTNUM.charAt(index));
         }
         
         String saltStrNum = saltNum.toString();
         //return saltStrNum;
         
         LocalDateTime myDateObj = LocalDateTime.now();
         DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HHmmss");
         String formattedDate = myDateObj.format(myFormatObj);
         //System.out.println("After formatting: " + formattedDate);
         
         //Base64.Encoder enc = Base64.getEncoder();
         String str = saltStr+formattedDate+saltStrNum;
         
         
         // encode data using BASE64
        // String encoded = enc.encodeToString(str.getBytes());
        // System.out.println("encoded value is \t"+ saltStr+ str);
 		//return str;
 		return str;
    }
    
    public static Result GenerateCode() {
    	
    	
        // menampilkan list member dan cek referral code
        List<Member> listMember  = Member.find.where().eq("referral_code", null).findList();
        
        int i = 1;
        // update jika tidak ada referral code
        for (final Member member : listMember) {
        Transaction txn = Ebean.beginTransaction();
           try {
    	   String email = member.email;
    	   String referral_code=generateReferralCodeTemp();
    	   System.out.println("Update Email : "+email+" menjadi "+referral_code);
    	   //ObjectMapper mapper = new ObjectMapper();
           Member model = Member.find.where().eq("email", email).setMaxRows(1).findUnique();
           model.referral_code = referral_code;
           model.update();
           txn.commit();
           } catch (Exception e) {
               e.printStackTrace();
               txn.rollback();
           } finally {
               txn.end();
           }
           
           System.out.println("berhasil update");
       	   i++;
        }
        
    	//System.out.println(generateReferralCodeTemp());
    	
    	response.setBaseResponse(0, 0, 0, "Success", "total : " + i);
        return ok(Json.toJson(response));
    }
    
}
