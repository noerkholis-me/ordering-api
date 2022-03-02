package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="s_order_payment")
public class SOrderPayment extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final String VERIFY = "V";
    public static final String PAYMENT_VERIFY = "P";
    public static final String PAYMENT_REJECT = "R";
    public static final String COD_VERIFY = "COD";

    public static Finder<Long, SOrderPayment> find = new Finder<>(Long.class,SOrderPayment.class);

    @JsonIgnore
    @OneToOne(cascade = { CascadeType.ALL })
    public SOrder order;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "confirm_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date confirmAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "void_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date voidAt;

    @Column(name = "confirm_by")
    @JsonIgnore
    @ManyToOne
    public UserCms confirmBy;

    @JsonProperty("total_transfer")
    public Double totalTransfer;

    @Column(unique = true)
    @JsonProperty("invoice_no")
    public String invoiceNo;

    @JsonProperty("debit_acc_name")
    public String debitAccountName;

    @JsonProperty("debit_acc_no")
    public String debitAccountNumber;

    @JsonProperty("image_url")
    public String imageUrl;

    @JsonProperty("comments")
    public String comments;

    public String status;
    
    //MIDTRANS DATA
    @JsonProperty("transaction_id")
    public String transactionId;
    @JsonProperty("eci_code")
	public String eciCode;
    @JsonProperty("payment_instalment")
	public String paymentInstalment;
    @JsonProperty("va_number")
	public String vaNumber;
    @JsonProperty("company_code")
	public String companyCode;
    @JsonProperty("settlement")
	public boolean settlement;
    
    @JsonProperty("payment_type")
    public String paymentType;
    @JsonProperty("bank")
    public String bank;
    @JsonProperty("card_type")
    public String cardType;
    @JsonProperty("instalment_cost")
    public Double instalmentCost;

    public String getImageLink(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }

    public String getConfirmAt(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return confirmAt!=null? simpleDateFormat.format(confirmAt):"";
    }
    
    public String getConfirmTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return confirmAt!=null? simpleDateFormat.format(confirmAt):"";
    }

    public String getBank(){
    	return this.bank;
    }
    
    public Double getInstalmentCost() {
    	return this.instalmentCost == null ? 0D : this.instalmentCost;
    }

    public String getTotalFormat(){
        return CommonFunction.numberFormat(totalTransfer==null ? 0D : totalTransfer);
    }

//    @Transient
//    public String getStrStatus(){
//        String result = "";
//        switch (status){
//            case VERIFY : result = "Verify";break;
//            case PAYMENT_VERIFY :
//                result = salesOrder.status.equals(SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT) ? "Expired" : "Payment Verify";
//                break;
//            case COD_VERIFY :
//                result = salesOrder.status.equals(SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT) ? "Expired" : "COD Verify";
//                break;
//            case PAYMENT_REJECT : result = "Rejected";break;
//            default: status = "Invalid Status";
//        }
//
//        return result;
//    }

    public static Page<SOrderPayment> page(int page, int pageSize, String sortBy, String order, String name, String filter) {
        ExpressionList<SOrderPayment> qry = SOrderPayment.find
                .where()
                .ilike("order.orderNumber", "%" + name + "%")
                .eq("t0.is_deleted", false);

        if(!filter.equals("")){
            qry.eq("t0.status", filter);
        }

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

    public static String generateInvoiceCode(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMM");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM");
        SOrderPayment so = SOrderPayment.find.where("t0.created_at > '"+simpleDateFormat2.format(new Date())+"-01 00:00:00' and invoice_no is not null")
                .order("t0.created_at desc").setMaxRows(1).findUnique();
        String seqNum = "";
        if(so == null){
            seqNum = "00001";
        }else{
            seqNum = so.invoiceNo.substring(so.invoiceNo.length() - 5);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "00000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 5);
        }
        String code = "INV";
        code += simpleDateFormat.format(new Date()) + seqNum;
        return code;
    }
}
