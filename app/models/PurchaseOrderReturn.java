package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by nugraha on 5/29/17.
 */
@Entity
public class PurchaseOrderReturn extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final String TYPE_REFUND = "F";
    public static final String TYPE_REPLACED = "R";
    public static final String STATUS_PENDING = "P";
    public static final String STATUS_APPROVED = "A";
    public static final String STATUS_REJECTED = "R";
    public static final String STATUS_COMPLETED = "C";

    public static Finder<Long, PurchaseOrderReturn> find = new Finder<>(Long.class,
            PurchaseOrderReturn.class);

    @Column(name = "return_number", unique = true, length = 30)
    public String returnNumber;

    @JsonIgnore
    @ManyToOne
    public Vendor vendor;

    @JsonIgnore
    @ManyToOne
    public PurchaseOrder purchaseOrder;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date date;

    @Column(name = "document_no")
    public String documentNumber;

    @Column(name = "type", length =1)
    public String type;

    @Column(name = "status", length =1)
    public String status;

    public String description;

    @JsonProperty("approved_note")
    public String approvedNote;

    @Column(name = "approved_by")
    @JsonIgnore
    @ManyToOne
    public UserCms approvedBy;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    //odoo
    @Column(name = "odoo_id")
    public Integer odooId;

    @OneToMany(mappedBy = "purchaseOrderReturn")
    @JsonProperty("purchase_order_return_detail")
    public List<PurchaseOrderReturnDetail> purchaseOrderReturnDetails;


    public String getType(){
        return type;
    }

    public String getDateFormated(){
        return CommonFunction.getDate(date);
    }

    public int getQty(){
        int qty = 0;
        for(PurchaseOrderReturnDetail detail : purchaseOrderReturnDetails){
            qty += detail.quantity;
        }
        return qty;
    }

    public String getStatusName(){
        String result = "";
        switch (status){
            case STATUS_PENDING : result = "Pending";break;
            case STATUS_APPROVED : result = "Approved";break;
            case STATUS_COMPLETED : result = "Completed";break;
            case STATUS_REJECTED : result = "Rejected";break;
        }
        return result;
    }

    public String getTypeName(){
        String result = "";
        switch (type){
            case TYPE_REFUND : result = "Refund";break;
            case TYPE_REPLACED : result = "Replaced";break;
        }
        return result;
    }

    public String getDocumentNumber(){
        String poCode = "";
        if(purchaseOrder != null){
            poCode = purchaseOrder.code;
        }
        return poCode;
    }


    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public Long vendorId;

    @javax.persistence.Transient
    public String dateStr;

    @javax.persistence.Transient
    public List<String> listQty;

    @javax.persistence.Transient
    public List<String> listProductId;

    public static Page<PurchaseOrderReturn> page(int page, int pageSize, String sortBy, String order, String name) {
        ExpressionList<PurchaseOrderReturn> qry = PurchaseOrderReturn.find
                .where()
                .ilike("returnNumber", "%" + name + "%")
                .eq("t0.is_deleted", false);

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);

    }

    public static int findRowCount() {
        return
                find.where()
                        .eq("t0.is_deleted", false)
                        .findRowCount();
    }



    public static String generateReturnCode(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        PurchaseOrderReturn po = PurchaseOrderReturn.find.where("created_at > '"+simpleDateFormat2.format(new Date())+" 00:00:00'")
                .order("created_at desc").setMaxRows(1).findUnique();
        String seqNum = "";
        if(po == null){
            seqNum = "001";
        }else{
            seqNum = po.returnNumber.substring(po.returnNumber.length() - 3);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 3);
        }
        String code = "RTP";
        code += simpleDateFormat.format(new Date()) + seqNum;
        return code;
    }

    public static void seed(String orderNumber, Vendor vendor, String type, String status, List<Long> productId, UserCms user){
        PurchaseOrderReturn model = new PurchaseOrderReturn();
        model.date = new Date();
        model.returnNumber = orderNumber;
        model.documentNumber = "";
        model.vendor = vendor;
        model.type = type;
        model.status = status;
        model.userCms = user;
        model.save();

        for(Long id : productId){
            PurchaseOrderReturnDetail detail = new PurchaseOrderReturnDetail();
            detail.product = Product.find.byId(id);
            detail.quantity = 1;
            detail.purchaseOrderReturn = model;
            detail.save();
        }
    }

    public static String convertType(String type){
        if (type.equalsIgnoreCase("refund")){
            return TYPE_REFUND;
        }else if (type.equalsIgnoreCase("replaced")){
            return TYPE_REPLACED;
        }
        return "";
    }
}
