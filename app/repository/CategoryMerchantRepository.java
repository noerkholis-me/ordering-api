package repository;

import models.Merchant;
import models.CategoryMerchant;
import play.db.ebean.Model;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import java.io.IOException;

import javax.persistence.PersistenceException;
import java.util.*;
import java.text.SimpleDateFormat;

public class CategoryMerchantRepository extends Model {

    public static Finder<Long, CategoryMerchant> find = new Finder<>(Long.class, CategoryMerchant.class);

    public static CategoryMerchant findByIdAndMerchantId(Long id, Merchant merchant) {
        try {
			return find.where()
				.eq("t0.id", id)
				.eq("merchant", merchant)
				.eq("t0.is_deleted", Boolean.FALSE)
				.findUnique();
		} catch (PersistenceException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public static CategoryMerchant findByNameAndMerchantId(String name, Merchant merchant) {
    	try {
			return find.where()
					.ieq("t0.category_name", name)
					.eq("merchant", merchant)
					.eq("t0.is_deleted", Boolean.FALSE)
					.findUnique();
		} catch (PersistenceException e) {
			e.printStackTrace();
			return null;
		}
    }

    public static List<CategoryMerchant> getDataCategory(Query<CategoryMerchant> reqQuery, String sort, String filter, int offset, int limit)
			throws IOException {
		Query<CategoryMerchant> query = reqQuery;

		if (!"".equals(sort)) {
            query = query.orderBy(sort);
		} else {
			query = query.orderBy("t0.updated_at desc");
		}

		ExpressionList<CategoryMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


		exp = exp.disjunction();
		exp = exp.ilike("t0.category_name", "%" + filter + "%");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<CategoryMerchant> resData = query.findPagingList(limit).getPage(offset).getList();

		return resData;
	}

	public static List<CategoryMerchant> getTotalData(Query<CategoryMerchant> reqQuery)
			throws IOException {
		Query<CategoryMerchant> query = reqQuery;

		query = query.orderBy("t0.created_at desc");

		ExpressionList<CategoryMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<CategoryMerchant> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}
}