package repository;

import models.UserMerchant;
import play.db.ebean.Model;
import dtos.merchant.UserMerchantRequest;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import java.io.IOException;
import com.hokeba.api.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import java.util.*;
import java.text.SimpleDateFormat;

public class UserMerchantRepository extends Model {

    public static Finder<Long, UserMerchant> find = new Finder<>(Long.class, UserMerchant.class);

    public static UserMerchant findByEmailAndMerchantId(String email, Long merchantId) {
        return find.where()
            .eq("email", email)
            .eq("merchantId", merchantId)
            .eq("isActive", Boolean.TRUE)
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


        exp = exp.conjunction();
		exp = exp.or(Expr.ilike("t0.full_name", filter + "%"),Expr.ilike("t0.first_name", filter + "%"));
		// exp = exp.or(Expr.ilike("t0.last_name", filter + "%"),Expr.ilike("t0.email", filter + "%"));
        exp = exp.endJunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<UserMerchant> resData = query.findPagingList(limit).getPage(offset).getList();

		System.out.println(query.getGeneratedSql());

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

}
