package repository;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.Member;
import models.Merchant;
import models.merchant.ProductMerchant;
import play.db.ebean.Model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MemberRepository extends Model {

    private static final Finder<Long, Member> find = new Finder<>(Long.class, Member.class);

    public static Optional<Member> findById(Long id) {
        return Optional.ofNullable(find.where().eq("id", id).findUnique());
    }

    public static Optional<Member> findByPhoneAndMerchantId(String phoneNumber, Long merchantId) {
        return Optional.ofNullable(
                find.where()
                        .eq("phone", phoneNumber)
                        .eq("merchant.id", merchantId)
                        .findUnique()
        );
    }

    public static Integer getTotalPage(Query<Member> requestQuery) {
        Query<Member> query = requestQuery;
        ExpressionList<Member> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList().size();
    }

    public static Integer getTotalMember(Merchant merchant, String startDate, String endDate) throws Exception {
        Query<Member> memberQuery = find.where()
                .eq("merchant", merchant)
                .query();

        ExpressionList<Member> exp = memberQuery.where();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date start = simpleDateFormat.parse(startDate.concat(" 00:00:00.0"));
        Date end = simpleDateFormat.parse(endDate.concat(" 23:59:00.0"));

        Timestamp startTimestamp = new Timestamp(start.getTime());
        Timestamp endTimestamp = new Timestamp(end.getTime());
        exp.between("t0.created_at", startTimestamp, endTimestamp);
        return memberQuery.findList().size();
    }

    public static Query<Member> findAllMemberByMerchantId(Long merchantId) {
        Query<Member> memberQuery = Ebean.find(Member.class)
                .fetch("merchant")
                .where()
                .eq("merchant.id", merchantId)
                .query();
        return memberQuery;
    }

    public static List<Member> findAllMember(Query<Member> memberQuery, String sort, int offset, int limit, String status, String keyword) {
        Query<Member> query = memberQuery;
        if(!sort.equals("")) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.created_at desc");
        }
        ExpressionList<Member> exp = query.where();

        if (!status.equalsIgnoreCase("")) {
            Boolean isActive = Boolean.FALSE;
            if (status.equalsIgnoreCase("ACTIVE")) {
                isActive = Boolean.TRUE;
            }
            exp = exp.eq("t0.is_active", isActive);
        }
        exp = exp.ilike("t0.full_name", "%" + keyword + "%");
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

}
