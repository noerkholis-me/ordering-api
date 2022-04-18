package repository;

import models.*;
import play.db.ebean.Model;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import java.io.IOException;

import javax.persistence.PersistenceException;
import java.util.*;
import java.text.SimpleDateFormat;

public class ProductStoreRepository extends Model {

    public static Finder<Long, ProductStore> find = new Finder<>(Long.class, ProductStore.class);

    public static ProductStore findByIdAndMerchantId(Long id, Long merchantId) {
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

    public static ProductStore findForCust(Long productId, Long storeId, Long merchantId) {
        try {
			return find.where()
				.eq("product_id", productId)
				.eq("store_id", storeId)
				.eq("merchant_id", merchantId)
				.eq("is_deleted", Boolean.FALSE)
				.findUnique();
		} catch (PersistenceException e) {
			e.printStackTrace();
			return null;
		}
    }

    public static List<ProductStore> getDataProductStores(Query<ProductStore> reqQuery, String sort, String filter, int offset, int limit)
			throws IOException {
		Query<ProductStore> query = reqQuery;

		if (!"".equals(sort)) {
            query = query.orderBy(sort);
		} else {
			query = query.orderBy("t0.updated_at desc");
		}

		ExpressionList<ProductStore> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


		exp = exp.disjunction();
		exp = exp.ilike("t0.category_name", "%" + filter + "%");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<ProductStore> resData = query.findPagingList(limit).getPage(offset).getList();

		return resData;
	}

    public static List<ProductStore> getDataProductStore(Query<ProductStore> reqQuery)
			throws IOException {
        Query<ProductStore> query = reqQuery;
		query = query.orderBy("t0.updated_at desc");

		ExpressionList<ProductStore> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<ProductStore> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}

	public static List<ProductStore> getTotalData(Query<ProductStore> reqQuery)
			throws IOException {
		Query<ProductStore> query = reqQuery;

		query = query.orderBy("t0.created_at desc");

		ExpressionList<ProductStore> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<ProductStore> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}
}