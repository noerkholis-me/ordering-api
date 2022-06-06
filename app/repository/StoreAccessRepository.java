package repository;

import jdk.nashorn.internal.runtime.options.Option;
import models.*;
import models.store.*;
import play.db.ebean.Model;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import java.io.IOException;

import javax.persistence.PersistenceException;
import java.util.*;
import java.text.SimpleDateFormat;

public class StoreAccessRepository extends Model {

    public static Finder<Long, StoreAccess> find = new Finder<>(Long.class, StoreAccess.class);
    public static Finder<Long, StoreAccessDetail> findDetail = new Finder<>(Long.class, StoreAccessDetail.class);

	public static StoreAccess findById(Long id) {
		return find.where().eq("user_merchant_id", id).eq("is_deleted", false).eq("is_active", true).findUnique();
    }
    
    public static StoreAccessDetail findByIdStore(Long id) {
		return findDetail.where().eq("store_id", id).findUnique();
    }

    public static StoreAccess findByIdAndMerchantId(Long id, Long merchantId) {
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

    public static List<StoreAccess> getDataStoreAccess(Query<StoreAccess> reqQuery, String sort, String filter, int offset, int limit)
			throws IOException {
		Query<StoreAccess> query = reqQuery;

		if (!"".equals(sort)) {
            query = query.orderBy(sort);
		} else {
			query = query.orderBy("t0.updated_at desc");
		}

		ExpressionList<StoreAccess> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<StoreAccess> resData = query.findPagingList(limit).getPage(offset).getList();

		return resData;
	}

    public static List<StoreAccess> getDataStoreAccess(Query<StoreAccess> reqQuery)
			throws IOException {
        Query<StoreAccess> query = reqQuery;
		query = query.orderBy("t0.updated_at desc");

		ExpressionList<StoreAccess> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<StoreAccess> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}

	public static List<StoreAccess> getTotalData(Query<StoreAccess> reqQuery)
			throws IOException {
		Query<StoreAccess> query = reqQuery;

		query = query.orderBy("t0.created_at desc");

		ExpressionList<StoreAccess> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<StoreAccess> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}

	public static List<StoreAccessDetail> getDetailData(Query<StoreAccessDetail> reqQuery)
			throws IOException {
		Query<StoreAccessDetail> query = reqQuery;

		query = query.orderBy("t0.created_at desc");

		ExpressionList<StoreAccessDetail> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<StoreAccessDetail> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
    }
    
    public static List<StoreAccessDetail> findByIdAssign(Query<StoreAccessDetail> reqQuery)
			throws IOException {
		Query<StoreAccessDetail> query = reqQuery;

		ExpressionList<StoreAccessDetail> exp = query.where();

		query = exp.query();

		int total = query.findList().size();

		List<StoreAccessDetail> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}
}