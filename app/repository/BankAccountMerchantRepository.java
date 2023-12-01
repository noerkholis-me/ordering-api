package repository;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.BaseModel;
import models.Merchant;
import models.merchant.BankAccountMerchant;

import java.util.List;
import java.util.Optional;

public class BankAccountMerchantRepository extends BaseModel {

    private static final Finder<Long, BankAccountMerchant> find = new Finder<>(Long.class, BankAccountMerchant.class);

    public static Optional<BankAccountMerchant> findById(Long id) {
        return Optional.ofNullable(find.where().eq("id", id).findUnique());
    }

    public static Optional<BankAccountMerchant> findByAccountNumber(String accountNumber) {
        return Optional.ofNullable(find.where().eq("accountNumber", accountNumber).eq("isPrimary", true).findUnique());
    }

    public static Optional<BankAccountMerchant> findByAccountNumberNotPrimary(String accountNumber) {
        return Optional.ofNullable(find.where().eq("accountNumber", accountNumber).findUnique());
    }

    public static Optional<BankAccountMerchant> findByAccountNumberInSameMerchant(Merchant merchant, String accountNumber) {
        return Optional.ofNullable(find.where().eq("merchant", merchant).eq("accountNumber", accountNumber).findUnique());
    }

    public static List<BankAccountMerchant> findAll(Merchant merchant) {
        return find.where()
                .eq("merchant", merchant)
                .findList();
    }

    public static Query<BankAccountMerchant> findAllTablesQuery(Merchant merchant) {
        return find.where()
                .eq("isDeleted", false)
                .eq("merchant", merchant)
                .orderBy("updated_at desc");
    }

    public static List<BankAccountMerchant> getTotalPage(Query<BankAccountMerchant> requestQuery) {
        Query<BankAccountMerchant> query = requestQuery;
        ExpressionList<BankAccountMerchant> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<BankAccountMerchant> findAllWithPaging(Query<BankAccountMerchant> reqQuery, String sort, String filter, int offset, int limit) {
        Query<BankAccountMerchant> query = reqQuery;
        if (!sort.equals(""))
            query = query.orderBy(sort);
        else
            query = query.orderBy("t0.updated_at desc");

        ExpressionList<BankAccountMerchant> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("t0.account_name", "%" + filter + "%");
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        if (filter != "" && filter != null) {
            offset = 0;
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }


}
