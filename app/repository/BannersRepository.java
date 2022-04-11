package repository;

import models.Merchant;
import models.Banners;
import play.db.ebean.Model;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import java.io.IOException;

import javax.persistence.PersistenceException;
import java.util.*;
import java.text.SimpleDateFormat;

public class BannersRepository extends Model {

    public static Finder<Long, Banners> find = new Finder<>(Long.class, Banners.class);

    public static Banners findByIdAndMerchantId(Long id, Long merchantId) {
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

    public static List<Banners> getDataBanners(Query<Banners> reqQuery, String sort, String filter, int offset, int limit)
			throws IOException {
		Query<Banners> query = reqQuery;

		if (!"".equals(sort)) {
            query = query.orderBy(sort);
		} else {
			query = query.orderBy("t0.updated_at desc");
		}

		ExpressionList<Banners> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


		exp = exp.disjunction();
		exp = exp.ilike("t0.banner_name", "%" + filter + "%");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<Banners> resData = query.findPagingList(limit).getPage(offset).getList();

		return resData;
	}

	public static List<Banners> getForHomeBanners(Query<Banners> reqQuery)
			throws IOException {

		Query<Banners> query = reqQuery;
		query = query.orderBy("t0.date_from desc");

		ExpressionList<Banners> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // exp = exp.endjunction();

		query = exp.query();

		int total = query.findList().size();

		List<Banners> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}

	public static List<Banners> getTotalData(Query<Banners> reqQuery)
			throws IOException {
		Query<Banners> query = reqQuery;

		query = query.orderBy("t0.created_at desc");

		ExpressionList<Banners> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		query = exp.query();

		int total = query.findList().size();

		List<Banners> resData = query.findPagingList(0).getPage(0).getList();

		return resData;
	}
}