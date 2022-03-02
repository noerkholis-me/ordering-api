package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;
import com.hokeba.util.Encryption;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
@Table(name = "user_log")
public class UserCmsLog extends BaseModel {
    private static final long serialVersionUID = 1L;

    public static final String DEV_TYPE_WEB = "WEB";
    public static final String DEV_TYPE_IOS = "IOS";
    public static final String DEV_TYPE_ANDROID = "ANDROID";

    @JsonProperty("user_type")
    public String userType;
    public boolean isActive;

    // our
    public String token;
    @JsonProperty("expired_date")
    public Date expiredDate; // new DateTime(createdAt).plusDays(1);
    @JsonProperty("device_model")
    public String deviceModel; // device model number
    @JsonProperty("device_type")
    public String deviceType;
    @JsonProperty("api_key")
    public String apiKey;

    @ManyToOne
    @JsonBackReference
    public UserCms user;

    public static Finder<Long, UserCmsLog> find = new Finder<Long, UserCmsLog>(Long.class, UserCmsLog.class);

    private static String generateToken(String username, String password) throws NoSuchAlgorithmException {
        return Encryption.SHA1(new Date().toString() + "HOKEBATOKEN" + username + password);
    }

    public static UserCmsLog loginUser(String deviceModel, String deviceType, UserCms user) {
        try {
            UserCmsLog log = new UserCmsLog();
            if (deviceType.equalsIgnoreCase(DEV_TYPE_ANDROID) || deviceType.equalsIgnoreCase(DEV_TYPE_IOS)) {
                log.expiredDate = new DateTime(new Date()).plusDays(30).toDate();
            } else if (deviceType.equalsIgnoreCase(DEV_TYPE_WEB)) {
                log.expiredDate = new DateTime(new Date()).plusDays(1).toDate();
            } else {
                return null;
            }

            // log.token = generateToken(user.emailAddress, member.password);
            log.token = generateToken("", new Date().toString());
            log.deviceModel = deviceModel;
            log.deviceType = deviceType;

            log.isActive = true;
            log.userType = user.role.name;
            log.user = user;
            log.save();
            return log;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public static boolean logoutUser(String token) {
        UserCmsLog log = UserCmsLog.find.where().eq("token", token).eq("is_active", true).setMaxRows(1).findUnique();
        if (log != null) {
            log.delete();
            return true;
        }
        return false;
    }

    public static UserCmsLog getByToken(String token) {
        return UserCmsLog.find.where().eq("token", token).eq("is_active", true).setMaxRows(1).findUnique();
    }

    public static UserCmsLog isUserAuthorized(String token, String apiKey) {
        UserCmsLog log = UserCmsLog.find.where().eq("token", token).eq("is_active", true).setMaxRows(1).findUnique();
        Date today = new Date();
        if (log != null && apiKey.equalsIgnoreCase(Constant.getInstance().getApiKeyIOS())) {
            if (today.before(log.expiredDate)) {
                //disable
                return log;
            } else {
                log.isActive = false;
                log.save();
            }
        }
        return null;
    }

}
