package repository;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
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
		return find.where().eq("id", id).eq("is_active", true).eq("is_deleted", false).findUnique();
	}

    public static SubsCategoryMerchant findByIdAndMerchantId(Long id, Merchant merchant) {
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
    
    public static SubsCategoryMerchant findByNameAndMerchantId(String name, Merchant merchant) {
    	try {
			return find.where()
					.ieq("t0.subscategory_name", name)
					.eq("merchant", merchant)
					.eq("t0.is_deleted", Boolean.FALSE)
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
		exp = exp.ilike("t0.subscategory_name", "%" + filter + "%");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}
        if (filter != "" && filter != null) {
            offset = 0;
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
    
    public static List<SubsCategoryMerchant> getForDetailKiosk(Query<SubsCategoryMerchant> reqQuery)
			throws IOException {
		Query<SubsCategoryMerchant> query = reqQuery;

		query = query.orderBy("t0.sequence asc");

		ExpressionList<SubsCategoryMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<SubsCategoryMerchant> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}

	public static List<SubsCategoryMerchant> getListSequence(Merchant merchant) {
		List<SubsCategoryMerchant> lists = new ArrayList<>();
        lists = find.where()
                .eq("merchant", merchant)
				.eq("t0.is_deleted", Boolean.FALSE)
				.eq("t0.is_active", Boolean.TRUE)
				.orderBy("t0.sequence ASC").findList();
				
				return lists;
	}

	public static SubsCategoryMerchant getLatestSequence(Merchant merchant) {
		try {
			return find.where()
				.eq("merchant", merchant)
				.eq("t0.is_deleted", Boolean.FALSE)
				.orderBy("t0.sequence DESC")
				.setMaxRows(1).findUnique();
		} catch (PersistenceException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<SubsCategoryMerchant> findListCategoryMiniPos(Long merchantId) {
		String querySql = "SELECT scm.id FROM subs_category_merchant scm "
			+ "JOIN product_merchant pm ON  scm.id = pm.subs_category_merchant_id "
			+ "JOIN product_merchant_detail pmd ON pm.id = pmd.product_merchant_id "
			+ "WHERE pm.is_deleted = false AND pm.is_active = true "
			+ "AND pmd.is_deleted = false AND pmd.product_type = 'MAIN' "
			+ "AND scm.merchant_id = " + merchantId + " AND scm.is_deleted = false AND scm.is_active = true "
			+ "GROUP BY scm.id "
			+ "ORDER BY scm.id ASC";

		RawSql rawSql = RawSqlBuilder.parse(querySql).create();
		Query<SubsCategoryMerchant> query = Ebean.find(SubsCategoryMerchant.class).setRawSql(rawSql);

		return query.findPagingList(0).getPage(0).getList();
	}

	public static List<SubsCategoryMerchant> findAllByMerchantAndSubCategory(Long merchantId, Long subCategoryId) {
		String querySql = "SELECT scm.id FROM subs_category_merchant scm "
				+ "WHERE scm.merchant_id = " + merchantId + " AND scm.subcategory_id = " + subCategoryId + " AND scm.is_deleted = false "
				+ "ORDER BY scm.id DESC";

		RawSql rawSql = RawSqlBuilder.parse(querySql).create();
		Query<SubsCategoryMerchant> query = Ebean.find(SubsCategoryMerchant.class).setRawSql(rawSql);

		return query.findPagingList(0).getPage(0).getList();
	}
}