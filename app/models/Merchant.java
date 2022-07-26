package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.mapping.request.MapMerchantRegister;
import com.hokeba.mapping.request.MapMerchantUpdateProfile;
import com.hokeba.mapping.request.MapShippingId;
import com.hokeba.mapping.response.MapKeyValue;
import com.hokeba.mapping.response.MapMerchantPayment;
import com.hokeba.mapping.response.MapPaymentMethod;
import com.hokeba.mapping.response.MapProductRatting;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import com.hokeba.util.Encryption;
import dtos.FeatureAndPermissionSession;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import lombok.*;

@Entity
@Table(name="merchant")
public class Merchant extends BaseModel{
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECT = "REJECT";

	@JsonIgnore
    public String password;
    @JsonProperty("id")
    public Long id;
    @JsonProperty("email")
    @Column(unique = true)
    public String email;
    @Column(name = "birth_date")
    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    public Date birthDate;
    @Size(max = 1)
    @Column(length = 1)
    public String gender;
    @JsonProperty("full_name")
    public String fullName;
    @JsonProperty("domain")
    @Column(unique = true)
    public String domain;
    
    @JsonProperty("account_number")
    public String accountNumber;
    @JsonProperty("account_alias")
    public String accountAlias;
    
    
    @JsonIgnore
    public boolean ownMerchant;

	@JsonProperty("merchant_code")
	public String merchantCode;
	
	public String name;
	public String logo;
	public boolean display;
	public String type;
	@JsonProperty("company_name")
	public String companyName;
	public String status;
	
	@JsonProperty("city_name")
	public String cityName;
	@JsonProperty("postal_code")
	public String postalCode;
	// public String province;
	@JsonProperty("commission_type")
	public String commissionType;
	public String address;
	public String phone;
	
	@JsonProperty("meta_description")
	public String metaDescription;
	public String story;
	public String url;
	@JsonProperty("merchant_url_page")
	public String merchantUrlPage;
	public boolean anchor;
	@JsonProperty("url_banner")
	public String urlBanner;
	
	@JsonProperty("quick_response")
	public Long quickResponse;
	@JsonProperty("product_availability")
	public Long productAvailability;
	@JsonProperty("product_quality")
	public Long productQuality;
	public Double rating;
    @JsonProperty("count_rating")
    public int countRating;
	
	@JsonProperty("product_handled_and_shipped_description")
	public String productHandledAndShippedDescription;
	@JsonProperty("product_handled_description")
	public String productHandledDescription;
	@JsonProperty("product_shipped_description")
	public String productShippedDescription;

    @JsonIgnore
    @Column(name = "activation_code")
    public String activationCode;
    @JsonProperty("is_active")
    @Column(name = "is_active")
    public boolean isActive;

    @ManyToOne
    public District district;
    @ManyToOne
    public Township township;
    @ManyToOne
    public Region region;
    @ManyToOne
    public Village village;
    @ManyToOne
    public CourierPointLocation courierPointLocation;

    @ManyToOne
    public ShipperProvince province;
    @ManyToOne
    public ShipperCity city;
    @ManyToOne
    public ShipperSuburb suburb;
    @ManyToOne
    public ShipperArea area;

    @JsonProperty("couriers")
    @ManyToMany
    public List<Courier> couriers;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @JsonIgnore
    @Column(name = "reset_token")
    public String resetToken;

    @Column(name = "reset_time")
    public Long resetTime;

    @Column(name = "code_expire")
    public Date codeExpire;

    //odoo
    @JsonIgnore
    @Column(name = "odoo_id")
    public Integer odooId;

    public Double balance;
    @Column(name = "unpaid_customer")
    @JsonProperty("unpaid_customer")
    public Double unpaidCustomer;
    @Column(name = "unpaid_hokeba")
    @JsonProperty("unpaid_hokeba")
    public Double unpaidHokeba;
    @Column(name = "paid_hokeba")
    @JsonProperty("paid_hokeba")
    public Double paidHokeba;

    // FOR ADDITIONAL FEATURE
    @Column(name = "is_pos")
    @JsonProperty("is_pos")
    public boolean isPos;

