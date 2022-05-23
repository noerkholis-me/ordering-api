package repository.pickuppoint;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.Merchant;
import models.Store;
import models.pupoint.*;
import play.db.ebean.Model;
import com.avaje.ebean.Expr;
import java.io.IOException;

import javax.persistence.PersistenceException;
import java.util.*;
import java.text.SimpleDateFormat;

public class PickUpPointSetupRepository extends Model {

    public static Finder<Long, PickUpPointSetup> find = new Finder<>(Long.class, PickUpPointSetup.class);

    public static PickUpPointSetup findByMerchantId (Long merchantId) {
			return find.where()
				.eq("merchant_id", merchantId)
				.eq("is_deleted", Boolean.FALSE)
				.findUnique();
	}
	
	public static PickUpPointSetup findByStoreId (Long storeId) {
		return find.where()
			.eq("id", storeId)
			.eq("is_deleted", Boolean.FALSE)
			.findUnique();
}

    public static PickUpPointSetup findByStoreIdandMerchantId(Long storeId, Long merchantId) {
			return find.where()
				.eq("store_id", storeId)
				.eq("merchant_id", merchantId)
				.eq("is_deleted", Boolean.FALSE)
				.findUnique();
	}

	public static PickUpPointSetup findByIdandMerchantId(Long id, Long merchantId) {
		return find.where()
			.eq("id", id)
			.eq("merchant_id", merchantId)
			.eq("is_deleted", Boolean.FALSE)
			.findUnique();
	}
	
	public static List<PickUpPointSetup> getListPickUpPointSetup(Query<PickUpPointSetup> reqQuery, String sort, String filter, int offset, int limit, Long storeId)
			throws IOException {
		Query<PickUpPointSetup> query = reqQuery;

		if (!"".equals(sort)) {
            query = query.orderBy(sort);
		} else {
			query = query.orderBy("t0.updated_at desc");
		}

		ExpressionList<PickUpPointSetup> exp = query.where();


		// exp = exp.disjunction();
		if(storeId != null && storeId != 0) {
			exp.eq("t0.store_id", storeId);
		}
		// exp = exp.like("t0.store_id", "%" + filter + "%");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<PickUpPointSetup> resData = query.findPagingList(limit).getPage(offset).getList();

		return resData;
	}

	public static List<PickUpPointSetup> getTotalData(Query<PickUpPointSetup> reqQuery)
			throws IOException {
		Query<PickUpPointSetup> query = reqQuery;

		ExpressionList<PickUpPointSetup> exp = query.where();

		query = exp.query();

		int total = query.findList().size();

		List<PickUpPointSetup> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}

}
