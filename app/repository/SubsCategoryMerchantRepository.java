package repository;

import models.CategoryMerchant;
import models.Merchant;
import models.SubCategoryMerchant;
import models.SubsCategoryMerchant;
import play.db.ebean.Model;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import java.io.IOException;

import javax.persistence.PersistenceException;
import java.util.*;
import java.text.SimpleDateFormat;

public class SubsCategoryMerchantRepository extends Model {

    public static Finder<Long, SubsCategoryMerchant> find = new Finder<>(Long.class, SubsCategoryMerchant.class);


	public static SubsCategoryMerchant findById(Long id) {
		return find.where().eq("id", id).findUnique();
	}

    public static SubsCategoryMerchant findByIdAndMerchantId(Long id, Long merchantId) {
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

    public static List<SubsCategoryMerchant> getDataSubsCategory(Query<SubsCategoryMerchant> reqQuery, String sort, String filter, int offset, int limit)
			throws IOException {
		Query<SubsCategoryMerchant> query = reqQuery;

		if (!"".equals(sort)) {
            query = query.orderBy(sort);
		} else {
			query = query.orderBy("t0.updated_at desc");
		}

		ExpressionList<SubsCategoryMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


		exp = exp.disjunction();
		exp = exp.ilike("t0.subcategory_name", "%" + filter + "%");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<SubsCategoryMerchant> resData = query.findPagingList(limit).getPage(offset).getList();

		return resData;
	}

	public static List<SubsCategoryMerchant> getTotalData(Query<SubsCategoryMerchant> reqQuery)
			throws IOException {
		Query<SubsCategoryMerchant> query = reqQuery;

		query = query.orderBy("t0.created_at desc");

		ExpressionList<SubsCategoryMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<SubsCategoryMerchant> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
    }
    
    public static List<SubsCategoryMerchant> getTotalSubsCategory(Query<SubsCategoryMerchant> reqQuery)
			throws IOException {
		Query<SubsCategoryMerchant> query = reqQuery;

		query = query.orderBy("t0.created_at desc");

		ExpressionList<SubsCategoryMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<SubsCategoryMerchant> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
    }
    
    public static List<SubsCategoryMerchant> getDataForCategory(Query<SubsCategoryMerchant> reqQuery)
			throws IOException {
		Query<SubsCategoryMerchant> query = reqQuery;

		query = query.orderBy("t0.created_at desc");

		ExpressionList<SubsCategoryMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<SubsCategoryMerchant> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}

	public static List<SubsCategoryMerchant> getListSequence(Long merchantId) {
		List<SubsCategoryMerchant> lists = new ArrayList<>();
        lists = find.where()
                .eq("merchant_id", merchantId)
				.eq("is_deleted", Boolean.FALSE)
				.eq("is_active", Boolean.TRUE)
				.orderBy("sequence ASC").findList();
				
				return lists;
	}

	public static SubsCategoryMerchant getLatestSequence(Long merchantId) {
		try {
			return find.where()
				.eq("merchant_id", merchantId)
				.eq("is_deleted", Boolean.FALSE)
				.orderBy("sequence DESC")
				.setMaxRows(1).findUnique();
		} catch (PersistenceException e) {
			e.printStackTrace();
			return null;
		}
	}
}