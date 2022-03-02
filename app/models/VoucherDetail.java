package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;
import models.mapper.MapVoucher;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by nugraha on 5/18/17.
 */
@Entity
public class VoucherDetail extends BaseModel {
    private static final long serialVersionUID = 1L;
    private static final int STATUS_USED = 1;
    private static final int STATUS_UNUSED = 0;

    public static Finder<Long, VoucherDetail> find = new Finder<>(Long.class,
            VoucherDetail.class);

    public String code;

    @JsonProperty("order_number")
    public String orderNumber;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    public Voucher voucher;

    @Column(name = "status", columnDefinition = "integer default 0")
    @JsonProperty("status")
    public int status;

    @JsonIgnore
    @ManyToOne
    public Member member;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("used_at")
    public Date usedAt;

    //odoo
    @Column(name = "odoo_id")
    public Integer odooId;

    @javax.persistence.Transient
    @JsonProperty("start_date")
    private String getStartDate(){
        return CommonFunction.getDate(voucher.validFrom);
    }

    @javax.persistence.Transient
    @JsonProperty("end_date")
    private String getEndDate(){
        return CommonFunction.getDate(voucher.validTo);
    }

    @javax.persistence.Transient
    @JsonProperty("voucher_status")
    private String getVoucherStatus(){
        String result = "";
        if(status == STATUS_USED){
            result = "Used";
        }else{
//            if(voucher.validTo.compareTo(new Date()) == -1){
//                result = "Expired";
//            }else result = "Active";
        }
        return result;
    }

    public String getStatusName(){
        return (status == 0)? "Unused":"Used";
    }


    public String getUsedDate(){
        return CommonFunction.getDate(usedAt);
    }

    public static Page<VoucherDetail> page(Long id, int page, int pageSize, String sortBy, String order, String name) {
        return VoucherDetail.find
                .where()
                .eq("voucher.id", id)
                .ilike("code", "%" + name + "%")
                .eq("t0.is_deleted", false)
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);

    }

    public static int findRowCount(Long id) {
        return
                find.where()
                        .eq("voucher.id", id)
                        .eq("is_deleted", false)
                        .findRowCount();
    }

    public static Voucher findByCode2(String code) {
        Date now = new Date();
        List<VoucherDetail> detail = VoucherDetail.find
                .where()
                .or(Expr.eq("voucher.masking", code), Expr.eq("code", code))
                .le("voucher.validFrom", now)
                .ge("voucher.validTo", now)
                .ge("voucher.status", true)
                .eq("t0.status", STATUS_UNUSED)
                .eq("t0.is_deleted", false)
                .setMaxRows(1)
                .findList();
        if(detail.size() > 0){
            return detail.get(0).voucher;
        }else return null;
    }

    public static VoucherDetail findByCode(String code) {
        Date now = new Date();
        return VoucherDetail.find
                .where()
                .or(Expr.eq("voucher.masking", code), Expr.eq("code", code))
                .le("voucher.validFrom", now)
                .ge("voucher.validTo", now)
                .ge("voucher.status", true)
                .eq("t0.status", STATUS_UNUSED)
                .eq("t0.is_deleted", false)
                .setMaxRows(1)
                .findUnique();
    }

    public static List<MapVoucher> getAllData() {
        String sql = " select code, valid_from, valid_to, status from ( " +
                "select d.code, v.valid_from, v.valid_to, " +
                "case when d.status = 1 then 'Used' " +
                "when v.valid_from <= now() and v.valid_to >= now() then 'Active' " +
                "when v.valid_to < now() then 'Expired' " +
                "end as status " +
                " from voucher_detail d " +
                "left join voucher v on v.id = d.voucher_id ) as tbl";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("code", "code")
                .columnMapping("valid_from", "startDate")
                .columnMapping("valid_to", "endDate")
                .columnMapping("status", "status")
                .create();
        com.avaje.ebean.Query<MapVoucher> query = Ebean.find(MapVoucher.class);
        query.setRawSql(rawSql);
        List<MapVoucher> resData = query.findList();
        return resData;
    }

    public static List<MapVoucher> getVoucherMember(Long memberId) {
        String sql = "SELECT code, masking, status, valid_to, valid_from FROM (" +
                "SELECT a.code, e.masking, '1' as status, e.valid_to, e.valid_from " +
                "FROM voucher_detail a " +
                "LEFT JOIN voucher e ON e.id = a.voucher_id " +
                "WHERE a.member_id = "+memberId+" " +
                "UNION " +
                "SELECT f.code, g.masking, '0' as status, g.valid_to, g.valid_from " +
                "FROM voucher_detail f " +
                "LEFT JOIN voucher g ON g.id = f.voucher_id " +
                "LEFT JOIN voucher_member h ON h.voucher_id = g.id " +
                "WHERE h.member_id = "+memberId+" AND f.status = 0 ) tbl";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("code", "code")
                .columnMapping("masking", "masking")
                .columnMapping("valid_from", "startDate")
                .columnMapping("valid_to", "endDate")
                .columnMapping("status", "status")
                .create();
        com.avaje.ebean.Query<MapVoucher> query = Ebean.find(MapVoucher.class);
        query.setRawSql(rawSql);
        List<MapVoucher> resData = query.findList();
        return resData;
    }

}
