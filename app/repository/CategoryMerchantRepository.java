package repository;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.BrandMerchant;
import models.Merchant;
import models.CategoryMerchant;
import models.QrGroupStore;
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

	public static List<CategoryMerchant> getDataCategory(Long merchantId, String sort, String filter, int offset, int limit) {
		String sorting;
		if (!"".equals(sort)) {
			sorting = sort;
		} else {
			sorting = "DESC";
		}

		String querySql = "SELECT cm.id FROM category_merchant cm "
				+ "WHERE cm.merchant_id = " + merchantId + " AND cm.is_deleted = false "
				+ "ORDER BY cm.updated_at " + sorting;

		RawSql rawSql = RawSqlBuilder.parse(querySql).create();
		Query<CategoryMerchant> query = Ebean.find(CategoryMerchant.class).setRawSql(rawSql);

		ExpressionList<CategoryMerchant> exp = query.where();
		exp = exp.disjunction();
		exp = exp.ilike("cm.category_name", "%" + filter + "%");
		exp = exp.endJunction();
		query = exp.query();

		return query.findPagingList(limit).getPage(offset).getList();
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

	public static List<CategoryMerchant> findAllByMerchant(Long merchantId, String sort, String filter, int offset, int limit) {
		String sorting;
		if (!"".equals(sort)) {
			sorting = sort;
		} else {
			sorting = "DESC";
		}

		String querySql = "SELECT cm.id FROM category_merchant cm "
			+ "JOIN merchant mc ON cm.merchant_id = mc.id "
			+ "JOIN product_merchant pm ON cm.id = pm.category_merchant_id "
			+ "WHERE cm.merchant_id = '" + merchantId + "' AND cm.is_deleted = false AND cm.is_active = true "
			+ "AND pm.is_deleted = false AND pm.is_active = true "
			+ "GROUP BY cm.id "
			+ "ORDER BY cm.id " + sorting;

		RawSql rawSql = RawSqlBuilder.parse(querySql).create();
		Query<CategoryMerchant> query = Ebean.find(CategoryMerchant.class).setRawSql(rawSql);

		ExpressionList<CategoryMerchant> exp = query.where();
		exp = exp.disjunction();
		exp = exp.ilike("cm.category_name", "%" + filter + "%");
		query = exp.query();

		return query.findPagingList(limit).getPage(offset).getList();
	}

	public static String queryTotalCategory(Long merchantId, String category) {
		return "SELECT pm.id FROM category_merchant cm "
			+ "JOIN merchant mc ON cm.merchant_id = mc.id "
			+ "JOIN product_merchant pm ON cm.id = pm.category_merchant_id "
			+ "JOIN product_merchant_detail pmd ON pm.id = pmd.product_merchant_id "
			+ "WHERE cm.merchant_id = '" + merchantId + "' AND cm.is_deleted = false AND cm.is_active = true "
			+ "AND pm.is_deleted = false AND pm.is_active = true AND " + category + " "
			+ "AND pmd.is_deleted = false AND pmd.product_type = 'MAIN' "
			+ "ORDER BY pm.id DESC";
	}

	public static Integer getTotalProductCategory(Long merchantId, Long categoryMerchantId) {
		String querySql = queryTotalCategory(merchantId, "pm.category_merchant_id = " + categoryMerchantId + "");

		RawSql rawSql = RawSqlBuilder.parse(querySql).create();
		Query<CategoryMerchant> query = Ebean.find(CategoryMerchant.class).setRawSql(rawSql);

		return query.findPagingList(0).getPage(0).getList().size();
	}

	public static Integer getTotalProductSubCategory(Long merchantId, Long subCategoryMerchantId) {
		String querySql = queryTotalCategory(merchantId, "pm.sub_category_merchant_id = " + subCategoryMerchantId + "");

		RawSql rawSql = RawSqlBuilder.parse(querySql).create();
		Query<CategoryMerchant> query = Ebean.find(CategoryMerchant.class).setRawSql(rawSql);

		return query.findPagingList(0).getPage(0).getList().size();
	}

	public static Integer getTotalProductSubsCategory(Long merchantId, Long subsCategoryMerchantId) {
		String querySql = queryTotalCategory(merchantId, "pm.subs_category_merchant_id = " + subsCategoryMerchantId + "");

		RawSql rawSql = RawSqlBuilder.parse(querySql).create();
		Query<CategoryMerchant> query = Ebean.find(CategoryMerchant.class).setRawSql(rawSql);

		return query.findPagingList(0).getPage(0).getList().size();
	}
}