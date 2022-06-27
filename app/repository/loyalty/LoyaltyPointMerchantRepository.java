package repository.loyalty;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.loyalty.*;
import play.db.ebean.Model;

import java.io.IOException;

import java.util.*;

public class LoyaltyPointMerchantRepository extends Model {

    public static Finder<Long, LoyaltyPointMerchant> find = new Finder<>(Long.class, LoyaltyPointMerchant.class);

	public static LoyaltyPointMerchant findById (Long id) {
		return find.where()
				.eq("id", id)
				.eq("is_deleted", Boolean.FALSE)
				.findUnique();
	}

	public static LoyaltyPointMerchant findByIdandSubsCategoryId(Long id, Long subsCategoryId, Long merchantId) {
		return find.where()
			.eq("id", id)
            .eq("subs_category_id", subsCategoryId)
			.eq("merchant_id", merchantId)
			.eq("is_deleted", Boolean.FALSE)
			.findUnique();
	}

	public static LoyaltyPointMerchant findBySubsCategoryId(Long subsCategoryId, Long merchantId) {
		return find.where()
            .eq("subs_category_id", subsCategoryId)
			.eq("merchant_id", merchantId)
			.eq("is_deleted", Boolean.FALSE)
			.findUnique();
	}
	
	public static List<LoyaltyPointMerchant> getListLoyaltyPoint(Query<LoyaltyPointMerchant> reqQuery, int offset, int limit)
			throws IOException {
		Query<LoyaltyPointMerchant> query = reqQuery;

		
		query = query.orderBy("t0.updated_at desc");

		ExpressionList<LoyaltyPointMerchant> exp = query.where();


		// exp = exp.conjunction();
		// exp = exp.ilike("t0.pupoint_name", "%" + filter + "%");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<LoyaltyPointMerchant> resData = query.findPagingList(limit).getPage(offset).getList();

		return resData;
	}

	public static List<LoyaltyPointMerchant> getTotalData(Query<LoyaltyPointMerchant> reqQuery)
			throws IOException {
		Query<LoyaltyPointMerchant> query = reqQuery;

		ExpressionList<LoyaltyPointMerchant> exp = query.where();

		query = exp.query();

		int total = query.findList().size();

		List<LoyaltyPointMerchant> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}

}