    // CASH TYPE
    @Column(name = "is_cash")
    @JsonProperty("is_cash")
    public boolean isCash;
    @Column(name = "type_cash")
    @JsonProperty("type_cash")
    public String typeCash;

    // DEBIT / CREDIT TYPE
    @Column(name = "is_debit_credit")
    @JsonProperty("is_debit_credit")
    public boolean isDebitCredit;
    @Column(name = "type_debit_credit")
    @JsonProperty("type_debit_credit")
    public String typeDebitCredit;

    // QRIS TYPE
    @Column(name = "is_qris")
    @JsonProperty("is_qris")
    public boolean isQris;
    @Column(name = "type_qris")
    @JsonProperty("type_qris")
    public String typeQris;

    @Column(name = "is_kiosk")
    @JsonProperty("is_kiosk")
    public boolean isKiosk;

    @Column(name = "is_mobile_qr")
    @JsonProperty("is_mobile_qr")
    public boolean isMobileQr;

    @javax.persistence.Transient
    @JsonProperty("code")
    public String getCode(){
        return merchantCode;
    }

    @javax.persistence.Transient
    @JsonProperty("type")
    public String getType(){
        return "MERCHANT";
    }

    @javax.persistence.Transient
    @JsonProperty("seller_reviews")
    public List<SellerReview> getSellerReviews(){
        return SellerReview.getReview("merchant", id);
    }

    @javax.persistence.Transient
    @JsonProperty("order_stat")
    public List<MapKeyValue> orderStat = new ArrayList<>();

    @javax.persistence.Transient
    @JsonProperty("lists")
    public List<MapMerchantPayment> lists = new ArrayList<>();

    @ManyToOne(cascade = { CascadeType.ALL })
    public Role role;

    @Column(name = "total_active_balance")
    public BigDecimal totalActiveBalance;

    public Merchant(String password, String email, String gender, String fullName, String name, String companyName, String status, String cityName, String address, String phone, boolean isActive) {
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.fullName = fullName;
        this.name = name;
        this.companyName = companyName;
        this.status = status;
        this.cityName = cityName;
        this.address = address;
        this.phone = phone;
        this.isActive = isActive;
    }

    public void setOrderStat(){
        orderStat.add(new MapKeyValue("Successful Transactions", String.valueOf(SalesOrderSeller.getOrderByStatus(id,
                Arrays.asList(SalesOrder.ORDER_STATUS_VERIFY)))));
        orderStat.add(new MapKeyValue("Product Sold", String.valueOf(Product.getNumberOfSold(id))));
        orderStat.add(new MapKeyValue("Shipping Success", String.valueOf(SalesOrderSeller.getOrderByStatus(id,
                SalesOrder.ORDER_COMPLETED))));
        orderStat.add(new MapKeyValue("Shipping Failed", String.valueOf(SalesOrderSeller.getOrderByStatus(id,
                SalesOrder.ORDER_FAILED))));
    }

    @JsonGetter("couriers")
    public List<Courier> getCouriers() {
    	List<Courier> result = new ArrayList<>();
    	for (Courier courier : couriers) {
			if (!courier.isDeleted) {
				result.add(courier);
			}
		}
    	return result;
    }
    
//    @javax.persistence.Transient
//    @JsonProperty("couriers")
//    public List<Courier> couriers;
//
//    public void setCouriers(){
//        couriers = Courier.find.where().eq("is_deleted", false).findList();
//    }

    @javax.persistence.Transient
    @JsonProperty("payment_method")
    public List<MapPaymentMethod> paymentMethods;

    public void setPaymentMethods(){
        paymentMethods = Arrays.asList(new MapPaymentMethod(1L, "Transfer Bank", Constant.getInstance().getImageUrl() + "pm-bank-transfer.png"));
    }

    @javax.persistence.Transient
    @JsonProperty("rating_stat")
    public MapProductRatting ratingStat;

