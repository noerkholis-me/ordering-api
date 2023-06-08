package repository;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
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

	public static Banners findByIdAndMerchantId(Long id, Merchant merchant) {
		try {
			return find.where()
					.eq("id", id)
					.eq("merchant", merchant)
					.eq("is_deleted", Boolean.FALSE)
					.findUnique();
		} catch (PersistenceException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Banners> getDataBanners(Long merchantId, String sort, String filter, int offset, int limit) {
		String sorting;
		if (!"".equals(sort)) {
			sorting = sort;
		} else {
			sorting = "DESC";
		}

		String querySql = "SELECT b.id FROM banners b "
			+ "WHERE b.merchant_id = " + merchantId + " AND b.is_deleted = false "
			+ "ORDER BY b.updated_at " + sorting;

		RawSql rawSql = RawSqlBuilder.parse(querySql).create();
		Query<Banners> query = Ebean.find(Banners.class).setRawSql(rawSql);

		ExpressionList<Banners> exp = query.where();
		exp = exp.disjunction();
		exp = exp.ilike("b.banner_name", "%" + filter + "%");
		exp = exp.endJunction();
		query = exp.query();

		return query.findPagingList(limit).getPage(offset).getList();
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