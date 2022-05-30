package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import com.hokeba.util.Encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
@Table(name = "member")
public class Member extends BaseModel {
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "blacklist_email";

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    public String password;

    @JsonProperty("first_name")
    public String firstName;
    @JsonProperty("last_name")
    public String lastName;

    @JsonProperty("full_name")
    public String fullName;
    @JsonProperty("email")
    @Column(unique = true)
    public String email;
    
    public String token;
    public Date tokenExpireTime;
    
    @Column(length = 6)
    public String otp;
    public Date otpExpireTime;
    
    @JsonProperty("username")
    @Column(unique = true)
    public String username;
    public String emailNotifikasi;
    @JsonProperty("thumbnail_image_url")
    public String thumbnailImageUrl;
    @JsonProperty("medium_image_url")
    public String mediumImageUrl;
    @JsonProperty("large_image_url")
    public String largeImageUrl;
    @Column(unique = true)
    public String phone;

    @Size(max = 1)
    @Column(length = 1)
    public String gender;
    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    public Date birthDate;

    @JsonProperty("billing_address_id")
    public String billingAddressId;
    @Column(unique = true)
    @JsonProperty("facebook_user_id")
    public String facebookUserId;
    @Column(unique = true)
    @JsonProperty("google_user_id")
    public String googleUserId;
    @Column(unique = true)
    @JsonProperty("apple_user_id")
    public String appleUserId;

    @JsonIgnore
    @Column(name = "activation_code")
    public String activationCode;
    @JsonProperty("is_active")
    @Column(name = "is_active")
    public boolean isActive;

    @JsonProperty("news_letter")
    @Column(name = "news_letter")
    public Boolean newsLetter;

    @JsonIgnore
    @Column(name = "reset_token")
    public String resetToken;

    @JsonIgnore
    @Column(name = "reset_time")
    public Long resetTime;

    @JsonIgnore
    @Column(name = "code_expire")
    public Date codeExpire;

    @Transient
    @JsonProperty("billing_address")
    public Address billingAddress;

    @Transient
    @JsonProperty("shipping_address")
    public Address shippingAddress;

    @JsonProperty("has_password")
    public boolean hasSetPassword() {
        return (this.password != null);
    }

    @JsonProperty("birth_day")
    public String getBirthDay() {
        return CommonFunction.getDate(birthDate);
    }

    @Transient
    @JsonProperty("orders")
    public List<SalesOrder> orders;
//    public List<SalesOrder> getOrder() {
//        return SalesOrder.getOrderByMember(id);
//    }

    // @JsonProperty("is_subscribe")
    // public boolean isSubscribe;// TODO

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    public List<Address> addresses = new ArrayList<Address>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonIgnore
    public List<MemberLog> logs = new ArrayList<MemberLog>();

    @Column(name = "last_login")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date lastLogin;

    @Column(name = "last_purchase")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date lastPurchase;
    
    @JsonIgnore
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    public List<LoyaltyPoint> loyaltyPoint;
//    public List<LoyaltyPoint> loyaltyPoint = new ArrayList<LoyaltyPoint>();

    //odoo
    @Column(name = "odoo_id")
    public Integer odooId;

    @JsonProperty("referral_code")
    @Column(name = "referral_code")
    public String referral_code;

    @JsonIgnore
    @OneToMany(mappedBy = "referrer")
    public List<MemberReferral> referrer;
    
    @JsonIgnore
    @OneToMany(mappedBy = "referral")
    public List<MemberReferral> referral;

    public static Finder<Long, Member> find = new Finder<Long, Member>(Long.class, Member.class);

    public static Member findDataCustomer(String email, String phoneNumber){
        return find.where().raw("t0.email = '" + email + "' or t0.phone = '" + phoneNumber + "'").eq("t0.is_deleted", false).findUnique();
    }

    public Member() {

    }

