package repository;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.Merchant;
import models.appsettings.*;
import play.db.ebean.Model;
import com.avaje.ebean.Expr;
import java.io.IOException;

import javax.persistence.PersistenceException;
import java.util.*;

public class AppSettingRepository extends Model {

    public static Finder<Long, AppSettings> find = new Finder<>(Long.class, AppSettings.class);

    public static AppSettings findByMerchantId (Long merchantId) {
			return find.where()
				.eq("merchant_id", merchantId)
				.eq("is_deleted", Boolean.FALSE)
				.findUnique();
    }

    public static AppSettings findByIdandMerchantId(Long id, Long merchantId) {
			return find.where()
				.eq("id", id)
				.eq("merchant_id", merchantId)
				.eq("is_deleted", Boolean.FALSE)
				.findUnique();
    }

}