    public void setRatingStat(){
        ratingStat = new MapProductRatting();
        ratingStat.setAverage(SellerReview.getAverage("merchant", id));
        ratingStat.setBintang1(SellerReview.getJumlah("merchant", id, 1));
        ratingStat.setBintang2(SellerReview.getJumlah("merchant", id, 2));
        ratingStat.setBintang3(SellerReview.getJumlah("merchant", id, 3));
        ratingStat.setBintang4(SellerReview.getJumlah("merchant", id, 4));
        ratingStat.setBintang5(SellerReview.getJumlah("merchant", id, 5));
        ratingStat.setCount(SellerReview.getJumlah("merchant", id));
    }

    public String getThumbnailImageLink(){
        return "";
    }

    public void updateStatus(String newStatus) {

        if(newStatus.equals("active"))
            isActive = Merchant.ACTIVE;
        else if(newStatus.equals("inactive"))
            isActive = Merchant.INACTIVE;

        super.update();

    }

	public static Finder<Long, Merchant> find = new Finder<Long, Merchant>(Long.class, Merchant.class);

	public static Page<Merchant> page(int page, int pageSize, String sortBy, String order, String filter) {
		return
				find.where()
						.ilike("name", "%" + filter + "%")
						.eq("is_deleted", false)
						.ne("id", -1L)
						.orderBy(sortBy + " " + order)
						.findPagingList(pageSize)
						.setFetchAhead(false)
						.getPage(page);
	}

	public static int findRowCount() {
		return
				find.where()
						.eq("is_deleted", false)
                        .ne("id", -1L)
						.findRowCount();
	}

	public static int findRowCountActive() {
		return
				find.where()
						.eq("is_deleted", false)
						.eq("is_active", true)
                        .ne("id", -1L)
						.findRowCount();
	}

    public Merchant(){

    }

    public Merchant(String domain, String name, String password, String fullName, String emailAddress, String phone, String gender,
                  String birthDate) throws ParseException {
        super();
        this.password = Encryption.EncryptAESCBCPCKS5Padding(password);
        this.fullName = fullName;
        this.email = emailAddress;
        if (!phone.isEmpty()){
            this.phone = phone;
        }
        this.gender = gender;
        if (!birthDate.isEmpty()){
            this.birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
        }
//            this.domain = domain;
        this.name = name;
        this.activationCode = Encryption.EncryptAESCBCPCKS5Padding(email);
        this.anchor = this.display = true;
        this.isActive = false;
        this.merchantCode = generateMerchantCode();
        rating = 0D;
        countRating = 0;
    }

    public Merchant(MapMerchantRegister map) throws ParseException {
        super();
        this.password = Encryption.EncryptAESCBCPCKS5Padding(map.getPassword());
        this.fullName = map.getName();
        this.email = map.getEmail();
        if (!map.getPhone().isEmpty()){
            this.phone = map.getPhone();
        }
        this.gender = map.getGender();
        if (!map.getBirthDate().isEmpty()){
            this.birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(map.getBirthDate());
        }
        
        this.accountNumber = map.getAccountNumber();
        this.accountAlias = map.getAccountAlias();
        this.name = map.getName();
        this.activationCode = Encryption.EncryptAESCBCPCKS5Padding(map.getEmail());
        this.anchor = this.display = true;
        this.isActive = false;
        this.merchantCode = generateMerchantCode();
        rating = 0D;
        countRating = 0;
        
        this.address = map.getAddress();
        this.postalCode = map.getPostalCode();
        this.url = map.getUrl();

        if (map.getDistrictId() != null){
            district = District.find.byId(map.getDistrictId());
        }
        if (map.getRegionId() != null){
            region = Region.find.byId(map.getRegionId());
        }
        if (map.getVillageId() != null){
            village = Village.find.byId(map.getVillageId());
        }
        if (map.getTownshipId() != null){
            township = Township.find.byId(map.getTownshipId());
        }
        if (map.getPickupPoint() != null){
            courierPointLocation = CourierPointLocation.find.byId(map.getPickupPoint());
        }

        couriers = null;
        if (map.getShippings() != null){
            List<Long> ids = new ArrayList<>();
            for (MapShippingId sid : map.getShippings()){
                ids.add(sid.getShippingId());
            }
            if (ids.size() > 0){
                couriers = Courier.find.where().eq("is_deleted", false).in("id", ids).findList();
            }
        }

        if (couriers == null){
            couriers = Courier.find.where().ne("name", "COD").eq("is_deleted", false).findList();
        }

        balance = unpaidCustomer = unpaidHokeba = paidHokeba = 0D;
    }

