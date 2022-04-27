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

public class PickUpPointRepository extends Model {

    public static Finder<Long, PickUpPoint> find = new Finder<>(Long.class, PickUpPoint.class);

    public static PickUpPoint findByMerchantId (Long merchantId) {
			return find.where()
				.eq("merchant_id", merchantId)
				.eq("is_deleted", Boolean.FALSE)
				.findUnique();
    }

    public static PickUpPoint findByStoreIdandMerchantId(Long storeId, Long merchantId) {
			return find.where()
				.eq("store_id", storeId)
				.eq("merchant_id", merchantId)
				.eq("is_deleted", Boolean.FALSE)
				.findUnique();
	}

	public static PickUpPoint findByIdandMerchantId(Long id, Long merchantId) {
		return find.where()
			.eq("id", id)
			.eq("merchant_id", merchantId)
			.eq("is_deleted", Boolean.FALSE)
			.findUnique();
	}
	
	public static List<PickUpPoint> getListPickUpPoint(Query<PickUpPoint> reqQuery, String sort, String filter, int offset, int limit)
			throws IOException {
		Query<PickUpPoint> query = reqQuery;

		if (!"".equals(sort)) {
            query = query.orderBy(sort);
		} else {
			query = query.orderBy("t0.updated_at desc");
		}

		ExpressionList<PickUpPoint> exp = query.where();


		exp = exp.disjunction();
		exp = exp.ilike("t0.pupoint_name", "%" + filter + "%");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<PickUpPoint> resData = query.findPagingList(limit).getPage(offset).getList();

		return resData;
	}

	public static List<PickUpPoint> getTotalData(Query<PickUpPoint> reqQuery)
			throws IOException {
		Query<PickUpPoint> query = reqQuery;

		ExpressionList<PickUpPoint> exp = query.where();

		query = exp.query();

		int total = query.findList().size();

		List<PickUpPoint> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}

}