    public Member(String password, String firstName, String lastName, String emailAddress, String phone, String gender,
                  String birthDate, String billingAddressId, List<Address> addresses, boolean active, boolean newsLetter) throws ParseException {
        super();
        this.password = Encryption.EncryptAESCBCPCKS5Padding(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + ((lastName != null && !lastName.equals("")) ? " " + lastName : "");
        this.emailNotifikasi = this.email = emailAddress;
        this.phone = phone;
        this.gender = gender;
        this.birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
        this.billingAddressId = billingAddressId;
        this.addresses = addresses;
        this.activationCode = "";
        this.isActive = active;
        this.newsLetter = newsLetter;

    }

    public Member(String password, String firstName, String lastName, String emailAddress, String phone, String gender,
                  String birthDate, String billingAddressId, List<Address> addresses, boolean active, boolean newsLetter, Integer odooId) throws ParseException {
        super();
        this.password = Encryption.EncryptAESCBCPCKS5Padding(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + ((lastName != null && !lastName.equals("")) ? " " + lastName : "");
        this.emailNotifikasi = this.email = emailAddress;
        this.phone = phone;
        this.gender = gender;
        this.birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
        this.billingAddressId = billingAddressId;
        this.addresses = addresses;
        this.activationCode = "";
        this.isActive = active;
        this.newsLetter = newsLetter;
//            this.odooId = odooId;


    }

    public Member(String password, String fullName, String emailAddress, String username, String phone, String gender,
                  String birthDate, Boolean newsLetter, String googleId, String fbId) throws ParseException {
        super();
        String[] split = fullName.split(" ");
        String firstName = split[0];
        String lastName = " " + firstName;
        if (split.length > 1){
             lastName = fullName.replaceFirst(firstName+" ", "");
        }

        this.password = Encryption.EncryptAESCBCPCKS5Padding(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.emailNotifikasi = this.email = emailAddress;
        this.username = username;
        this.newsLetter = newsLetter;
        if (!phone.isEmpty()){
            this.phone = phone;
        }
        this.gender = gender;
        if (!birthDate.isEmpty()){
            this.birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    	Date date = new Date();
    	String actCode = email+formatter.format(date);
        this.activationCode = Encryption.EncryptAESCBCPCKS5Padding(actCode);
        LocalDateTime exp = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(1);
		Date expCode = Date.from(exp.atZone(ZoneId.systemDefault()).toInstant());
        this.codeExpire = expCode;
        this.isActive = false;
        if (googleId != null && !googleId.isEmpty()){
            this.googleUserId = googleId;
        }
        if (fbId != null && !fbId.isEmpty()){
            this.facebookUserId = fbId;
        }

    }
    
    public Member(String password, String fullName, String emailAddress, String username, String phone, String gender,
            String birthDate, Boolean newsLetter, String googleId, String fbId, String referral_code) throws ParseException {
		  super();
		  String[] split = fullName.split(" ");
		  String firstName = split[0];
		  String lastName = " " + firstName;
		  if (split.length > 1){
		       lastName = fullName.replaceFirst(firstName+" ", "");
		  }
		
		  this.password = Encryption.EncryptAESCBCPCKS5Padding(password);
		  this.firstName = firstName;
		  this.lastName = lastName;
		  this.fullName = fullName;
		  this.emailNotifikasi = this.email = emailAddress;
		  this.username = username;
		  this.newsLetter = newsLetter;
		  if (!phone.isEmpty()){
		      this.phone = phone;
		  }
		  this.gender = gender;
		  if (!birthDate.isEmpty()){
		      this.birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
		  }
		  SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date date = new Date();
			String actCode = email+formatter.format(date);
		  this.activationCode = Encryption.EncryptAESCBCPCKS5Padding(actCode);
		  LocalDateTime exp = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(1);
			Date expCode = Date.from(exp.atZone(ZoneId.systemDefault()).toInstant());
		  this.codeExpire = expCode;
		  this.isActive = false;
		  if (googleId != null && !googleId.isEmpty()){
		      this.googleUserId = googleId;
		  }
		  if (fbId != null && !fbId.isEmpty()){
		      this.facebookUserId = fbId;
		  }
		  
		  this.referral_code = referral_code;
		
        }
        
        public Member(String emailAddress, String phone, String fullName) throws ParseException {
		  super();
		  String[] split = fullName.split(" ");
		  String firstName = split[0];
		  String lastName = " " + firstName;
		  if (split.length > 1){
		       lastName = fullName.replaceFirst(firstName+" ", "");
		  }
		
		  this.firstName = firstName;
		  this.lastName = lastName;
		  this.fullName = fullName;
		  this.emailNotifikasi = this.email = emailAddress;
		  if (!phone.isEmpty()){
		      this.phone = phone;
		  }
		  SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date date = new Date();
			String actCode = email+formatter.format(date);
		  this.activationCode = Encryption.EncryptAESCBCPCKS5Padding(actCode);
		  LocalDateTime exp = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(1);
			Date expCode = Date.from(exp.atZone(ZoneId.systemDefault()).toInstant());
		  this.codeExpire = expCode;
		  this.isActive = true;
		  
		  this.referral_code = referral_code;
		
		}
    
    

    public Member(String phone,String code) {
        super();
        this.activationCode = code;
        this.phone = phone;
    }

    public Member(String email, String password, String firstName, String lastName) {
        super();
        this.password = Encryption.EncryptAESCBCPCKS5Padding(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + ((lastName != null && !lastName.equals("")) ? " " + lastName : "");
        this.email = email;
        this.activationCode = Encryption.EncryptAESCBCPCKS5Padding(email);
        this.isActive = true;

    }

    public static Member login(String email, String password) {
        String encPassword = Encryption.EncryptAESCBCPCKS5Padding(password);
        Member member = Member.find.where().and(Expr.eq("email", email), Expr.eq("password", encPassword))
                .setMaxRows(1).findUnique();
        return member;

    }

    public static Member loginByPhone(String phone, String password) {
        String encPassword = Encryption.EncryptAESCBCPCKS5Padding(password);
        Member member = Member.find.where().and(Expr.eq("phone", phone), Expr.eq("password", encPassword))
                .setMaxRows(1).findUnique();
        return member;
    }
    
    public static Member loginByUsername(String username, String password) {
    	String encPassword = Encryption.EncryptAESCBCPCKS5Padding(password);
    	Member member = Member.find.where().and(Expr.eq("username", username), Expr.eq("password", encPassword))
    			.setMaxRows(1).findUnique();
    	return member;
    }


    // validate email and password when create new member
    public static String validation(String email, String username, String password, String confPassword, String phone, String fullName) {
        if (email != null && !email.matches(CommonFunction.emailRegex)) {
            return "Email format not valid.";
        }
//        if (username != null && !username.matches(CommonFunction.usernameRegex)) {
//        	return "Username format not valid";
//        }
        String[] mails = email.split("@");
        Integer row = BlacklistEmail.find.where().eq("name", "@"+mails[1]).eq("is_deleted", false).findRowCount();
        if (row > 0){
            return "The email service provider that you are using can not be used in Whizliz Please use another email service provider.";
        }
        Member member = Member.find.where().eq("email", email).setMaxRows(1).findUnique();
        if (member != null) {
            return "The email is already registered.";
        }
//        Member memberUsername = Member.find.where().eq("username", username).setMaxRows(1).findUnique();
//        if (memberUsername != null) {
//        	return "The username is already registered";
//        }
//        if (!CommonFunction.passwordValidation(password)) {
//            return "Password must be at least 8 character, has no whitespace, "
//                    + "and have at least 3 variations from uppercase, lowercase, number, or symbol";
//        }

        if (!phone.isEmpty()){
            if (!phone.matches(CommonFunction.phoneRegex)){
                return "Phone format not valid.";
            }
            Member memberPhone = Member.find.where().eq("phone", phone).setMaxRows(1).findUnique();
            if (memberPhone != null) {
                return "The phone is already registered.";
            }
        }
        if (!CommonFunction.passwordValidation(password)) {
            return "Password must be at least 8 character";
        }
        if (!confPassword.equals(password)) {
            return "Password and confirm password did not match.";
        }
        if(!fullName.isEmpty()) {
        	if(!fullName.matches(CommonFunction.nameRegex)) {
        		return "Name format not valid.";
        	}
        }
        return null;
    }
    

    public static String validation(Long id, String email, String phone) {
        if (email != null && !email.matches(CommonFunction.emailRegex)) {
            return "Email format not valid.";
        }
        String[] mails = email.split("@");
        Integer row = BlacklistEmail.find.where().eq("name", "@"+mails[1]).eq("is_deleted", false).findRowCount();
        if (row > 0){
            return "The email service provider that you are using can not be used in Whizliz Please use another email service provider.";
        }
        Member member = Member.find.where().eq("email", email).ne("id", id).setMaxRows(1).findUnique();
        if (member != null) {
            return "The email is already registered.";
        }

        if (!phone.isEmpty()){
            if (!phone.matches(CommonFunction.phoneRegex)){
                return "Phone format not valid.";
            }
            Member memberPhone = Member.find.where().eq("phone", phone).ne("id", id).setMaxRows(1).findUnique();
            if (memberPhone != null) {
                return "The phone is already registered.";
            }
        }

        return null;
    }

    public static String validation(String email, String phone, String fullName) {
        if (email != null && !email.matches(CommonFunction.emailRegex)) {
            return "Email format not valid.";
        }
        String[] mails = email.split("@");
        Integer row = BlacklistEmail.find.where().eq("name", "@"+mails[1]).eq("is_deleted", false).findRowCount();
        if (row > 0){
            return "The email service provider that you are using can not be used in Whizliz Please use another email service provider.";
        }
        Member member = Member.find.where().eq("email", email).setMaxRows(1).findUnique();
        if (member != null) {
            return "The email is already registered.";
        }

        if (!phone.isEmpty()){
            if (!phone.matches(CommonFunction.phoneRegex)){
                return "Phone format not valid.";
            }
            Member memberPhone = Member.find.where().eq("phone", phone).setMaxRows(1).findUnique();
            if (memberPhone != null) {
                return "The phone is already registered.";
            }
        }

        if(!fullName.isEmpty()) {
        	if(!fullName.matches(CommonFunction.nameRegex)) {
        		return "Name format not valid.";
        	}
        }

        return null;
    }
    
    
    public static String validation(Long id, String name) {
        if (name != null && !name.matches(CommonFunction.nameRegex)) {
            return "Name format not valid.";
        }
        return null;
    }

    public static String validation(String phoneNumber) {
        Member member = Member.find.where().eq("phone", phoneNumber).setMaxRows(1).findUnique();
        if (member != null && member.isActive == false) {
            return "Phone number is already registered but not active.";
        }
        if (member != null) {
            return "Phone number is already registered.";
        }
        return null;
    }
    
    public static String updateProfileValidation(Long id, String name, String phoneNumber) {
    	if (name != null && !name.matches(CommonFunction.nameRegex)) {
            return "Name format not valid.";
        }
    	if (!phoneNumber.isEmpty()) {
	    	if (!phoneNumber.matches(CommonFunction.phoneRegex)){
	            return "Phone format not valid.";
	        }
	    	Member member = Member.find.where().ne("id", id).eq("phone", phoneNumber).setMaxRows(1).findUnique();
	        if (member != null && member.isActive == false) {
	            return "Phone number is already registered but not active.";
	        }
	        if (member != null) {
	            return "Phone number is already registered.";
	        }
    	}
        return null;
    }

    public static String verification(String phone, String code) {
        Member member = Member.find.where().eq("phone", phone).findUnique();
        if (member != null && !member.activationCode.equals(code)) {
            return "Wrong verification code";
        }
        if (member == null) {
            return "You must register first";
        }
        if (System.currentTimeMillis()>member.codeExpire.getTime()){
            return "Your code is expired";
        }
        if(member.isActive){
            return "Your account is already active";
        }
        return null;
    }


    public static String validation(String firstName, String email, String username, String birthDate, String gender, String password, String fullName) {
        if (!email.matches(CommonFunction.emailRegex)) {
            return "Email format not valid.";
        }
        if (!fullName.matches(CommonFunction.nameRegex)) {
        	return "Name format not valid.";
        }
        if (!username.matches(CommonFunction.usernameRegex)) {
        	return "Username format not valid";
        }
        Member member = Member.find.where().eq("email", email).setMaxRows(1).findUnique();
        if (member != null) {
            return "The email is already registered.";
        }
        Member memberUsername = Member.find.where().eq("username", username).setMaxRows(1).findUnique();
        if (memberUsername != null) {
        	return "The username is already registered";
        }
        if (firstName.equals("")) {
            return "First name must not empty";
        }
        if (!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F")) {
            return "Input gender is not valid";
        }
        if (!CommonFunction.passwordValidation(password)) {
            return "Password must be at least 8 character, has no whitespace, "
                    + "and have at least 3 variations from uppercase, lowercase, number, or symbol";
        }
        // if (!birthDate.matches(
        // "(^(((0[1-9]|1[0-9]|2[0-8])[\\/](0[1-9]|1[012]))|((29|30|31)[\\/](0[13578]|1[02]))|((29|30)[\\/](0[4,6,9]|11)))[\\/](19|[2-9][0-9])\\d\\d$)|(^29[\\/]02[\\/](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)"))
        // {
        // return "Birthday format is invalid";
        // }
        return null;
    }

    public String changePassword(String oldPass, String newPass, String conPass)
            throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String encPass = Encryption.EncryptAESCBCPCKS5Padding(oldPass);
        if (encPass.equals(this.password)) {
            if (!oldPass.equals(newPass)) {
                if (CommonFunction.passwordValidation(newPass)) {
                    if (conPass.equals(newPass)) {
                        Transaction txn = Ebean.beginTransaction();
                        try {
                            this.password = Encryption.EncryptAESCBCPCKS5Padding(newPass);
                            this.save();
                            Update<MemberLog> upd = Ebean.createUpdate(MemberLog.class,
                                    "UPDATE member_log SET is_active=:isActive WHERE is_active=true and member_id=:memberId");
                            upd.set("isActive", true);
                            upd.set("memberId", this.id);
                            upd.execute();
                            txn.commit();
                            return null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            txn.rollback();
                        } finally {
                            txn.end();
                        }
                        return "500";
                    }
                    return "Password does not match the confirm password";
                }
                return "Password must be at least 8 character, has no whitespace, "
                        + "and have at least 3 variations from uppercase, lowercase, number, or symbol";
            }
            return "Your new password must be different";
        }
        return "Invalid old password";
    }

    public String changePassword(String newPass, String conPass)
            throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String validation = CommonFunction.passwordValidation(newPass, conPass);
        if (validation == null) {
            Transaction txn = Ebean.beginTransaction();
            try {
                this.password = Encryption.EncryptAESCBCPCKS5Padding(newPass);
                this.save();
                Member.removeAllToken(this.id);
                txn.commit();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
            return "500";
        }
        return validation;
    }

    public static void removeAllToken(Long id) {
        Update<MemberLog> upd = Ebean.createUpdate(MemberLog.class,
                "UPDATE member_log SET is_active=:isActive WHERE is_active=true and member_id=:memberId");
        upd.set("isActive", false);
        upd.set("memberId", id);
        upd.execute();
    }

    public static Page<Member> page(int page, int pageSize, String sortBy, String order, String name, int status) {
        ExpressionList<Member> qry = Member.find
                .where()
                .ilike("full_name", "%" + name + "%")
                .eq("is_deleted", false);

        if (status >= 0){
            qry.eq("is_active", status==1);
        }

        return
                qry.orderBy(sortBy + " " + order)
                    .findPagingList(pageSize)
                    .setFetchAhead(false)
                    .getPage(page);
    }



    public static Page<SalesOrder> pageOrderHistory(Long memberId, int page, int pageSize, String sortBy, String order, String name) {
        ExpressionList<SalesOrder> qry = SalesOrder.find
                .where()
                .ilike("orderNumber", "%" + name + "%")
                .eq("t0.is_deleted", false)
                .eq("member.id", memberId);

//        if(!filter.equals("")){
//            qry.eq("t0.status", filter);
//        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);

    }
    public static Integer findRowCountOrderHistory(Long memberId) {
        return SalesOrder.find.where().eq("is_deleted", false).eq("member.id", memberId).findRowCount();
    }

    public static Member findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    public Boolean getNewsLetter() {
        return newsLetter == null ? true : newsLetter;
    }

    public static Integer RowCount() {
        return find.where().eq("is_deleted", false).findRowCount();
    }

    public String getRegisterDate(){
        return CommonFunction.getDateTime(createdAt);
    }

    public String getLastLogin(){
        return CommonFunction.getDateTime(lastLogin);
    }

    public String getLastPurchase(){
        return CommonFunction.getDateTime(lastPurchase);
    }

    public String getBirthDate(){
        return CommonFunction.getDate(birthDate);
    }

    public String getThumbnailImageLink(){
        return thumbnailImageUrl==null || thumbnailImageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + thumbnailImageUrl;
    }
}