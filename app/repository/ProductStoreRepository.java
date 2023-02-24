package repository;

import jdk.nashorn.internal.runtime.options.Option;
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

	public static Optional<ProductStore> findById(Long id) {
		return Optional.ofNullable(find.where().eq("id", id).findUnique());
	}

    public static ProductStore findByIdAndMerchantId(Long id, Merchant merchant) {
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

    public static ProductStore findForCust(Long productId, Long storeId, Merchant merchant) {
        try {
			return find.where()
				.eq("t0.product_id", productId)
				.eq("t0.store_id", storeId)
				.eq("merchant", merchant)
				.eq("t0.is_deleted", Boolean.FALSE)
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
		exp = exp.ilike("t0.product_name", "%" + filter + "%");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}
        if (filter != "" && filter != null) {
            offset = 0;
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

	public static List<ProductStore> findByStoreId(Store store) {
		try {
			return find.where()
					.eq("store", store)
					.eq("t0.is_deleted", Boolean.FALSE)
					.findList();
		} catch (PersistenceException e) {
			e.printStackTrace();
			return null;
		}
	}
}