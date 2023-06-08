package repository;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
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

    public static BrandMerchant findByIdAndMerchantId(Long id, Merchant merchant) {
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
    
    public static BrandMerchant findByNameAndMerchantId(String name, Merchant merchant) {
    	try {
			return find.where()
					.ieq("t0.brand_name", name)
					.eq("merchant", merchant)
					.eq("t0.is_deleted", Boolean.FALSE)
					.findUnique();
		} catch (PersistenceException e) {
			e.printStackTrace();
			return null;
		}
    }

	public static List<BrandMerchant> getDataBrand(Long merchantId, String isActive, String sort, String filter, int offset, int limit) {
		String sorting;
		if (!"".equals(sort)) {
			sorting = sort;
		} else {
			sorting = "DESC";
		}

		String querySql;
		String _isActive = isActive.equals("true") ? "true" : "false";
		if (isActive.isEmpty()) {
			querySql = "SELECT bm.id FROM brand_merchant bm "
				+ "WHERE bm.merchant_id = " + merchantId + " AND bm.is_deleted = false "
				+ "ORDER BY bm.updated_at " + sorting;
		} else {
			querySql = "SELECT bm.id FROM brand_merchant bm "
				+ "WHERE bm.merchant_id = " + merchantId + " AND bm.is_deleted = false "
				+ "AND bm.is_active = " + _isActive + " "
				+ "ORDER BY bm.updated_at " + sorting;
		}

		RawSql rawSql = RawSqlBuilder.parse(querySql).create();
		Query<BrandMerchant> query = Ebean.find(BrandMerchant.class).setRawSql(rawSql);

		ExpressionList<BrandMerchant> exp = query.where();
		exp = exp.disjunction();
		exp = exp.ilike("bm.brand_name", "%" + filter + "%");
		exp = exp.endJunction();
		query = exp.query();

		return query.findPagingList(limit).getPage(offset).getList();
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

	public static List<BrandMerchant> findListBrand(Long merchantId, int offset, int limit) {
		String querySql = "SELECT bm.id FROM brand_merchant bm "
			+ "WHERE bm.merchant_id = '" + merchantId + "' AND bm.is_deleted = false AND bm.is_active = true "
			+ "ORDER BY bm.brand_name ASC";

		RawSql rawSql = RawSqlBuilder.parse(querySql).create();
		Query<BrandMerchant> query = Ebean.find(BrandMerchant.class).setRawSql(rawSql);

		return query.findPagingList(limit).getPage(offset).getList();
	}

	public static Integer getTotalProductBrand(Long merchantId, Long brandId) {
		String querySql =  "SELECT pmd.id FROM product_merchant_detail pmd "
			+ "JOIN product_merchant pm ON pmd.product_merchant_id = pm.id "
			+ "JOIN brand_merchant bm ON pm.brand_merchant_id = bm.id "
			+ "WHERE pmd.product_type = 'MAIN' AND pmd.is_deleted = false "
			+ "AND pm.merchant_id = " + merchantId + " AND pm.brand_merchant_id = " + brandId + " AND pm.is_active = true AND pm.is_deleted = false "
			+ "AND bm.is_active = true "
			+ "ORDER BY pm.id DESC";

		RawSql rawSql = RawSqlBuilder.parse(querySql).create();
		Query<BrandMerchant> query = Ebean.find(BrandMerchant.class).setRawSql(rawSql);

		return query.findPagingList(0).getPage(0).getList().size();
	}
}