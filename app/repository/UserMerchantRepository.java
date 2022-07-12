package repository;

import com.hokeba.util.Encryption;
import models.Merchant;
import models.UserMerchant;
import play.db.ebean.Model;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import java.io.IOException;

import javax.persistence.PersistenceException;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * @author kudo
 * @version 1.0
 * this is just for sample repository, please refactor to generic class. It can be resuable for other models.
 */
public class UserMerchantRepository extends Model {

    public static Finder<Long, UserMerchant> find = new Finder<>(Long.class, UserMerchant.class);

    public static UserMerchant findByEmailAndRole_MerchantId(String email, Merchant merchant) {
		try {
			return find.where()
				.eq("email", email)
				.eq("role.merchant", merchant)
				.eq("isActive", Boolean.TRUE)
				.findUnique();
		} catch (PersistenceException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static UserMerchant findByEmail(String email) {
		return find.where()
				.eq("email", email)
				.eq("isActive", Boolean.TRUE)
				.findUnique();
	}

	public static UserMerchant forResendEmail(String email, Merchant merchant) {
		return find.where()
				.eq("t0.email", email)
				.eq("role.merchant", merchant)
				.eq("t0.is_deleted", Boolean.FALSE)
				.setMaxRows(1)
				.findUnique();
	}

    public static UserMerchant findById(Long id, Merchant merchant) {
        return find.where()
            .eq("t0.id", id)
            .eq("role.merchant", merchant)
            .eq("t0.is_deleted", Boolean.FALSE)
            .findUnique();
	}
	
    public static UserMerchant findAccountById(Long id) {
        return find.where()
            .eq("id", id)
            .eq("isActive", Boolean.TRUE)
            .findUnique();
    }
	
    public static UserMerchant findByActivationCode(String activationCode) {
        return find.where()
            .eq("activation_code", activationCode)
            .findUnique();
    }

    public static List<UserMerchant> getDataUser(Query<UserMerchant> reqQuery, String sort, String filter, int offset, int limit)
			throws IOException {
		Query<UserMerchant> query = reqQuery;

		if (!"".equals(sort)) {
            query = query.orderBy(sort);
		} else {
			query = query.orderBy("t0.updated_at desc");
		}

		ExpressionList<UserMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


		exp = exp.disjunction();
		exp = exp.or(Expr.ilike("t0.full_name", "%" + filter + "%"),Expr.ilike("t0.first_name", "%" + filter + "%"));
		exp = exp.or(Expr.ilike("t0.email", "%"+filter+"%"),Expr.ilike("t0.last_name", "%" + filter + "%"));
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<UserMerchant> resData = query.findPagingList(limit).getPage(offset).getList();

		return resData;
	}

	public static List<UserMerchant> getTotalData(Query<UserMerchant> reqQuery)
			throws IOException {
		Query<UserMerchant> query = reqQuery;

		query = query.orderBy("t0.created_at desc");

		ExpressionList<UserMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<UserMerchant> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}

	public static UserMerchant findDataActivationCode(String activationCode) {
        UserMerchant data = find.where().eq("t0.activation_code", activationCode).eq("t0.is_deleted", false).findUnique();
        return data;
    }

	public static UserMerchant login(String email, String password) {
		String encPassword = Encryption.EncryptAESCBCPCKS5Padding(password);
		UserMerchant member = find.where().and(Expr.eq("email", email), Expr.eq("password", encPassword))
				.eq("isDeleted", false).setMaxRows(1).findUnique();
		return member;
	}

}
