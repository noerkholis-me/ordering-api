package repository;

import models.Merchant;
import models.BrandMerchant;
import play.db.ebean.Model;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import java.io.IOException;

import javax.persistence.PersistenceException;
import java.util.*;
import java.text.SimpleDateFormat;

public class BrandMerchantRepository extends Model {

    public static Finder<Long, BrandMerchant> find = new Finder<>(Long.class, BrandMerchant.class);

    public static BrandMerchant findByIdAndMerchantId(Long id, Long merchantId) {
        try {
			return find.where()
				.eq("id", id)
				.eq("merchant_id", merchantId)
				.eq("is_deleted", Boolean.FALSE)
				.findUnique();
		} catch (PersistenceException e) {
			e.printStackTrace();
			return null;
		}
    }

    public static List<BrandMerchant> getDataBrand(Query<BrandMerchant> reqQuery, String sort, String filter, int offset, int limit)
			throws IOException {
		Query<BrandMerchant> query = reqQuery;

		if (!"".equals(sort)) {
            query = query.orderBy(sort);
		} else {
			query = query.orderBy("t0.updated_at desc");
		}

		ExpressionList<BrandMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


		exp = exp.disjunction();
		exp = exp.ilike("t0.brand_name", "%" + filter + "%");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<BrandMerchant> resData = query.findPagingList(limit).getPage(offset).getList();

		return resData;
	}

	public static List<BrandMerchant> getTotalData(Query<BrandMerchant> reqQuery)
			throws IOException {
		Query<BrandMerchant> query = reqQuery;

		query = query.orderBy("t0.created_at desc");

		ExpressionList<BrandMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<BrandMerchant> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}

	public static List<BrandMerchant> getDataBrandHomepage(Query<BrandMerchant> reqQuery, int offset)
			throws IOException {
		Query<BrandMerchant> query = reqQuery;
		query = query.orderBy("t0.brand_name asc");

		ExpressionList<BrandMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		List<BrandMerchant> resData = query.findPagingList(0).getPage(offset).getList();

		return resData;
	}
}