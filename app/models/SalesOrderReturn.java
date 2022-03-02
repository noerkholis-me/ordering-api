package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.ApiFilter;
import com.hokeba.api.ApiFilterValue;
import com.hokeba.api.ApiResponse;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.response.MapReturStatus;
import com.hokeba.mapping.response.MapReturStatusDetail;
import com.hokeba.mapping.response.MapReturnMerchant;
import com.hokeba.util.CommonFunction;

import javax.persistence.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
public class SalesOrderReturn extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final String TYPE_REFUND = "F";
    public static final String TYPE_REPLACED = "R";
    public static final String STATUS_PENDING = "P";
    public static final String STATUS_APPROVED = "A";
    public static final String STATUS_REJECTED = "R";
    public static final String STATUS_COMPLETED = "C";
    public static final String STATUS_ONPROGRESS = "O";

    public static Finder<Long, SalesOrderReturn> find = new Finder<>(Long.class,
            SalesOrderReturn.class);

    @JsonIgnore
    @ManyToOne
    public SalesOrder salesOrder;

    @JsonIgnore
    @ManyToOne
    public SalesOrderSeller salesOrderSeller;

    @JsonIgnore
    @ManyToOne
    public SalesOrderReturnGroup salesOrderReturnGroup;

    @Column(name = "return_number", unique = true, length = 30)
    @JsonProperty("return_no")
    public String returnNumber;

    @JsonIgnore
    @ManyToOne
    public Member member;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date date;

    @Column(name = "document_no")
    @JsonProperty("tracking_number")
    public String documentNumber;

    @Column(name = "type", length =1)
    public String type;

    @Column(name = "status", length =1)
    public String status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "request_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date requestAt;

    @JsonProperty("return_description")
    @Column(name = "description")
    public String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "schedule_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date scheduleAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "send_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date sendAt;

    @JsonProperty("note")
    public String note;
//
//    @Column(name = "approved_by")
//    @JsonIgnore
//    @ManyToOne
//    public UserCms approvedBy;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @OneToMany(mappedBy = "salesOrderReturn")
    @JsonProperty("items")
    public List<SalesOrderReturnDetail> salesOrderReturnDetails;

    //odoo
    @Column(name = "odoo_id")
    public Integer odooId;

    @Column(name = "pengeluaran_odoo_id")
    public Integer pengeluaranOdooId;

    @Column(name = "approved_by")
    public String approvedBy;

    @Column(name = "rejected_by")
    public String rejectedBy;

    @Column(name = "shipped_by")
    public String shippedBy;

    @Column(name = "delivered_by")
    public String deliveredBy;

