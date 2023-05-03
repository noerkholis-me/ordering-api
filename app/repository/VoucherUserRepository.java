package repository;

import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

import models.Member;
import models.Merchant;
import models.ProductStore;
import models.voucher.VoucherMerchant;
import models.voucher.VoucherUser;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

public class VoucherUserRepository extends Model{
	
	public static Finder<Long, VoucherUser> find = new Finder<>(Long.class, VoucherUser.class);
	
	public static List<VoucherMerchant> findVoucherByMerchantAndMember (Merchant merchant, Member member, String filter, int offset, int limit) {
		
		String queryStr = "SELECT vmn.id FROM voucher_merchant_new vmn "
				+ "JOIN voucher_user vu ON vmn.id = vu.voucher_id "
				+ "WHERE vmn.merchant_id = '"+merchant.id+"' AND vu.user_id = '"+ member.id +"' AND vu.available = true ";
		
		RawSql rawsql = RawSqlBuilder.parse(queryStr).create();
		Query<VoucherMerchant> query = Ebean.find(VoucherMerchant.class).setRawSql(rawsql);
		
		ExpressionList<VoucherMerchant> exp = query.where();
        exp = exp.disjunction();
        exp = exp.or(Expr.ilike("vmn.tittle", "%" + filter + "%"), Expr.ilike("vmn.code", "%" + filter + "%"));
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
	}
}
