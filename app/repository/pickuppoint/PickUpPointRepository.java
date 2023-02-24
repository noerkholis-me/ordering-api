package repository.pickuppoint;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.pupoint.*;
import play.db.ebean.Model;

import java.io.IOException;

import java.util.*;

public class PickUpPointRepository extends Model {

    public static Finder<Long, PickUpPointMerchant> find = new Finder<>(Long.class, PickUpPointMerchant.class);

	public static PickUpPointMerchant findById (Long id) {
		return find.where()
				.eq("id", id)
				.eq("is_deleted", Boolean.FALSE)
				.findUnique();
	}

    public static PickUpPointMerchant findByMerchantId (Long merchantId) {
			return find.where()
				.eq("merchant_id", merchantId)
				.eq("is_deleted", Boolean.FALSE)
				.findUnique();
    }

    public static PickUpPointMerchant findByStoreIdandMerchantId(Long storeId, Long merchantId) {
			return find.where()
				.eq("store_id", storeId)
				.eq("merchant_id", merchantId)
				.eq("is_deleted", Boolean.FALSE)
				.findUnique();
	}

	public static PickUpPointMerchant findByIdandMerchantId(Long id, Long merchantId) {
		return find.where()
			.eq("id", id)
			.eq("merchant_id", merchantId)
			.eq("is_deleted", Boolean.FALSE)
			.findUnique();
	}
	
	public static List<PickUpPointMerchant> getListPickUpPoint(Query<PickUpPointMerchant> reqQuery, String sort, String filter, int offset, int limit, Long idStore)
			throws IOException {
		Query<PickUpPointMerchant> query = reqQuery;

		if (!"".equals(sort)) {
            query = query.orderBy(sort);
		} else {
			query = query.orderBy("t0.updated_at desc");
		}

		ExpressionList<PickUpPointMerchant> exp = query.where();


		// exp = exp.conjunction();
		if(idStore != null && idStore != 0){
			exp = exp.eq("t0.store_id", idStore);
		}
		exp = exp.ilike("t0.pupoint_name", "%" + filter + "%");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}
        if (filter != "" && filter != null) {
            offset = 0;
        }

		List<PickUpPointMerchant> resData = query.findPagingList(limit).getPage(offset).getList();

		return resData;
	}

	public static List<PickUpPointMerchant> getTotalData(Query<PickUpPointMerchant> reqQuery)
			throws IOException {
		Query<PickUpPointMerchant> query = reqQuery;

		ExpressionList<PickUpPointMerchant> exp = query.where();

		query = exp.query();

		int total = query.findList().size();

		List<PickUpPointMerchant> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}

}