//    @Transient
//    @JsonProperty("product_id")
//    public Long getProductId(){
//        return salesOrderDetail.getProductId();
//    }
//    @Transient
//    @JsonProperty("product_name")
//    public String getProductName(){
//        return salesOrderDetail.productName;
//    }
//    @Transient
//    @JsonProperty("product_image")
//    public String getProductImage(){
//        return salesOrderDetail.getImage();
//    }
//    @Transient
//    @JsonProperty("product_price")
//    public Double getProductPrice(){
//        return salesOrderDetail.price;
//    }
    @Transient
    @JsonProperty("order_id")
    public Long getOrderId(){
        return salesOrder.id;
    }

    @javax.persistence.Transient
    @JsonProperty("order_no")
    public String getOrderNo(){
        return salesOrderSeller.orderNumber;
    }
    @javax.persistence.Transient
    @JsonProperty("order_date")
    public String getOrderDate(){
        return CommonFunction.getDate(salesOrder.orderDate);
    }
    @javax.persistence.Transient
    @JsonProperty("return_date")
    public String getReturnDate(){
        return CommonFunction.getDate(date);
    }
    @javax.persistence.Transient
    @JsonProperty("return_type")
    public String getReturnType(){
        return getTypeName();
    }
    @javax.persistence.Transient
    @JsonProperty("return_customer")
    public String getReturnCustomer(){
        return salesOrder.member.fullName;
    }
    @javax.persistence.Transient
    @JsonProperty("return_merchant")
    public String getReturnMerchant(){
        return salesOrderSeller.getSellerName();
    }
    @javax.persistence.Transient
    @JsonProperty("return_id")
    public Long getReturnId(){
        return id;
    }

    @javax.persistence.Transient
    @JsonProperty("status_shipping")
    public MapReturStatus getStatusShipping() {
        if (type == null) return null;
        if (type.equals(SalesOrderReturn.TYPE_REPLACED) && !status.equals(SalesOrderReturn.STATUS_REJECTED)){
            boolean isProcessing = false;
            boolean isShipped = false;
            boolean isCompleted = false;

            switch (status){
                case SalesOrderReturn.STATUS_APPROVED :
                    isProcessing = true;
                    break;
                case SalesOrderReturn.STATUS_COMPLETED :
                    isProcessing = true;
                    isShipped = true;
                    isCompleted = true;
                    break;
                case SalesOrderReturn.STATUS_ONPROGRESS :
                    isProcessing = true;
                    isShipped = true;
                    break;
            }

            MapReturStatusDetail processing = new MapReturStatusDetail(isProcessing, CommonFunction.getDateTime(requestAt));
            MapReturStatusDetail shipped = new MapReturStatusDetail(isShipped, CommonFunction.getDateTime(scheduleAt));
            MapReturStatusDetail completed = new MapReturStatusDetail(isCompleted, CommonFunction.getDateTime(sendAt));

            return new MapReturStatus(processing, shipped, completed);

        }
        return null;
    }

    public String getStatus(){
        return getStatusName();
    }

    public String getStatusName(){
        String result = "";
        switch (status){
            case STATUS_PENDING : result = "Pending";break;
            case STATUS_APPROVED : result = "Approved";break;
            case STATUS_COMPLETED : result = "Completed";break;
            case STATUS_REJECTED : result = "Rejected";break;
            case STATUS_ONPROGRESS : result = "On Progress";break;
        }
        return result;
    }

    public String getTypeName(){
        if (type == null) return "";
        String result = "";
        switch (type){
            case TYPE_REFUND : result = "Refund";break;
            case TYPE_REPLACED : result = "Replaced";break;
        }
        return result;
    }

    public static String generateReturnCode(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMM");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM");
        SalesOrderReturn so = SalesOrderReturn.find.where("created_at > '"+simpleDateFormat2.format(new Date())+"-01 00:00:00'")
                .order("created_at desc").setMaxRows(1).findUnique();
        String seqNum = "";
        if(so == null){
            seqNum = "0001";
        }else{
            seqNum = so.returnNumber.substring(so.returnNumber.length() - 4);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "0000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 4);
        }
        String code = "RTS";
        code += simpleDateFormat.format(new Date()) + seqNum;
        return code;
    }

    public String getSendDateFormated(){
        return CommonFunction.getDateTime(sendAt);
    }

    public String getRequestDateFormated(){
        return CommonFunction.getDateTime(date);
    }

    public String getScheduleDateFormated(){
        return CommonFunction.getDateTime(scheduleAt);
    }

    public int getQty(){
        int qty = 0;
        for(SalesOrderReturnDetail detail : salesOrderReturnDetails){
            qty += detail.quantity;
        }
        return qty;
    }

    public static Page<SalesOrderReturn> page(Long id, int page, int pageSize, String sortBy, String order, String name) {
        ExpressionList<SalesOrderReturn> qry = SalesOrderReturn.find
                .where()
                .ilike("returnNumber", "%" + name + "%")
                .eq("t0.is_deleted", false)
                .eq("salesOrderReturnGroup.id", id);

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);

    }

    public static int findRowCount(Long id) {
        return
                find.where()
                        .eq("t0.is_deleted", false)
                        .eq("salesOrderReturnGroup.id", id)
                        .findRowCount();
    }

    public static String convertType(String type){
        if (type.equalsIgnoreCase("refund")){
            return TYPE_REFUND;
        }else if (type.equalsIgnoreCase("replace")){
            return TYPE_REPLACED;
        }
        return "";
    }

    public static <T> BaseResponse<T> getDataMerchant(com.avaje.ebean.Query<T> reqQuery, String type, String sort, String filter, int offset, int limit)
            throws IOException {
        com.avaje.ebean.Query<T> query = reqQuery;

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        }

        ExpressionList<T> exp = query.where();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


        exp = exp.conjunction();
        exp = exp.ilike("return_number", filter + "%");
        switch (type){
            case "new" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("status", "in", new ApiFilterValue[]{new ApiFilterValue(SalesOrderReturn.STATUS_PENDING)}));
                break;
            case "packed" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("status", "in", new ApiFilterValue[]{new ApiFilterValue(SalesOrderReturn.STATUS_APPROVED)}));
                break;
            case "shipped" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("status", "in", new ApiFilterValue[]{new ApiFilterValue(SalesOrderReturn.STATUS_ONPROGRESS)}));
                break;
            case "delivered" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("status", "in", new ApiFilterValue[]{new ApiFilterValue(SalesOrderReturn.STATUS_COMPLETED)}));
                break;
            case "rejected" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("status", "in", new ApiFilterValue[]{new ApiFilterValue(SalesOrderReturn.STATUS_REJECTED)}));
                break;
        }

        exp = exp.endJunction();

        query = exp.query();

        int total = query.findList().size();

        if (limit != 0) {
            query = query.setMaxRows(limit);
        }

        List<T> resData = query.findPagingList(limit).getPage(offset).getList();

        BaseResponse<T> response = new BaseResponse<>();
        response.setData(new ObjectMapper().convertValue(resData, MapReturnMerchant[].class));
        response.setMeta(total, offset, limit);
        response.setMessage("Success");

        return response;
    }
}
