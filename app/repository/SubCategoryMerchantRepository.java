package repository;

import models.CategoryMerchant;
import models.Merchant;
import models.SubCategoryMerchant;
import play.db.ebean.Model;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import java.io.IOException;

import javax.persistence.PersistenceException;
import java.util.*;
import java.text.SimpleDateFormat;

public class SubCategoryMerchantRepository extends Model {

    public static Finder<Long, SubCategoryMerchant> find = new Finder<>(Long.class, SubCategoryMerchant.class);


	public static SubCategoryMerchant findById(Long id) {
		return find.where().eq("id", id).findUnique();
	}

    public static SubCategoryMerchant findByIdAndMerchantId(Long id, Merchant merchant) {
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
    
    public static SubCategoryMerchant findByNameAndMerchantId(String name, Merchant merchant) {
    	try {
    		return find.where()
    				.eq("t0.subcategory_name".toLowerCase(), name.toLowerCase())
    				.eq("merchant", merchant)
					.eq("t0.is_deleted", Boolean.FALSE)
    				.findUnique();
    	} catch (PersistenceException e) {
    		e.printStackTrace();
    		return null;
    	}
    }

    public static List<SubCategoryMerchant> getDataSubCategory(Query<SubCategoryMerchant> reqQuery, String sort, String filter, int offset, int limit)
			throws IOException {
		Query<SubCategoryMerchant> query = reqQuery;

		if (!"".equals(sort)) {
            query = query.orderBy(sort);
		} else {
			query = query.orderBy("t0.updated_at desc");
		}

		ExpressionList<SubCategoryMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


		exp = exp.disjunction();
		exp = exp.ilike("t0.subcategory_name", "%" + filter + "%");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<SubCategoryMerchant> resData = query.findPagingList(limit).getPage(offset).getList();

		return resData;
	}

	public static List<SubCategoryMerchant> getTotalData(Query<SubCategoryMerchant> reqQuery)
			throws IOException {
		Query<SubCategoryMerchant> query = reqQuery;

		query = query.orderBy("t0.created_at desc");

		ExpressionList<SubCategoryMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<SubCategoryMerchant> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
    }
    
    public static List<SubCategoryMerchant> getTotalSubCategory(Query<SubCategoryMerchant> reqQuery)
			throws IOException {
		Query<SubCategoryMerchant> query = reqQuery;

		query = query.orderBy("t0.created_at desc");

		ExpressionList<SubCategoryMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<SubCategoryMerchant> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
    }
    
    public static List<SubCategoryMerchant> getDataForCategory(Query<SubCategoryMerchant> reqQuery)
			throws IOException {
		Query<SubCategoryMerchant> query = reqQuery;

		query = query.orderBy("t0.created_at desc");

		ExpressionList<SubCategoryMerchant> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<SubCategoryMerchant> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}

	public static List<SubCategoryMerchant> getListSequence(Merchant merchant) {
		List<SubCategoryMerchant> lists = new ArrayList<>();
        lists = find.where()
                .eq("merchant", merchant)
				.eq("t0.is_deleted", Boolean.FALSE)
				.eq("t0.is_active", Boolean.TRUE)
				.orderBy("t0.sequence ASC").findList();
				
				return lists;
	}

	public static SubCategoryMerchant getLatestSequence(Merchant merchant) {
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
}