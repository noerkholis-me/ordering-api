package repository.loyalty;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.loyalty.*;
import models.*;
import play.db.ebean.Model;

import java.io.IOException;

import java.util.*;

public class LoyaltyPointHistoryRepository extends Model {

    public static Finder<Long, LoyaltyPointHistory> find = new Finder<>(Long.class, LoyaltyPointHistory.class);

	public static LoyaltyPointHistory findByMember (Member member) {
		return find.where()
				.eq("member", member)
				.eq("t0.is_deleted", Boolean.FALSE).order("t0.created_at desc")
				.setMaxRows(1).findUnique();
	}
	
	public static List<LoyaltyPointHistory> getListLoyaltyPointHistory(Query<LoyaltyPointHistory> reqQuery, int offset, int limit)
			throws IOException {
		Query<LoyaltyPointHistory> query = reqQuery;

		
		query = query.orderBy("t0.updated_at desc");

		ExpressionList<LoyaltyPointHistory> exp = query.where();


		// exp = exp.conjunction();
		// exp = exp.ilike("t0.pupoint_name", "%" + filter + "%");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<LoyaltyPointHistory> resData = query.findPagingList(limit).getPage(offset).getList();

		return resData;
	}

	public static List<LoyaltyPointHistory> getTotalData(Query<LoyaltyPointHistory> reqQuery)
			throws IOException {
		Query<LoyaltyPointHistory> query = reqQuery;

		ExpressionList<LoyaltyPointHistory> exp = query.where();

		query = exp.query();

		int total = query.findList().size();

		List<LoyaltyPointHistory> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}

}