    public static Merchant fromMap(Merchant model, MapMerchantUpdateProfile map) {
        if (!map.getPassword().isEmpty()){
            model.password = Encryption.EncryptAESCBCPCKS5Padding(map.getPassword());
        }
        if (!map.getName().isEmpty()){
            model.name = map.getName();
            model.fullName = map.getName();
        }
        if (!map.getPhone().isEmpty()){
        	model.phone = map.getPhone();
        }
        if (!map.getPostalCode().isEmpty()){
        	model.postalCode = map.getPostalCode();
        }
        if (!map.getAddress().isEmpty()){
        	model.address = map.getAddress();
        }
        if (!map.getUrl().isEmpty()){
            model.url = map.getUrl();
        }
        if (!map.getGender().isEmpty()){
            model.gender = map.getGender();
        }
        if (!map.getBirthDate().isEmpty()){
            try {
                model.birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(map.getBirthDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!map.getAccountNumber().isEmpty()){
        	model.accountNumber = map.getAccountNumber();
        }
        if (!map.getAccountAlias().isEmpty()){
        	model.accountAlias = map.getAccountAlias();
        }
        if (map.getDistrictId() != null){
            model.district = District.find.byId(map.getDistrictId());
        }
        if (map.getRegionId() != null){
            model.region = Region.find.byId(map.getRegionId());
        }
        if (map.getVillageId() != null){
            model.village = Village.find.byId(map.getVillageId());
        }
        if (map.getTownshipId() != null){
            model.township = Township.find.byId(map.getTownshipId());
        }

        if (map.getShippings() != null){
            List<Long> ids = new ArrayList<>();
            for (MapShippingId sid : map.getShippings()){
                ids.add(sid.getShippingId());
            }
            if (ids.size() > 0){
                model.couriers.clear();
                model.couriers.addAll(Courier.find.where().eq("is_deleted", false).in("id", ids).findList());
            }
        }

        if (map.getPickupPoint() != null){
            model.courierPointLocation = CourierPointLocation.find.byId(map.getPickupPoint());
        }

        return model;
    }

    public static String validation(String email, String password, String confPassword) {
        if (email != null && !email.matches(CommonFunction.emailRegex)) {
            return "Email format not valid.";
        }
        String[] mails = email.split("@");
        Integer row = BlacklistEmail.find.where().eq("name", "@"+mails[1]).eq("is_deleted", false).findRowCount();
        if (row > 0){
            return "The email service provider that you are using can not be used in Whizliz Please use another email service provider.";
        }
        Merchant member = Merchant.find.where().eq("email", email).setMaxRows(1).findUnique();
        if (member != null) {
            return "The email is already registered.";
        }
//        Merchant memberD = Merchant.find.where().eq("domain", domain).setMaxRows(1).findUnique();
//        if (memberD != null) {
//            return "The domain is already registered.";
//        }
        /*if (!CommonFunction.passwordValidation(password)) {
            return "Password must be at least 8 character, has no whitespace, "
                    + "and have at least 3 variations from uppercase, lowercase, number, or symbol";
        }*/
        if (!CommonFunction.passwordValidation(password)) {
            return "Password must be at least 8 character";
        }
        if (!confPassword.equals(password)) {
            return "Password and confirm password did not match.";
        }
        return null;
    }

    public static String validation(String password, String confPassword) {
        if (!password.isEmpty()){
            if (!CommonFunction.passwordValidation(password)) {
                return "Password must be at least 8 character";
            }
            if (!confPassword.equals(password)) {
                return "Password and confirm password did not match.";
            }
        }

        return null;
    }

    public String getLogo(){
        return logo==null || logo.isEmpty() ? "http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/shop-icon.png" : Constant.getInstance().getImageUrl() + logo;
    }

    public Double getBalance(){
        return balance == null ? 0D : balance;
    }

    public Double getUnpaidHokeba(){
        return unpaidHokeba == null ? 0D : unpaidHokeba;
    }

    public Double getUnpaidCustomer(){
        return unpaidCustomer == null ? 0D : unpaidCustomer;
    }

    public Double getPaidHokeba(){
        return paidHokeba == null ? 0D : paidHokeba;
    }

    public boolean hasSetPassword() {
        return (this.password != null);
    }

    public String generateMerchantCode(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");

        Merchant merchant = Merchant.find.where().ilike("merchantCode", "ME"+simpleDateFormat.format(new Date())+"%")
                .order("created_at desc").setMaxRows(1).findUnique();
        String seqNum = "";
        if(merchant == null){
            seqNum = "0000001";
        }else{
            seqNum = merchant.merchantCode.substring(merchant.merchantCode.length() - 7);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "0000000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 7);
        }
        String code = "ME";
        code += simpleDateFormat.format(new Date()) + seqNum;
        return code;
    }

    public static Merchant login(String email, String password) {
        String encPassword = Encryption.EncryptAESCBCPCKS5Padding(password);
        Merchant member = Merchant.find.where().and(Expr.eq("email", email), Expr.eq("password", encPassword))
                .eq("ownMerchant", false).setMaxRows(1).findUnique();
        return member;
    }

    public static boolean isPasswordValid(String encPassword, String password){
        String encPassword2 = Encryption.EncryptAESCBCPCKS5Padding(password);
        return encPassword.equals(encPassword2);
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
                            Update<MerchantLog> upd = Ebean.createUpdate(MerchantLog.class,
                                    "UPDATE merchant_log SET is_active=:isActive WHERE is_active=true and merchant_id=:memberId");
                            upd.set("isActive", false);
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
                Merchant.removeAllToken(this.id);
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
        Update<MerchantLog> upd = Ebean.createUpdate(MerchantLog.class,
                "UPDATE merchant_log SET is_active=:isActive WHERE is_active=true and merchant_id=:memberId");
        upd.set("isActive", false);
        upd.set("memberId", id);
        upd.execute();
    }

    public Boolean isHokeba(){
        return id == -1L;
    }
    
    public static Merchant fetchOwnMerchant() {
    	return Merchant.find.where().eq("t0.own_merchant", true).orderBy("t0.id asc").setMaxRows(1).findUnique();
    }

    public List<FeatureAndPermissionSession> checkFeatureAndPermissions() {
        List<RoleFeature> myFeature = this.role.featureList;
        List<FeatureAndPermissionSession> featureAndPermissionSessionList = new ArrayList<>();
        for (RoleFeature feature : myFeature) {
            FeatureAndPermissionSession featureAndPermissionSession = new FeatureAndPermissionSession();
            featureAndPermissionSession.setFeatureName(feature.getFeature().name);
            featureAndPermissionSession.setIsView(feature.isView());
            featureAndPermissionSession.setIsAdd(feature.isAdd());
            featureAndPermissionSession.setIsEdit(feature.isEdit());
            featureAndPermissionSession.setIsDelete(feature.isDelete());
            featureAndPermissionSessionList.add(featureAndPermissionSession);
        }
        return featureAndPermissionSessionList;
    }

    public HashMap<String, Boolean> checkPrivilegeList() {
        LinkedHashMap<String, Boolean> result = new LinkedHashMap<String, Boolean>();
        List<Feature> allFeature = Feature.find.all();
        List<RoleFeature> myFeature = role.featureList;
        for (Feature targetFeature : allFeature) {
            String keyTarget = targetFeature.key;
            result.put(keyTarget, false);
        }
        for (RoleFeature feature : myFeature) {
            String keyTarget = feature.feature.key;
            result.put(keyTarget, true);
        }
        return result;
    }

    public static Merchant merchantGetId(Long merchantId) {
    	return Merchant.find.where().eq("t0.id", merchantId).orderBy("t0.id asc").setMaxRows(1).findUnique();
    }

    public static Merchant findByActivationCode(String activationCode) {
    	return Merchant.find.where().eq("t0.activation_code", activationCode).setMaxRows(1).findUnique();
    }

    public static Merchant findByEmail(String email, Boolean is_deleted) {
    	return Merchant.find.where().eq("t0.email", email).eq("t0.is_deleted", is_deleted).setMaxRows(1).findUnique();
    }

}
