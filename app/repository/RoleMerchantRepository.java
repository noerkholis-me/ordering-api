package repository;

import com.avaje.ebean.*;
import models.*;
import play.db.ebean.Model;

import java.io.IOException;
import java.util.*;

public class RoleMerchantRepository extends Model {
	public static Finder<Long, RoleMerchant> find = new Finder<>(Long.class, RoleMerchant.class);
	
	public static RoleMerchant findByIdAndMerchantId(Long id, Merchant merchant) {
        return find.where()
            .eq("id", id)
            .eq("merchant", merchant)
			.eq("isActive", Boolean.TRUE)
            .eq("isDeleted", Boolean.FALSE)
            .findUnique();
    }

    public static List<RoleMerchant> getDataRole(Query<RoleMerchant> reqQuery, String sort, String filter, int offset, int limit)
			throws IOException {
		Query<RoleMerchant> query = reqQuery;

		if (!"".equals(sort)) {
            query = query.orderBy(sort);
		} else {
			query = query.orderBy("t0.created_at desc");
		}

		ExpressionList<RoleMerchant> exp = query.where();


		exp = exp.disjunction();
		exp = exp.or(Expr.ilike("t0.name", "%" + filter + "%"),Expr.ilike("t0.description", "%" + filter + "%"));
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<RoleMerchant> resData = query.findPagingList(limit).getPage(offset).getList();

		return resData;

		
	}

	public static List<RoleMerchant> getTotalData(Query<RoleMerchant> reqQuery)
			throws IOException {
		Query<RoleMerchant> query = reqQuery;

		ExpressionList<RoleMerchant> exp = query.where();

		query = exp.query();

		int total = query.findList().size();

		List<RoleMerchant> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}



}