package models;

import com.avaje.ebean.Query;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;
import com.hokeba.util.Encryption;
import org.joda.time.DateTime;
import repository.RoleMerchantRepository;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
@Table(name = "merchant_log")
public class MerchantLog extends BaseModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final String DEV_TYPE_WEB = "WEB";
    public static final String DEV_TYPE_IOS = "IOS";
    public static final String DEV_TYPE_ANDROID = "ANDROID";
    public static final String DEV_TYPE_MINI_POS = "MINIPOS";
    public static final String DEV_TYPE_KITCHEN = "KITCHEN";

    @JsonProperty("member_type")
    public String memberType;
    public boolean isActive;

    // our
    public String token;
    @JsonProperty("expired_date")
    public Date expiredDate; // new DateTime(createdAt).plusDays(1);
    @JsonProperty("device_model")
    public String deviceModel; // device model number
    @JsonProperty("device_type")
    public String deviceType;
    @JsonProperty("device_id")
    public String deviceId;
    @JsonProperty("api_key")
    public String apiKey;

    @ManyToOne
    @JsonBackReference
    public Merchant merchant;

    @ManyToOne
    @JsonBackReference
    public UserMerchant userMerchant;

    @Transient
    public Long getMerchantId() {
        if (merchant != null)
            return merchant.id;
        return new Long(0);
    }

    public static Finder<Long, MerchantLog> find = new Finder<Long, MerchantLog>(Long.class, MerchantLog.class);

    private static String generateToken(String username, String password) throws NoSuchAlgorithmException {
        return Encryption.SHA1(new Date().toString() + "MERCHANTHOKEBATOKEN" + username + password);
    }

    public static MerchantLog loginMerchant(String deviceModel, String deviceType, String deviceId, Merchant member, UserMerchant userMerchant, Boolean userType) {
        MerchantLog log = new MerchantLog();
        try {
            if (deviceType.equalsIgnoreCase(DEV_TYPE_ANDROID) || deviceType.equalsIgnoreCase(DEV_TYPE_IOS)) {
                log.expiredDate = new DateTime(new Date()).plusDays(1).toDate();
            } else if (deviceType.equalsIgnoreCase(DEV_TYPE_WEB)) {
                log.expiredDate = new DateTime(new Date()).plusDays(1).toDate();
            } else if (deviceType.equalsIgnoreCase(DEV_TYPE_MINI_POS)) {
                if(userType){
                    List<RoleMerchant> roleMerchant = RoleMerchantRepository.findByMerchantId(member);
                    if(!roleMerchant.isEmpty() && roleMerchant.stream().findFirst().get().isCashier()) {
                        log.expiredDate = new DateTime(new Date()).plusDays(1).toDate();
                    } else {
                        return null;
                    }
                } else {
                    RoleMerchant roleMerchant = RoleMerchantRepository.find.where().eq("id", userMerchant.getRole().id).findUnique();
                    if(roleMerchant != null && roleMerchant.isCashier()) {
                        log.expiredDate = new DateTime(new Date()).plusDays(1).toDate();
                    } else {
                        return null;
                    }
                }
            } else if (deviceType.equalsIgnoreCase(DEV_TYPE_KITCHEN)) {
                if (userType) {
                    List<RoleMerchant> roleMerchant = RoleMerchantRepository.findByMerchantId(member);
                    if (!roleMerchant.isEmpty() && roleMerchant.stream().findFirst().get().isKitchen()) {
                        log.expiredDate = new DateTime(new Date()).plusDays(1).toDate();
                    } else {
                        return null;
                    }
                } else {
                    RoleMerchant roleMerchant = RoleMerchantRepository.find.where().eq("id", userMerchant.getRole().id).findUnique();
                    if (roleMerchant != null && roleMerchant.isKitchen()) {
                        log.expiredDate = new DateTime(new Date()).plusDays(1).toDate();
                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
            String userCode = "";
            String passCode = "";
            if (userType == Boolean.TRUE) {
                if(member.email!=null){
                    userCode = member.email;
                    passCode = member.password;
                }
                log.memberType = "merchant";
                log.token = generateToken(userCode, passCode);
            } else {
                if(userMerchant.email != null){
                    userCode = userMerchant.email;
                    passCode = userMerchant.password;
                }
                log.memberType = "user_merchant";
                log.token = generateToken(userCode, passCode);
            }

            log.deviceModel = deviceModel;
            log.deviceType = deviceType;
            log.deviceId = deviceId;

            log.isActive = true;
            log.merchant = member;
            log.userMerchant = userMerchant;
            log.save();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
        return log;
    }

    public static boolean logoutMerchant(String token) {
        MerchantLog log = MerchantLog.find.where().eq("token", token).eq("is_active", true)
                .setMaxRows(1).findUnique();
        if (log != null) {
            log.isActive = false;
            log.update();
            return true;
        }
        return false;
    }

    public static MerchantLog isMerchantAuthorized(String token, String apiKey) {
        // validate token
        MerchantLog log = MerchantLog.find.where().eq("token", token)
                .eq("is_active", true)
                .setMaxRows(1).findUnique();

        // validate api key
        String keyWeb = Constant.getInstance().getApiKeyWeb();
        String keyIos = Constant.getInstance().getApiKeyIOS();
        String keyAndroid = Constant.getInstance().getApiKeyAndroid();
        String keyMiniPos = Constant.getInstance().getApiKeyMiniPos();

        Date today = new Date();
        if (log != null && (
            (log.deviceType.equalsIgnoreCase(MerchantLog.DEV_TYPE_WEB) && apiKey.equalsIgnoreCase(keyWeb))
                || (log.deviceType.equalsIgnoreCase(MerchantLog.DEV_TYPE_IOS) && apiKey.equalsIgnoreCase(keyIos))
                || (log.deviceType.equalsIgnoreCase(MerchantLog.DEV_TYPE_ANDROID) && apiKey.equalsIgnoreCase(keyAndroid))
                || (log.deviceType.equalsIgnoreCase(MerchantLog.DEV_TYPE_MINI_POS) && (apiKey.equalsIgnoreCase(keyMiniPos) 
                || apiKey.equalsIgnoreCase(keyWeb)))
                || (log.deviceType.equalsIgnoreCase(MerchantLog.DEV_TYPE_KITCHEN) && apiKey.equalsIgnoreCase(keyMiniPos))
            )) {
            if (today.before(log.expiredDate)) {
                return log;
            } else {
                log.isActive = false;
                log.save();
            }
        }
        return null;
    }

    public static MerchantLog getByToken(String token) {
        return MerchantLog.find.where().eq("token", token).eq("is_active", true).setMaxRows(1).findUnique();
    }

    public static List<MerchantLog> getListMerchantLog(Merchant merchant) {
        return MerchantLog.find.where()
                .eq("is_active", true)
                .eq("merchant", merchant)
                .in("device_type", Arrays.asList("IOS", "ANDROID"))
                .findList();
    }

}