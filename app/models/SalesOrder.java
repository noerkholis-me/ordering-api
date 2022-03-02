package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.mapping.request.MapOrder;
import com.hokeba.mapping.request.MapOrderDetail;
import com.hokeba.mapping.request.MapOrderSeller;
import com.hokeba.mapping.request.MapVoucherCode;
import com.hokeba.mapping.request.MapOrderRedeem;
import com.hokeba.mapping.response.MapOrderSellerProduct;
import com.hokeba.mapping.response.MapOrderUserList;
import com.hokeba.mapping.response.MapOrderUserListDetail;
import com.hokeba.mapping.response.MapOrderUserStatus;
import com.hokeba.payment.kredivo.KredivoService;
import com.hokeba.payment.midtrans.MidtransService;
import com.hokeba.payment.midtrans.response.TransactionStatus;
import com.hokeba.payment.midtrans.response.TransactionToken;
import com.hokeba.shipping.beeexpress.util.ShippingUtil;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Encryption;

import controllers.users.LoyaltyController;
import play.Logger;
import play.libs.Json;

import com.hokeba.scheduler.ExpireOrderScheduler;

import javax.persistence.*;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.joda.time.format.DateTimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by hendriksaragih on 4/26/17.
 */
@Entity
public class SalesOrder extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final String PAYMENT_METHOD_COD = "COD";
    public static final String PAYMENT_METHOD_TRANSFER = "TRF";

    public static final String ORDER_STATUS_CHECKOUT = "CH";
    public static final String ORDER_STATUS_VERIFY = "OV";
    public static final String ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION = "WC";
    public static final String ORDER_STATUS_PICKING = "PI";
    public static final String ORDER_STATUS_PACKING = "PA";
    public static final String ORDER_STATUS_ON_DELIVERY = "OD";
    public static final String ORDER_STATUS_RECEIVE_BY_CUSTOMER = "RC";
    public static final String ORDER_STATUS_CUSTOMER_NOT_AT_THE_ADDRESS_STATE = "NA";
    public static final String ORDER_STATUS_EXPIRE_PAYMENT = "EX";
    public static final String ORDER_STATUS_CANCEL = "CA";
    public static final String ORDER_STATUS_CANCEL_BY_CUSTOMER_SERVICE = "CC";
    public static final String ORDER_STATUS_RETURN = "RT";
    public static final String ORDER_STATUS_REPLACED = "RP";

    public static final List<String> ORDER_NEW = Arrays.asList(ORDER_STATUS_VERIFY);
    public static final List<String> ORDER_PAID = Arrays.asList(ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION);
    public static final List<String> ORDER_PROCESSED = Arrays.asList(ORDER_STATUS_PICKING, ORDER_STATUS_PACKING, ORDER_STATUS_ON_DELIVERY);
    public static final List<String> ORDER_COMPLETED = Arrays.asList(ORDER_STATUS_RECEIVE_BY_CUSTOMER);
    public static final List<String> ORDER_RETURN = Arrays.asList(ORDER_STATUS_RETURN);
    public static final List<String> ORDER_FAILED = Arrays.asList(ORDER_STATUS_CANCEL, ORDER_STATUS_CUSTOMER_NOT_AT_THE_ADDRESS_STATE);

    public static final List<String> ORDER_PENDING = Arrays.asList(ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION, ORDER_STATUS_EXPIRE_PAYMENT);
    public static final List<String> PAYMENT_CANCEL = Arrays.asList(ORDER_STATUS_EXPIRE_PAYMENT, ORDER_STATUS_CANCEL, ORDER_STATUS_CANCEL_BY_CUSTOMER_SERVICE);

    public static Finder<Long, SalesOrder> find = new Finder<>(Long.class, SalesOrder.class);

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    @JsonProperty("order_date")
    public Date orderDate;

    @Column(unique = true)
    @JsonProperty("order_number")
    public String orderNumber;

    public Double discount;

    public Double voucher;

    public Double subtotal;

    @JsonProperty("shipping")
    public Double shipping;

    @JsonProperty("total_price")
    public Double totalPrice;

    @JsonIgnore
    @ManyToOne
    public Member member;

    @ManyToOne
    @JsonProperty("shipment_address")
    public Address shipmentAddress;

    @ManyToOne
    @JsonProperty("pickup_point")
    public CourierPointLocation courierPointLocation;

    @ManyToOne
    public Courier courier;

    @ManyToOne
    @JsonProperty("billing_address")
    public Address billingAddress;

    @ManyToOne
    @JsonProperty("bank")
    public Bank bank;

    public String status;

    @JsonProperty("expired_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date expiredDate;

    public String struct; //placeholder for payment gateway token

    @JsonProperty("shipment_type")
    public String shipmentType; //temporary placeholder for payment gateway url

    @JsonProperty("payment_type")
    public String paymentType;

    @JsonProperty("email_notif")
    public String emailNotif; //temporary placeholder for order notes
    
    @JsonProperty("device_type")
    public String deviceType;

    @OneToMany(mappedBy = "salesOrder")
    @JsonProperty("details")
    public List<SalesOrderDetail> salesOrderDetail;

    @OneToMany(mappedBy = "salesOrder")
    @JsonProperty("sellers")
    public List<SalesOrderSeller> salesOrderSellers;

    @OneToOne(mappedBy = "salesOrder")
    @JsonProperty("sales_order_payment")
    public SalesOrderPayment salesOrderPayment;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    @JsonProperty("approved_date")
    public Date approvedDate;

    @Column(name = "approved_by")
    @JsonIgnore
    @ManyToOne
    public UserCms approvedBy;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @JsonIgnore
    @OneToMany(mappedBy = "salesOrder",cascade = CascadeType.ALL)
    public List<LoyaltyPoint> loyaltyPoint = new ArrayList<LoyaltyPoint>();

    @JsonProperty("checkout_type")
    public Long checkoutType;
    
    @JsonIgnore
    public Long getCheckoutType() {
    	return checkoutType;    	
    }
    
    public void setCheckoutType(Long checkoutType) {
    	this.checkoutType = checkoutType;
    }
    
    @Transient
    @JsonProperty("loyalty_point")
    public Long getLoyaltyPoint(){
//    	return loyaltyPoint.isEmpty() ? 0L : loyaltyPoint.get(0).point;
    	if(loyaltyPoint.isEmpty()) {
    		return 0L;
    	}
    	else {
        	LoyaltyPoint min = null;
        	for(LoyaltyPoint lp : loyaltyPoint) {
        		if(lp.point < 0) min = lp;
        	}
        	return min == null ? 0L : min.point;
    	}
    }

    @Transient
    @JsonProperty("payment_time")
    public String paymentTime() {
    	return salesOrderPayment!=null? salesOrderPayment.getConfirmTime() :"";
    }

    @Transient
    @JsonProperty("tranfer_amount")
    public Double getTranferAmount(){
        return subtotal;
    }
    @Transient
    @JsonProperty("expired")
    public String getExpired(){
        return CommonFunction.getDateTime(expiredDate);
    }
    @Transient
    @JsonProperty("start")
    public String getStart(){
        return CommonFunction.getDateTime(new Date());
    }
    @Transient
    @JsonProperty("order_date_string")
    public String getOrderDateString(){
        return CommonFunction.getDate(orderDate);
    }
    @Transient
    @JsonProperty("payment_method")
    public String getPaymentMethod(){
    	String result = "";
        if (paymentType != null) {
	        switch (paymentType){
	            case MidtransService.PAYMENT_METHOD_MIDTRANS : {
	            	result = fetchMidtransPaymentInfo();
	            	break;
	            }
	            case KredivoService.PAYMENT_METHOD_KREDIVO : {
	            	result = fetchKredivoPaymentInfo();
	            	break;
	            }
	        }
        }

        return result;
    }
    @Transient
    @JsonProperty("sub_total")
    public Double getSubTotal(){
        return totalPrice;
    }
    @Transient
    @JsonProperty("total")
    public Double getTotal(){
        return subtotal + getInstalmentCost();
    }
    
    @JsonGetter("instalment_cost")
    public Double getInstalmentCost() {
    	return this.salesOrderPayment == null ? 0D : this.salesOrderPayment.getInstalmentCost();
    }
    
    @JsonGetter("midtrans_info")
    public TransactionToken getMidtransInfo() {
    	if (MidtransService.PAYMENT_METHOD_MIDTRANS.equals(this.paymentType)) {
    		TransactionToken midtransInfo = new TransactionToken();
    		midtransInfo.redirectUrl = this.shipmentType;
    		midtransInfo.token = this.struct;
    		return midtransInfo;
    	}
    	return null;
    }
    
    @JsonGetter("kredivo_url")
    public String getKredivoUrl() {
    	if (KredivoService.PAYMENT_METHOD_KREDIVO.equals(this.paymentType)) {
    		return this.shipmentType;
    	}
    	return null;
    }
    
    @JsonGetter("order_notes")
    public String getOrderNotes() {
    	return this.emailNotif == null ? "" : this.emailNotif;
    }

    public long fetchRoundedGrandTotal() {
		return Math.round(Math.ceil(this.subtotal));
	}
    
    @Transient
    @JsonProperty("status_text")
    public String getStatusText(){
        return convertStatusName(status);
    }
    @Transient
    @JsonProperty("order_number_encrypt")
    public String getOrderNumberEncrypt(){
        return Encryption.EncryptAESCBCPCKS5Padding(orderNumber);
    }

//    @Transient
//    public String getStrStatus(){
//        String result = "";
//        int countStatus = 0;
//        String status = "";
//        Map<String, Integer> mapStatus = new HashMap<>();
//        for(SalesOrderSeller item:salesOrderSellers){
//            if(!status.contains(item.status)){
//                countStatus++;
//                status += item.status+";";
//                mapStatus.put(item.status,1);
//            }else{
//                if(mapStatus.containsKey(item.status)){
//                    mapStatus.put(item.status,mapStatus.get(item.status).intValue()+1);
//                }
//            }
//        }
//        if(countStatus == 1){
//            result = convertStatusName(status.replace(";",""));
//        }else{
//            String tmp = "";
//            for(Map.Entry<String, Integer> entry : mapStatus.entrySet()){
//                tmp += convertStatusName(entry.getKey())+" ["+entry.getValue()+"];";
//            }
//            if (!tmp.isEmpty()){
//                tmp = tmp.substring(0, tmp.length()-1);
//            }
//            result = "<a href=\"#\" onclick=\"showStatusDetail('"+tmp+"')\">View Status</a>";
//        }
//
//        return result;
//    }
    
    private String convertStatusName(String status){
        String result = "";
        switch (status){
            case ORDER_STATUS_VERIFY : result = "Order Verified";break;
            case ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION :
                result = salesOrderPayment != null ? "Waiting Payment Confirmation" : "Unpaid";
                break;
            case ORDER_STATUS_EXPIRE_PAYMENT : result = "Expire Payment";break;
            case ORDER_STATUS_PICKING : result = "Picking";break;
            case ORDER_STATUS_PACKING : result = "Packing";break;
            case ORDER_STATUS_ON_DELIVERY : result = "On Delivery";break;
            case ORDER_STATUS_RECEIVE_BY_CUSTOMER : result = "Received By Customer";break;
            case ORDER_STATUS_CUSTOMER_NOT_AT_THE_ADDRESS_STATE : result = "Customer Not At The Address State";break;
            case ORDER_STATUS_CANCEL : result = "Cancel";break;
            case ORDER_STATUS_RETURN : result = "Return";break;
            case ORDER_STATUS_REPLACED : result = "Replaced";break;
            case ORDER_STATUS_CANCEL_BY_CUSTOMER_SERVICE : result = "Cancel By Customer Service";break;
            default: result = "Invalid Status";
        }
        return result;
    }
    
    private String fetchMidtransPaymentInfo() {
    	String result = "MIDTRANS";
    	SalesOrderPayment payment = this.salesOrderPayment;
    	String paymentType = payment != null ? payment.paymentType : null;
		String installment = payment != null ? payment.paymentInstalment : null;
		String bankName = payment != null ? payment.getBank() : null; //dont use public attribute access, must use getter for bank value
    	if (paymentType != null && !paymentType.isEmpty()) {
    		switch (paymentType) {
    			case "bank_transfer" : {
    				result += " - Bank Transfer";
    				if (bankName != null && !bankName.isEmpty()) {
    					result += " " + bankName;
    				}
    				break;
    			}
    			case "credit_card": {
    				result += " - Credit Card";
    				if (bankName != null && !bankName.isEmpty()) {
    					result += " " + bankName;
    				}
    				if (installment != null && !installment.isEmpty()) {
    					result += " " + installment + " Month Installment";
    				}
    				break;
    			}
    			case "gopay" : {
    				result += " - GoPay";
    				break;
    			}
    			case "echannel" : {
    				result += " - Mandiri Bill";
    				break;
    			}
    		}
    	}
    	return result;
    }
    
    private String fetchKredivoPaymentInfo() {
    	String result = "KREDIVO";
    	String paymentType = salesOrderPayment != null ? salesOrderPayment.paymentType : null;
    	if (paymentType != null && !paymentType.isEmpty()) {
    		switch (paymentType) {
    			case "30_days" : {
    				result += " - 30 days";
    				break;
    			}
    			case "3_months": {
    				result += " - 3 Months Installment";
    				break;
    			}
    			case "6_months" : {
    				result += " - 6 Months Installment";
    				break;
    			}
    			case "12_months" : {
    				result += " - 1 Year Installment";
    				break;
    			}
    		}
    	}
    	return result;
    }

    public boolean isCOD(){
//        return paymentType.equals("COD");
    	return false;
    }

    @Transient
    @JsonProperty("payment_status")
    public String getPaymentStatus(){
        String result = "Unpaid";

        if(status.equals(ORDER_STATUS_RETURN)){
            result = "Refund";
        }

        if (salesOrderPayment != null){
            if(salesOrderPayment.status.equals(SalesOrderPayment.PAYMENT_VERIFY)){
                if(status.equals(ORDER_STATUS_EXPIRE_PAYMENT))
                    result = "Cancel";
                else result = "Waiting for Confirmation";
            }else if(salesOrderPayment.status.equals(SalesOrderPayment.VERIFY)){
                result = "Paid";
            }else if(salesOrderPayment.status.equals(SalesOrderPayment.PAYMENT_REJECT)){
                result = "Cancel";
            }
        }

        return result;
    }

    @Transient
    @JsonProperty("currency")
    public String getCurrency(){
        return "IDR";
    }

    @Transient
    @JsonProperty("bank_name")
    public String getBankName(){
        return bank != null ? bank.bankName : "";
    }

    @Transient
    @JsonProperty("qty")
    public int getQty(){
        return salesOrderDetail.size();
    }

    @Transient
    @JsonProperty("shipped_date")
    public String getShippedDate(){
        Date sendDate = null;
        for(SalesOrderSeller seller : salesOrderSellers){
            if(seller.sentDate != null){
                sendDate = seller.sentDate;
            }
        }
        return (sendDate != null)? CommonFunction.getDate2(sendDate):"";
    }

    @Transient
    @JsonProperty("product_list")
    public List<MapOrderUserListDetail> getProductList(){
        List<SalesOrderDetail> items = salesOrderDetail;
        List<MapOrderUserListDetail> result = new ArrayList<>();
        for(SalesOrderDetail item : items){
            MapOrderUserListDetail product = new MapOrderUserListDetail(item.productName, item.getImageUrl(), item.quantity);
            result.add(product);
        }
        return result;
    }

    @Transient
    @JsonProperty("order_status")  
    public MapOrderUserStatus getOrderStatus(){
        boolean isProcessing = false;
        boolean isShipped = false;
        boolean isCompleted = false;

        List<String> ORDER_PROCESSED = Arrays.asList(ORDER_STATUS_VERIFY, ORDER_STATUS_PICKING, ORDER_STATUS_PACKING);
        List<String> ORDER_SHIPPED = Arrays.asList(ORDER_STATUS_ON_DELIVERY,ORDER_STATUS_CUSTOMER_NOT_AT_THE_ADDRESS_STATE);
        List<String> ORDER_COMPLETED = Arrays.asList(ORDER_STATUS_RECEIVE_BY_CUSTOMER,ORDER_STATUS_RETURN,ORDER_STATUS_REPLACED);
        List<String> ORDER_CANCEL = Arrays.asList(ORDER_STATUS_CANCEL,ORDER_STATUS_CANCEL_BY_CUSTOMER_SERVICE);
        if(ORDER_PROCESSED.contains(status)){
            isProcessing = true;
        }
        if(ORDER_SHIPPED.contains(status)){
            isProcessing = true;
            isShipped = true;
        }
        if(ORDER_COMPLETED.contains(status)){
            isProcessing = true;
            isShipped = true;
            isCompleted = true;
        }

        MapOrderUserStatus orderStatus = new MapOrderUserStatus(isProcessing, isShipped, isCompleted, null, null, null);
        return orderStatus;
    }

    public static Page<SalesOrder> page(int page, int pageSize, String sortBy, String order, String name, String filter) {
        ExpressionList<SalesOrder> qry = SalesOrder.find
                .where()
                .ilike("orderNumber", "%" + name + "%")
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

    public String getTotalFormat(){
        return CommonFunction.numberFormat(subtotal);
    }

    public String getSubTotalFormat(){
        return CommonFunction.numberFormat(totalPrice);
    }

    public String getDiscountFormat(){
        return CommonFunction.numberFormat(discount);
    }

    public String getVoucherFormat(){
        return CommonFunction.numberFormat(voucher);
    }

    public String getShippingFormat(){
        return CommonFunction.numberFormat(shipping);
    }

    public String getTanggal(){
        return CommonFunction.getDate(orderDate);
    }

    public String getApprovedDate(){
        return CommonFunction.getDate(approvedDate);
    }

    public String getApprovedBy(){
        return approvedBy != null ? approvedBy.fullName : "";
    }

    public String getShippingAddress(){
        if (shipmentAddress != null){
            return "" +
                    shipmentAddress.name+"\n" +
                    shipmentAddress.address+"\n" +
                    shipmentAddress.getCity()+ " " + shipmentAddress.getProvince()+ " \n" +
                    shipmentAddress.postalCode+"\n" +
                    shipmentAddress.phone+"\n" +
                    "";
        }
        return "";
    }

    public String getPaymentAddress(){
        if (billingAddress != null){
            return "" +
                    billingAddress.name+"\n" +
                    billingAddress.address+"\n" +
                    billingAddress.getCity()+ " " + billingAddress.getProvince()+ " \n" +
                    billingAddress.postalCode+"\n" +
                    billingAddress.phone+"\n" +
                    "";
        }

        return "";
    }

    public static String generateSOCode(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMM");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM");
        SalesOrder so = SalesOrder.find.where("t0.created_at > '"+simpleDateFormat2.format(new Date())+"-01 00:00:00'")
                .order("t0.created_at desc").setMaxRows(1).findUnique();
        String seqNum = "";
        if(so == null){
            seqNum = "00001";
        }else{
            seqNum = so.orderNumber.substring(so.orderNumber.length() - 5);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "00000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 5);
        }
        String code = "HSO";
        code += simpleDateFormat.format(new Date()) + seqNum;
        return code;
    }

    //odoo
    public static Long fromRequest(Member member, MapOrder map){ //TODO create new order
        SalesOrder model = new SalesOrder();
        model.orderDate = new Date();
        model.orderNumber = generateSOCode();
        model.voucher = model.totalPrice = model.subtotal = model.discount = 0D;
        model.member = member;
        model.shipmentAddress = Address.find.byId(map.getShippingAddress());
        model.billingAddress = Address.find.byId(map.getBillingAddress());
//        model.bank = Bank.find.byId(map.getBankId());
//        model.status = ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION;
        model.status = ORDER_STATUS_CHECKOUT;
        model.expiredDate = PaymentExpiration.getExpired();
        model.struct = "";
        model.shipmentType = "";
        model.emailNotif = map.getOrderNotes();
        model.checkoutType = 0L;
        model.deviceType = map.getDeviceType();
//        CourierPointLocation cpl = null;
//        if (map.getPickupPoint() != null){
//            cpl = CourierPointLocation.find.byId(map.getPickupPoint());
//        }
//        if (cpl != null){
//            model.courierPointLocation = cpl;
//        }
        model.save();

        Double subTotal = 0D;
        Double discount = 0D;
        Double priceTotal = 0D;
        Double shipping = 0D;
        List<Long> salesOrderSellers = new ArrayList<>();
        for (MapOrderSeller mos : map.getSellers()){ //TODO per merchant
            SalesOrderSeller sos = new SalesOrderSeller();
            sos.orderDate = model.orderDate;
            Merchant merchant = null;
            Vendor vendor = null;
            boolean flagOwnMerchant = false;
            if (mos.getMerchantId() != null){
                merchant = Merchant.find.byId(mos.getMerchantId());
                flagOwnMerchant = merchant.ownMerchant;
                sos.merchant = merchant;
                if (sos.merchant.id == -1L){
                    sos.orderNumber = "M0-" +model.orderNumber ;
                }else{
                    sos.orderNumber = "M"+sos.merchant.id + "-" +model.orderNumber ;
                }
            }else{
                vendor = Vendor.find.byId(mos.getVendorId());
                sos.vendor = vendor;
                sos.orderNumber = "V"+sos.vendor.id + "-" +model.orderNumber ;
            }
//            ShippingCostDetail cs;
//            Courier c;
//            if (map.getPaymentMethod().equals("COD")){
//                c = Courier.find.where().eq("name", "COD").setMaxRows(1).findUnique();
//                cs = ShippingCostDetail.find.where().eq("service", c.services.get(0)).setMaxRows(1).findUnique();
//            }else{
//                cs = ShippingCostDetail.find.byId(mos.getCourierServiceId());
//                cs.getServiceName();
//                c = cs.service.courier;
//            }
            sos.salesOrder = model;
//            sos.courier = c;
//            sos.shippingCostDetail = cs;
            sos.member = model.member;
            sos.status = model.status;
            sos.shipmentAddress = model.shipmentAddress;
//            if (cpl != null){
//                sos.courierPointLocation = cpl;
//            }
            
            Date currentDate = new Date();
            
            if(mos.getShipmentType() == 0L) { // check whether user chose normal shipping or picking up themselves
            	//using normal shipping
            	sos.shipmentType = 0L;
            	
            	Double courierValue = mos.getCourier().value;
                String[] etdSplit = mos.getCourier().etd.split("-");
                String etdTimeTarget = etdSplit[etdSplit.length-1].replace("HARI", "").trim().replace("+", "");
                Integer courierEstTime = StringUtils.isNumeric(etdTimeTarget) ? Integer.parseInt(etdTimeTarget) : 10 ;
                
                sos.courierCode = mos.getCourier().courierCode.trim();
                sos.courierName = mos.getCourier().courier.trim();
                sos.courierServiceCode = mos.getCourier().serviceCode.trim();
                sos.courierServiceName = mos.getCourier().service.trim();
                
                sos.shipping = courierValue == null || courierValue < 0 ? 0D : courierValue;
                
                sos.discount = 0D;
                sos.subtotal = sos.shipping;
                sos.voucher = sos.totalPrice = 0D;
                
                
                sos.sentDate = currentDate;
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                cal.add(Calendar.DATE, courierEstTime);
                sos.deliveredDate = cal.getTime();
            }
            else if(mos.getShipmentType() == 1L){
            	// using pick up point
            	sos.shipmentType = 1L;
            	
            	sos.pickUpPointName = mos.getPickuppoint().name;
            	sos.pickUpPointAddress = mos.getPickuppoint().address;
            	sos.pickUpPointContact = mos.getPickuppoint().contact;
            	sos.pickUpPointDuration = mos.getPickuppoint().duration;
            	sos.pickUpPointLatitude = mos.getPickuppoint().latitude;
            	sos.pickUpPointLongitude = mos.getPickuppoint().longitude;
            	
            	sos.shipping = 0D;
            	sos.discount = 0D;
            	sos.subtotal = sos.shipping;
            	sos.voucher = sos.totalPrice = 0D;

            	  
            }

            sos.paymentStatus = SalesOrderSeller.UNPAID_CUSTOMER;
            
            sos.save();

            Double subTotal2 = 0D;
            Double discount2 = 0D;
            Double priceTotal2 = 0D;
            Double weights = 0D;
            Double volumes = 0D;
            Double totalPayments = 0D;
            Map<Product, Integer> items = new HashMap<>();
            //set bag status to "checkout" ================================================================
//            Bag.bagCheckOut(mos.getItems(),member);
            for (MapOrderDetail mod : mos.getItems()){ //TODO per items
                SalesOrderDetail detail = new SalesOrderDetail();
                Product product = Product.find.byId(mod.getProductId());
                ProductDetailVariance productVariance = ProductDetailVariance.find.byId(mod.getProductVariantId());
                detail.product = product;
                detail.productVar = productVariance;
                detail.salesOrder = model;
                detail.salesOrderSeller = sos;
                if (mod.getSizeId() != null){
                    Size size = Size.find.byId(mod.getSizeId());
                    detail.sizeName = size.international;
                    detail.fashionSize = size;
                }
                Double discountPersen = 0D;
                Double discountAmount = 0D;
                detail.quantity = mod.getQuantity();
                ProductPrice productPrice = ProductPrice.find.where()
                		.eq("product_id", product.id)
                		.eq("is_active", true)
                		.le("start_date", currentDate)
                		.ge("end_date", currentDate)
                		.setMaxRows(1).findUnique();
                if (productPrice != null){
                	discountAmount =  (product.price - product.getPriceDisplay()) * detail.quantity;
                }
                else if (product.getDiscountActive()) {
	                switch (product.discountType){
	                    case 1 :
	                        discountAmount =  product.discount * detail.quantity;
	                        break;
	                    case 2 :
	                        discountPersen = product.discount;
	                        discountAmount = Math.floor((product.discount/100*product.price)) * detail.quantity;
	                        break;
	                }
                }
                detail.discountPersen = discountPersen;
                detail.discountAmount = discountAmount;
                detail.productName = product.name;
                detail.status = sos.status;
                detail.price = product.price;
                detail.priceDiscount = product.getPriceDisplay();
                detail.totalPrice = detail.quantity * detail.price;
                detail.voucher = detail.tax = 0D;
                detail.taxPrice = detail.totalPrice * (detail.tax/100);
                detail.subTotal = detail.totalPrice + detail.taxPrice - detail.discountAmount;
//                detail.paymentSeller = product.price * detail.quantity; //TODO buat bagi hasil?
                detail.paymentSeller = (flagOwnMerchant || product.buyPrice == null) ? 0D : (product.buyPrice * detail.quantity);


                subTotal += detail.subTotal;
                discount += discountAmount;
                priceTotal += detail.totalPrice;

                subTotal2 += detail.subTotal;
                discount2 += discountAmount;
                priceTotal2 += detail.totalPrice;
                detail.save();

                weights += product.getWeight();
                volumes += product.getVolumes();
                totalPayments += detail.paymentSeller;

                items.put(product, mod.getQuantity());

                productVariance.totalStock = productVariance.totalStock - detail.quantity; //TODO removing product stock
                productVariance.update();
                product.numOfOrder = product.numOfOrder == null ? 1 : (product.numOfOrder + 1); //TODO add order count to product for popularity
                product.update();
            }

            //TODO count shipping cost per seller
            sos.discount = discount2;
//            if (c.deliveryType.equals(Courier.DELIVERY_TYPE_PICK_UP_POINT)){
//                sos.shipping = ShippingUtil.calculateCost(items, sos.merchant.courierPointLocation, cpl);
//            }else{
//                sos.shipping = cs.calculateCost(weights, volumes);
//            }
//            sos.subtotal = subTotal2 + sos.shipping;
            sos.subtotal = subTotal2 + sos.shipping;
            sos.totalPrice = priceTotal2;
            sos.weights = weights;
            sos.volumes = volumes;
            sos.paymentSeller = flagOwnMerchant ? 0D : (totalPayments + sos.shipping);
            sos.update();
            shipping += sos.shipping;

            salesOrderSellers.add(sos.id);

        }

        model.shipping = shipping;
        model.totalPrice = priceTotal;
        model.discount = discount;
        model.subtotal = subTotal + shipping;
        model.paymentType = map.getPaymentMethod();
        model.update();
        
        HashMap<Integer, List<Voucher>> mapVoucher = new HashMap<>(); //TODO count voucher
        HashMap<Integer, List<VoucherDetail>> mapVoucherDetail = new HashMap<>();
        List<Long> voucherSet = new ArrayList<>();
        if (map.getVouchers() != null){
            for (MapVoucherCode code : map.getVouchers()){
                VoucherDetail voucherDetail = VoucherDetail.findByCode(code.getVoucherCode());
                if (voucherDetail != null){
                    Voucher voucher = voucherDetail.voucher;
                    if(voucher != null && !voucherSet.contains(voucher.id)){
                        List<Voucher> list = mapVoucher.get(voucher.priority);
                        if (list == null){
                            list = new LinkedList<>();
                        }
                        list.add(voucher);
                        mapVoucher.put(voucher.priority, list);

                        List<VoucherDetail> list2 = mapVoucherDetail.get(voucher.priority);
                        if (list2 == null){
                            list2 = new LinkedList<>();
                        }
                        list2.add(voucherDetail);
                        mapVoucherDetail.put(voucher.priority, list2);

                        voucherSet.add(voucher.id);
                    }
                }
            }
        }

        if(mapVoucher.size() > 0){
            int maxPriorityAllow = getMaxPriorityAllow(mapVoucher);
            SalesOrder so = SalesOrder.find.byId(model.id);
            for(int i=1; i<=maxPriorityAllow; i++){
                if(mapVoucher.containsKey(i)){
                    List<Voucher> vouchers = mapVoucher.get(i);
                    List<VoucherDetail> voucherDetails = mapVoucherDetail.get(i);
                    for (int j =0; j< vouchers.size(); j++){
                        entryVoucher(so, vouchers.get(j), voucherDetails.get(j), member);
                    }
                }
            }
            recalculateTotalSO(model.id);
        }
        
        checkLoyalty(model.id,map);

        for (Long id : salesOrderSellers){
            SalesOrderSeller sos = SalesOrderSeller.find.byId(id);

            SalesOrderSellerStatus sosStatus = new SalesOrderSellerStatus(sos, new Date(), 1, "Your orders has been created.");
            sosStatus.save();

            Merchant merchant = sos.merchant;
            if (merchant != null && !merchant.isHokeba()){
                merchant.unpaidCustomer = merchant.getUnpaidCustomer() + sos.paymentSeller;
                merchant.update();
            }
        }

        if (map.getPaymentMethod().equals("COD")){
            SalesOrderPayment payment = new SalesOrderPayment();
            payment.salesOrder = model;
            payment.invoiceNo = SalesOrderPayment.generateInvoiceCode();
            payment.confirmAt = new Date();
            payment.debitAccountName = "--";
            payment.debitAccountNumber = "--";
            payment.totalTransfer = model.subtotal;
            payment.imageUrl = "";
            payment.status = SalesOrderPayment.COD_VERIFY;
            payment.comments = "";
            payment.save();
        }

        return model.id;
    }

    private static void recalculateTotalSO(Long id){
        SalesOrder so = SalesOrder.find.byId(id);
        Double voucher = 0D;
        for (SalesOrderSeller sos : so.salesOrderSellers){
            Double voucherSeller = 0D;
            for (SalesOrderDetail sod : sos.salesOrderDetail){
                if (sod.voucher > 0D){
                    sod.subTotal -= sod.voucher;
                    sod.update();

                    voucherSeller += sod.voucher;
                }
            }

            if (voucherSeller > 0D){
                sos.voucher += voucherSeller;
                sos.subtotal -= voucherSeller;
                sos.update();
            }

            voucher += sos.voucher;
        }
        if (voucher > 0D){
            so.voucher = voucher;
            so.subtotal -= voucher;
            so.update();
        }
    }
    
    private static void checkLoyalty(Long id, MapOrder map) {
    	SalesOrder model = SalesOrder.find.byId(id);
    	long userPoint = 0;
    	
    	//customer choose to use points
    	if (!map.getLoyalty().equals(0L)) {
        	userPoint = LoyaltyPoint.countPoint(model.member.id);
        	double eligible = 0;
        	
        	for(SalesOrderSeller sos : model.salesOrderSellers) {
        		for(SalesOrderDetail sod : sos.salesOrderDetail){
        			Logger.info("available use: " + sod.product.getEligiblePointUsed());
//        			if(sod.product.getEligiblePointUsed()!=0) {
        			eligible += (sod.product.getEligiblePointUsed()*sod.quantity)-sod.voucher; // maximum loyaltypoint used per item is price - 10000
//        			Logger.info("available use - 10000: " + (sod.product.getEligiblePointUsed()));
//        			}
        		}
        	}

        	if(userPoint >= eligible) {
        		userPoint = (long)eligible;
        	}
        	
        	try {
            	LoyaltyPoint loyaltyPoint = LoyaltyPoint.reducePoint(model.member.id, userPoint, model.id);
        		if (loyaltyPoint != null) {
        			model.loyaltyPoint.add(loyaltyPoint);
        		}
        		model.subtotal -= userPoint;
        		model.update();
			} catch (Exception e) {
				// TODO: handle exception
        		e.printStackTrace();
			}
    	}
		boolean checkAllocate = LoyaltyController.allocateEligiblePoint(model.id,userPoint);//allocate loyalty per sod
    }

    
    

    private static void entryVoucher(SalesOrder so, Voucher voucher, VoucherDetail voucherDetail, Member member){
        if(voucher.minPurchase <= so.subtotal && (Voucher.ASSIGNED_TO_ALL.equals(voucher.assignedTo)
            || (Voucher.ASSIGNED_TO_CUSTOM.equals(voucher.assignedTo) && voucher.members.contains(member))) &&
    		//validasi source type
    		((so.deviceType.equals("WEB") && voucherDetail.voucher.deviceSource % Voucher.DEVICE_SOURCE_WEB == 0) || 
			(so.deviceType.equals("IOS") || so.deviceType.equals("ANDROID") && voucherDetail.voucher.deviceSource % Voucher.DEVICE_SOURCE_APPS == 0))) {
            if (Voucher.TYPE_FREE_DELIVERY.equals(voucher.type)) {
            	Double totalShipping = 0D, usedVoucher = 0D;
            	int idx = 0;
            	//get total shipping
            	for (SalesOrderSeller sos : so.salesOrderSellers) {
					totalShipping += sos.shipping;
				}
                for (SalesOrderSeller sos : so.salesOrderSellers){
                	idx++;
                    Merchant merchant = sos.merchant;
                    if (merchant != null){
                        if (Voucher.isSellerInVoucher(merchant, voucher)){
                        	if (voucher.maxValue == 0D || voucher.maxValue >= totalShipping)
                        		sos.voucher = sos.shipping;
                        	else if (voucher.maxValue < totalShipping) {
                        		//last data
                        		if (idx == so.salesOrderSellers.size())
                        			sos.voucher = voucher.maxValue - usedVoucher;
                        		else
                        			sos.voucher = Double.valueOf(Math.round(sos.shipping /totalShipping * voucher.maxValue));
                        		usedVoucher += sos.voucher;
                        	}
                            sos.subtotal -= sos.voucher;
                            sos.voucherId = voucherDetail.id;
                            sos.update();

                            voucherDetail.status = 1;
                            voucherDetail.member = member;
                            voucherDetail.orderNumber = so.orderNumber; //TODO
                            voucherDetail.usedAt = new Date();
                            voucherDetail.update();
                        }
                    }else{
                        if (Voucher.FILTER_STATUS_ALL.equals(voucher.filterStatus)){
                            ShippingCostDetail cs = sos.shippingCostDetail;
                            sos.voucher = cs.calculateCost(sos.weights, sos.volumes);
                            sos.subtotal -= sos.voucher;
                            sos.voucherId = voucherDetail.id;
                            sos.update();

                            voucherDetail.status = 1;
                            voucherDetail.member = member;
                            voucherDetail.usedAt = new Date();
                            voucherDetail.update();
                        }
                    }
                }

            }else{

                Double totalPrice = 0D, usedVoucher = 0D;
                for (SalesOrderSeller sos : so.salesOrderSellers){
                    for (SalesOrderDetail sod : sos.salesOrderDetail){
                        Product product = sod.product;
                        if(Voucher.isProductInVoucher(product, voucher)){
                            List<VoucherDetail> vouchers = sod.voucherDetails;
                            vouchers.add(voucherDetail);
                            sod.voucherDetails = vouchers;
                            sod.update();

                            totalPrice += sod.subTotal;
                        }
                    }
                }

                Double discount = voucher.discount;
                if (voucher.discountType == Voucher.DISCOUNT_TYPE_PERCENT) {
                    discount = (voucher.discount / 100) * totalPrice;
                    if (discount > voucher.maxValue) {
                        discount = voucher.maxValue;
                    }
                }

                int idx = 0, idy = 0;
                for (SalesOrderSeller sos : so.salesOrderSellers){
                	idx++;
                	idy = 0;
                    for (SalesOrderDetail sod : sos.salesOrderDetail){
                    	idy++;
                        List<VoucherDetail> vouchers = sod.voucherDetails;
                        if(vouchers.contains(voucherDetail)){
                            Double voucherPercent = (sod.subTotal / totalPrice) * 100;
                            Double value = 0D;
                            //last data
                            if (idx == so.salesOrderSellers.size() && idy == sos.salesOrderDetail.size())
                            	value = discount - usedVoucher;
                            else
                            	value = (double) Math.round((voucherPercent / 100) * discount);
                            usedVoucher += value;
                            sod.voucher += value;
                            sod.update();

                            voucherDetail.status = 1;
                            voucherDetail.member = member;
                            voucherDetail.usedAt = new Date();
                            voucherDetail.orderNumber = so.orderNumber; //TODO
                            voucherDetail.update();
                        }
                    }
                }
            }
        }

    }

    public static void seed(String orderNumber, String status, List<Long> productId){
        SalesOrder model = new SalesOrder();
        model.orderDate = new Date();
        model.orderNumber = orderNumber;
        model.discount = 0D;
        model.subtotal = 0D;
        model.totalPrice = 0D;
        model.member = Member.find.byId(1L);
        model.shipmentAddress = Address.find.byId(1L);
        model.courier = Courier.find.byId(1L);
        model.billingAddress = Address.find.byId(3L);
        model.bank = Bank.find.byId(1L);
        model.status = status;
        model.expiredDate = new Date();
        model.struct = "";
        model.shipmentType = "";
        model.paymentType = PAYMENT_METHOD_TRANSFER;
        model.emailNotif = "";
        if(status.equals(ORDER_STATUS_VERIFY)){
            model.approvedDate = new Date();
            model.approvedBy = UserCms.find.byId(1L);
        }
        model.save();
        Double total = 0D;
        Set<Merchant> merchant = new HashSet<>();
        Set<Vendor> vendor = new HashSet<>();
        Map<Long, Double> totalByMerchant = new HashMap<>();
        Map<Long, Double> totalByVendor = new HashMap<>();
        for(Long id : productId){
            SalesOrderDetail detail = new SalesOrderDetail();
            detail.product = Product.find.byId(id);
            detail.salesOrder = model;
            detail.discountPersen = 0D;
            detail.discountAmount = 0D;
            detail.quantity = 1;
            detail.status = model.status;
            detail.productName = detail.product.name;
            detail.price = detail.product.priceDisplay;
            detail.totalPrice = detail.quantity * detail.price;
            detail.tax = 0D;
            detail.taxPrice = detail.totalPrice * (detail.tax/100);
            detail.subTotal = detail.totalPrice + detail.taxPrice - detail.discountAmount - (detail.discountPersen/100*detail.totalPrice);
            if (detail.product.productType != 3){
                detail.vendor = detail.product.vendor;
                vendor.add(detail.vendor);
                if (totalByVendor.containsKey(detail.vendor.id)){
                    totalByVendor.put(detail.vendor.id, totalByVendor.get(detail.vendor.id) + detail.subTotal);
                }else{
                    totalByVendor.put(detail.vendor.id, detail.subTotal);
                }
            }else{
                detail.merchant = detail.product.merchant;
                merchant.add(detail.merchant);
                if (totalByMerchant.containsKey(detail.merchant.id)){
                    totalByMerchant.put(detail.merchant.id, totalByMerchant.get(detail.merchant.id) + detail.subTotal);
                }else{
                    totalByMerchant.put(detail.merchant.id, detail.subTotal);
                }
            }
            detail.save();
            total += detail.subTotal;
        }

        model.subtotal = total;
        model.totalPrice = model.subtotal - model.discount;
        model.update();

        if(status.equals(ORDER_STATUS_VERIFY)){
            SalesOrderPayment payment = new SalesOrderPayment();
            payment.salesOrder = model;
            payment.invoiceNo = orderNumber.replace("SO","INV");
            payment.confirmAt = new Date();
            payment.debitAccountName = "Waluyo";
            payment.debitAccountNumber = "310123681523";
            payment.totalTransfer = model.totalPrice;
            payment.status = SalesOrderPayment.VERIFY;
            payment.status = SalesOrderPayment.PAYMENT_VERIFY;
            payment.save();
        }

        Double shipping = 0D;
        Map<Long, SalesOrderSeller> som = new HashMap<>();
        for (Merchant m : merchant) {
            SalesOrderSeller sos = new SalesOrderSeller();
            sos.orderDate = model.orderDate;
            sos.orderNumber = model.orderNumber +"/"+m.getCode();
            sos.salesOrder = model;
            sos.courier = model.courier;
            sos.shippingCostDetail = ShippingCostDetail.find.byId(1L);
            sos.member = model.member;
            sos.status = model.status;
            sos.shipmentAddress = model.shipmentAddress;
            sos.discount = 0D;
            sos.subtotal = totalByMerchant.get(m.id);
            sos.merchant = m;
            sos.shipping = sos.shippingCostDetail.cost;
            sos.totalPrice = sos.subtotal + sos.shipping;
            sos.deliveredDate = sos.shippingCostDetail.getDelivered();
            sos.sentDate = new Date();

            sos.save();
            som.put(m.id, sos);
            SalesOrderSellerStatus sosStatus1 = new SalesOrderSellerStatus(sos, new Date(), 1, "Orders have been received and the verification process");
            sosStatus1.save();
            SalesOrderSellerStatus sosStatus2 = new SalesOrderSellerStatus(sos, new Date(), 3, "Your payment has been verified");
            sosStatus2.save();
            shipping += sos.shipping;
        }

        Map<Long, SalesOrderSeller> sov = new HashMap<>();
        for (Vendor m : vendor) {
            SalesOrderSeller sos = new SalesOrderSeller();
            sos.orderDate = model.orderDate;
            sos.orderNumber = model.orderNumber +"/"+m.code;
            sos.salesOrder = model;
            sos.courier = model.courier;
            sos.shippingCostDetail = ShippingCostDetail.find.byId(2L);
            sos.member = model.member;
            sos.status = model.status;
            sos.shipmentAddress = model.shipmentAddress;
            sos.discount = 0D;
            sos.subtotal = totalByVendor.get(m.id);
            sos.vendor = m;
            sos.shipping = sos.shippingCostDetail.cost;
            sos.totalPrice = sos.subtotal + sos.shipping;
            sos.deliveredDate = sos.shippingCostDetail.getDelivered();
            sos.sentDate = new Date();

            sos.save();
            sov.put(m.id, sos);
            SalesOrderSellerStatus sosStatus1 = new SalesOrderSellerStatus(sos, new Date(), 1, "Orders have been received and the verification process");
            sosStatus1.save();
            SalesOrderSellerStatus sosStatus2 = new SalesOrderSellerStatus(sos, new Date(), 3, "Your payment has been verified");
            sosStatus2.save();
            shipping += sos.shipping;
        }

        SalesOrder so = SalesOrder.find.byId(model.id);
        for (SalesOrderDetail sod : so.salesOrderDetail){
            if (sod.merchant != null){
                sod.salesOrderSeller = som.get(sod.merchant.id);
            }else{
                sod.salesOrderSeller = sov.get(sod.vendor.id);
            }
            sod.update();
        }

        model.shipping = shipping;
        model.subtotal = model.subtotal + shipping;
        model.update();
    }

    public static List<MapVoucherCode> countVoucherV2(Member member, MapOrder map){ //TODO count voucher
        MapOrderUserList result = new MapOrderUserList();
        List<com.hokeba.mapping.response.MapOrderSeller> sellers = new ArrayList<>();
        Double subTotal = 0D;
        Double shipping = 0D;
        CourierPointLocation cpl = CourierPointLocation.find.where().eq("id", map.getPickupPoint()).setMaxRows(1).findUnique();

        for (MapOrderSeller mos : map.getSellers()){
            com.hokeba.mapping.response.MapOrderSeller sos = new com.hokeba.mapping.response.MapOrderSeller();
            sos.items = new ArrayList<>();
            sos.setCourierServiceId(mos.getCourierServiceId());
            ShippingCostDetail cs = ShippingCostDetail.find.byId(mos.getCourierServiceId());
            cs.getServiceName();
            Courier c = cs.service.courier;

            Double subTotal2 = 0D;
            Double weights = 0D;
            Double volumes = 0D;
            Map<Product, Integer> items = new HashMap<>();
            for (MapOrderDetail mod : mos.getItems()){
                Double subTotal3;
                Double priceTotal3;

                Product prod = Product.find.byId(mod.getProductId());
                MapOrderSellerProduct mp = new MapOrderSellerProduct();
                mp.productId = prod.id;
                mp.quantity = mod.getQuantity();
                mp.price = prod.price;

                Double discountPersen = 0D;
                Double discountAmount = 0D;
                if (prod.getDiscountActive()) {
	                switch (prod.discountType){
	                    case 1 : discountAmount =  prod.discount * mod.getQuantity(); break;
	                    case 2 :
	                        discountPersen = prod.discount;
	                        discountAmount = Math.floor(prod.discount/100*prod.price) * mod.getQuantity();
	                        break;
	                }
                }
                mp.discountPersen = discountPersen;
                mp.discountAmount = discountAmount;
                priceTotal3 = mp.quantity * mp.price;
                subTotal3 = priceTotal3 - mp.discountAmount;

                subTotal2 += subTotal3;

                mp.voucherAmount = 0D;
                mp.vouchers = new ArrayList<>();
                sos.items.add(mp);

                weights += prod.getWeight();
                volumes += prod.getVolumes();

                items.put(prod, mod.getQuantity());
            }

            Merchant merchant = Merchant.find.byId(mos.getMerchantId());
            sos.sellerName = merchant.name;

            if (c.deliveryType.equals(Courier.DELIVERY_TYPE_PICK_UP_POINT)){
                sos.shipping = ShippingUtil.calculateCost(items, merchant.courierPointLocation, cpl);
            }else{
                sos.shipping = cs.calculateCost(weights, volumes);
            }

            shipping += sos.shipping;
            subTotal += subTotal2;
            sos.setMerchantId(mos.getMerchantId());
            sos.setVendorId(mos.getVendorId());
            sos.courierName = c.name;
            sos.weights = weights;
            sos.volumes = volumes;
            sellers.add(sos);
        }
        result.subTotal = subTotal;
        result.shipping = shipping;
        result.total = subTotal + shipping;
        result.sellers = sellers;

        HashMap<Integer, List<Voucher>> mapVoucher = new HashMap<>();
        HashMap<Integer, List<MapVoucherCode>> mapVoucherCode = new HashMap<>();
        Map<String, Map<Double, String>> resultVoucher = new HashMap<>();
        List<Long> voucherSet = new ArrayList<>();
        for (MapVoucherCode code : map.getVouchers()){
            VoucherDetail voucherDetail = VoucherDetail.findByCode(code.getVoucherCode());
            Map<Double, String> notes = new HashMap<>();
            if (voucherDetail != null){
                notes.put(0D, "");
                Voucher voucher = voucherDetail.voucher;
                if(voucher != null && !voucherSet.contains(voucher.id)){
                    List<Voucher> list = mapVoucher.get(voucher.priority);
                    if (list == null){
                        list = new LinkedList<>();
                    }
                    list.add(voucher);
                    mapVoucher.put(voucher.priority, list);

                    code.setVoucherId(voucher.id);
                    List<MapVoucherCode> list2 = mapVoucherCode.get(voucher.priority);
                    if (list2 == null){
                        list2 = new LinkedList<>();
                    }
                    list2.add(code);
                    mapVoucherCode.put(voucher.priority, list2);

                    voucherSet.add(voucher.id);
                }
            }else{
                notes.put(0D, "Voucher not found");
            }

            resultVoucher.put(code.getVoucherCode(), notes);
        }

        if(mapVoucher.size() > 0){
            int maxPriorityAllow = getMaxPriorityAllow(mapVoucher);
            int i = 1;
            while (i<=maxPriorityAllow){
                if(mapVoucher.containsKey(i)){
                    List<Voucher> vouchers = mapVoucher.get(i);
                    List<MapVoucherCode> mapVoucherCodes = mapVoucherCode.get(i);
                    for (int j =0; j< vouchers.size(); j++){
                        MapVoucherCode mapVoucher2 = mapVoucherCodes.get(j);
                        Map<Double, String> notes = new HashMap<>();
                        notes.put(entryVoucherV2(result, vouchers.get(j), mapVoucher2, member), "");
                        resultVoucher.put(mapVoucher2.getVoucherCode(), notes);
                    }
                }
                i++;
            }
            int maxPriority = getMaxPriority(mapVoucher);
            while (i<=maxPriority){
                if(mapVoucher.containsKey(i)){
                    Map<Double, String> notes = new HashMap<>();
                    notes.put(0D, "Voucher can't be combined");
                    List<MapVoucherCode> mapVoucherCodes = mapVoucherCode.get(i);
                    List<Voucher> vouchers = mapVoucher.get(i);
                    for (int j =0; j< vouchers.size(); j++){
                        MapVoucherCode mapVoucher2 = mapVoucherCodes.get(j);
                        resultVoucher.put(mapVoucher2.getVoucherCode(), notes);
                    }
                }
                i++;
            }
        }

        List<MapVoucherCode> results = new ArrayList<>();
        for(Map.Entry<String, Map<Double, String>> entry : resultVoucher.entrySet()){
            for(Map.Entry<Double, String> note : entry.getValue().entrySet()){
                results.add(new MapVoucherCode(entry.getKey(), note.getKey(), note.getValue()));
            }
        }

        return results;
    }
    
    // modification from countVoucherV2
 	// calculate voucher amount from current transaction / sales order
 	public static List<MapVoucherCode> calculateVoucher(Member member, MapOrder map, String deviceType) {
 		MapOrderUserList result = new MapOrderUserList();
 		int deviceSource = 0;
 		List<com.hokeba.mapping.response.MapOrderSeller> sellers = new ArrayList<>();
 		Double subTotal = 0D;
 		Double shipping = 0D;

 		HashMap<Integer, List<Voucher>> mapVoucher = new HashMap<>();
 		HashMap<Integer, List<MapVoucherCode>> mapVoucherCode = new HashMap<>();
 		Map<String, Map<Double, String>> resultVoucher = new HashMap<>();
 		List<Long> voucherSet = new ArrayList<>();
 		String message = "";
 		Double voucherAmount = 0D;

 		// checking voucher
 		MapVoucherCode code = map.getVouchers().get(0);
 		if (code != null) {
 			VoucherDetail voucherDetail = VoucherDetail.findByCode(code.getVoucherCode().toUpperCase());
 			Map<Double, String> notes = new HashMap<>();
 			//validasi device source
 	 		if ((deviceType.equals("WEB") && voucherDetail.voucher.deviceSource % Voucher.DEVICE_SOURCE_WEB == 0) || 
 				((deviceType.equals("IOS") || deviceType.equals("ANDROID")) && voucherDetail.voucher.deviceSource % Voucher.DEVICE_SOURCE_APPS == 0)) {
 	 			if (voucherDetail != null) {
 	 				notes.put(0D, "");
 	 				Voucher voucher = voucherDetail.voucher;
 	 				if (voucher != null && !voucherSet.contains(voucher.id)) {
 	 					List<Voucher> list = mapVoucher.get(voucher.priority);
 	 					if (list == null) {
 	 						list = new LinkedList<>();
 	 					}
 	 					list.add(voucher);
 	 					mapVoucher.put(voucher.priority, list);

 	 					code.setVoucherId(voucher.id);
 	 					List<MapVoucherCode> list2 = mapVoucherCode.get(voucher.priority);
 	 					if (list2 == null) {
 	 						list2 = new LinkedList<>();
 	 					}
 	 					list2.add(code);
 	 					mapVoucherCode.put(voucher.priority, list2);
 	 					voucherSet.add(voucher.id);
 	 				}
 	 				message =  voucher.getType();
 	 			} else {
 	 				message =  "Voucher not found";
 	 				notes.put(0D, "Voucher not found");
 	 			}
 	 		}
 	 		else {
	 				message =  "Voucher not valid in this device";
	 				notes.put(0D, "Voucher not valid in this device");
 	 		}
 			resultVoucher.put(code.getVoucherCode(), notes);
 		}

 		// calculate n validate voucher amount
 		if (mapVoucher.size() > 0) {

 			// calculate order with default discount
 			// per merchant / seller
 			for (MapOrderSeller mos : map.getSellers()) {
 				com.hokeba.mapping.response.MapOrderSeller sos = new com.hokeba.mapping.response.MapOrderSeller();
 				sos.items = new ArrayList<>();

 				Double subTotal2 = 0D;
 				Map<Product, Integer> items = new HashMap<>();
 				for (MapOrderDetail mod : mos.getItems()) {
 					Double subTotal3;
 					Double priceTotal3;
 					Product prod = Product.find.byId(mod.getProductId());
 					MapOrderSellerProduct mp = new MapOrderSellerProduct();
 					mp.productId = prod.id;
 					mp.quantity = mod.getQuantity();
 					mp.price = prod.price;

 					Double discountPersen = 0D;
 					Double discountAmount = 0D;
 					if (prod.getDiscountActive()) {
	 					switch (prod.discountType) {
	 					case 1:
	 						discountAmount = prod.discount * mod.getQuantity();
	 						break;
	 					case 2:
	 						discountPersen = prod.discount;
	 						discountAmount = Math.floor(prod.discount / 100 * prod.price) * mod.getQuantity();
	 						break;
	 					}
 					}
 					mp.discountPersen = discountPersen;
 					mp.discountAmount = discountAmount;
 					priceTotal3 = mp.quantity * mp.price;
 					subTotal3 = priceTotal3 - mp.discountAmount;
 					subTotal2 += subTotal3;

 					mp.voucherAmount = 0D;
 					mp.vouchers = new ArrayList<>();
 					sos.items.add(mp);
 					items.put(prod, mod.getQuantity());
 				}

 				Merchant merchant = Merchant.find.byId(mos.getMerchantId());
 				sos.sellerName = merchant.name;
 				sos.shipping = mos.getShippingPrice();
 				shipping += 0D;
 				subTotal += subTotal2;
 				sos.setMerchantId(mos.getMerchantId());
 				sos.setVendorId(mos.getVendorId());
 				sellers.add(sos);
 			}
 			result.subTotal = subTotal;
 			result.shipping = shipping;
 			result.total = subTotal + shipping;
 			result.sellers = sellers;

 			// calculate n validate voucher amount
 			int maxPriorityAllow = getMaxPriorityAllow(mapVoucher);
 			int i = 1;
 			while (i <= maxPriorityAllow) {
 				if (mapVoucher.containsKey(i)) {
 					List<Voucher> vouchers = mapVoucher.get(i);
 					List<MapVoucherCode> mapVoucherCodes = mapVoucherCode.get(i);
 					for (int j = 0; j < vouchers.size(); j++) {
 						MapVoucherCode mapVoucher2 = mapVoucherCodes.get(j);
 						voucherAmount = entryVoucherV2(result, vouchers.get(j), mapVoucher2, member);
 					}
 				}
 				i++;
 			}
 		}

 		List<MapVoucherCode> results = new ArrayList<>();
 		results.add(new MapVoucherCode(code.getVoucherCode(), voucherAmount, message));

 		return results;
 	}

    private static int getMaxPriorityAllow(HashMap<Integer, List<Voucher>> mapVoucher){
        int max = 0;
        for(int i=1; i<=20; i++){
            if(mapVoucher.containsKey(i)){
                max = i;
                if(isStopFurtherProcess(mapVoucher.get(i))){
                    break;
                }
            }
        }

        return max;
    }

    private static int getMaxPriority(HashMap<Integer, List<Voucher>> mapVoucher){
        int max = 0;
        for(int i=1; i<=20; i++){
            if(mapVoucher.containsKey(i)){
                max = i;
            }
        }

        return max;
    }

    private static  boolean isStopFurtherProcess(List<Voucher> vouchers){
        boolean isStopFurtherProcess = false;
        for (Voucher voucher : vouchers){
            if (voucher.stopFurtherRulePorcessing == 1){
                isStopFurtherProcess = true;
                break;
            }
        }

        return isStopFurtherProcess;
    }

    private static Double entryVoucherV2(MapOrderUserList result, Voucher voucher, MapVoucherCode mapVoucher, Member member){
        Double voucherAmount = 0D, totalShipping = 0D;
        if(voucher.minPurchase <= result.subTotal && (Voucher.ASSIGNED_TO_ALL.equals(voucher.assignedTo) || (Voucher.ASSIGNED_TO_CUSTOM.equals(voucher.assignedTo) && voucher.members.contains(member)))) {
            if (Voucher.TYPE_FREE_DELIVERY.equals(voucher.type)) {
            	//calculate total shipping
                for(int i = 0; i < result.getSellers().size(); i++){
					totalShipping += result.sellers.get(i).shipping;
				}
                
                for(int i = 0; i < result.getSellers().size(); i++){
                    com.hokeba.mapping.response.MapOrderSeller sos = result.sellers.get(i);
                    if (sos.getMerchantId() != null){
                        Merchant merchant = Merchant.find.byId(sos.getMerchantId());
                        if (Voucher.isSellerInVoucher(merchant, voucher)){
                        	//disabled max value or shipping under max value
                        	if (voucher.maxValue == 0D || voucher.maxValue >= totalShipping)
                        		voucherAmount += sos.shipping;
                        	// shipping cost > voucher max value
                        	else if (voucher.maxValue < totalShipping) {
                        		if (i == result.getSellers().size() - 1)
                        			voucherAmount += (voucher.maxValue - voucherAmount);
                        		else
                            		voucherAmount += Double.valueOf(Math.round(sos.shipping /totalShipping * voucher.maxValue));
                        	}
                        }
                    }
                    else {
                        if (Voucher.FILTER_STATUS_ALL.equals(voucher.filterStatus)){
                        	//disabled max value or shipping under max value
                        	if (voucher.maxValue == 0D || voucher.maxValue >= totalShipping)
                        		voucherAmount += sos.shipping;
                        	// shipping cost > voucher max value
                        	else if (voucher.maxValue < totalShipping)
                        		voucherAmount += Double.valueOf(Math.round(sos.shipping /totalShipping * voucher.maxValue));
                        }
                    }
                }

            } else {

                Double totalPrice = 0D;
                for(int i = 0; i < result.getSellers().size(); i++){
                    for(int j = 0; j < result.sellers.get(i).items.size(); j++){
                        Product product = Product.find.byId(result.sellers.get(i).items.get(j).productId);
                        if(Voucher.isProductInVoucher(product, voucher)){
                            totalPrice +=  (result.sellers.get(i).items.get(j).price * result.sellers.get(i).items.get(j).quantity) - result.sellers.get(i).items.get(j).discountAmount;
                            result.sellers.get(i).items.get(j).vouchers.add(mapVoucher);
                        }

                    }
                }

                Double discount = voucher.discount;
                if (voucher.discountType == Voucher.DISCOUNT_TYPE_PERCENT) {
                    discount = (voucher.discount / 100) * totalPrice;
                    if (discount > voucher.maxValue) {
                        discount = voucher.maxValue;
                    }
                }

                for(int i = 0; i < result.getSellers().size(); i++){
                    for(int j = 0; j < result.sellers.get(i).items.size(); j++){
                        if(result.sellers.get(i).items.get(j).vouchers.contains(mapVoucher)){
                            Double voucherPercent = (((result.sellers.get(i).items.get(j).price * result.sellers.get(i).items.get(j).quantity) - result.sellers.get(i).items.get(j).discountAmount) / totalPrice) * 100;
                            //last data
                            if (i == result.getSellers().size() - 1 && j == result.sellers.get(i).items.size() - 1)
                            	voucherAmount += discount - voucherAmount;
                            else
                            	voucherAmount += (double) Math.round((voucherPercent / 100) * discount);
                        }
                    }
                }
            }
        }
        Logger.info(voucherAmount.toString());
        return voucherAmount;
    }

    public static List<SalesOrder> getOrderByMember(Long member){
        return SalesOrder.find.where()
                .eq("member_id", member)
                .eq("t0.is_deleted", false)
                .setOrderBy("t0.id DESC").setMaxRows(5).findList();
    }
    
    public static void triggerExpireScheduller(Long orderId) {
    	ExpireOrderScheduler scheduleExpire = new ExpireOrderScheduler("* * * * * ?",
				60, TimeUnit.MINUTES, orderId);
		scheduleExpire.scheduleOnceAfterInterval();
    }
    
    public static void revertItemStockFromOrder(Long orderId) {
    	//only use this method inside ebean transaction
    	SalesOrder orderData = SalesOrder.find.where()
    			.eq("t0.id", orderId).eq("t0.is_deleted", false)
    			.eq("t0.status", ORDER_STATUS_EXPIRE_PAYMENT).findUnique();
    	revertItemStockFromOrder(orderData);
    	
    }
    
    public static void revertItemStockFromOrder(SalesOrder orderData) {
    	//only use this method inside ebean transaction
    	if (orderData != null) {
    		for (SalesOrderSeller orderSeller : orderData.salesOrderSellers) {
				for (SalesOrderDetail detail : orderSeller.salesOrderDetail) {
					ProductDetailVariance itemTarget = detail.productVar;
					itemTarget.totalStock += detail.quantity;
					itemTarget.update();
					
					for (VoucherDetail voucherDetail : detail.voucherDetails) {
						voucherDetail.status = 0;
                        voucherDetail.orderNumber = "";
                        voucherDetail.member = null;
                        voucherDetail.usedAt = null;
                        voucherDetail.update();
					}
				}
				Merchant merchant = orderSeller.merchant;
				merchant.unpaidCustomer = merchant.getUnpaidCustomer() - orderSeller.paymentSeller;
                merchant.update();
			}
    	}
    }
    
    public static void revertPointExpiredPayment(SalesOrder orderData) {
    	if (orderData != null) {
    		if(!orderData.getLoyaltyPoint().equals(0L)) {
    			//    		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    			Date date = orderData.createdAt;
    			date.setYear(date.getYear()+1);
    			//    		String orderNextYear = dateFormat.format(date);
    			LoyaltyPoint.addPoint(orderData.member.id,
    					-1*orderData.getLoyaltyPoint(),
    					orderData.id,
    					date,
    					"Reverted points from order " + orderData.orderNumber);
    		}
    	}
    }       
    
    public static void revertItemStockWhenCheckoutOrder(Long memberId) {
//    	Transaction txn = Ebean.beginTransaction();
//    	try {
	    	List<SalesOrder> orderData = SalesOrder.find.where()
	    			.eq("t0.member_id", memberId).eq("t0.is_deleted", false)
	    			.eq("t0.status", ORDER_STATUS_CHECKOUT).findList();
	    	for (SalesOrder salesOrder : orderData) {
	    		revertItemStockFromOrder(salesOrder);
	    		
	    		salesOrder.isDeleted = true;
	    		salesOrder.update();
			}
//	    	txn.commit();
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    		txn.rollback();
//    	} finally {
//    		txn.end();
//    	}
    }
    
    public static ServiceResponse revertItemStockWhenRecreateOrder(Long memberId, Long orderId) {
    	ServiceResponse response = new ServiceResponse();
    	Transaction txn = Ebean.beginTransaction();
    	try {
    		if (orderId != null) {
		    	SalesOrder orderData = SalesOrder.find.where()
		    			.eq("t0.id", orderId)
		    			.eq("t0.member_id", memberId).eq("t0.is_deleted", false)
		    			.or(Expr.eq("t0.status", ORDER_STATUS_CHECKOUT), Expr.eq("t0.status", ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION))
		    			.setMaxRows(1).findUnique();
		    	if (orderData != null) {
		    		MidtransService service = MidtransService.getInstance();
		    		ServiceResponse wsResponse = service.expireTransaction(orderData.orderNumber);
		    		if (wsResponse.getCode() != 408) {
		    			TransactionStatus mappedResponse = new ObjectMapper().convertValue(wsResponse.getData(), TransactionStatus.class);
		    			
		    			if ("407".equals(mappedResponse.status_code)) {
				    		revertItemStockFromOrder(orderData);
				    		
				    		orderData.status = ORDER_STATUS_CHECKOUT;
				    		orderData.isDeleted = true;
				    		orderData.update();
					    	txn.commit();
					    	
					    	response.setCode(200);
					    	response.setData("Success");
					    	return response;
		    			} else {
		    				response.setCode(400);
					    	response.setData(mappedResponse.status_message);
					    	return response;
		    			}
		    		} else {
		    			response.setCode(400);
		        		response.setData("We're sorry something went wrong");
				    	return response;
		    		}
				}
    		}
    		response.setCode(404);
    		response.setData("The record was not found");
    	} catch (Exception e) {
    		e.printStackTrace();
    		txn.rollback();
    		response.setCode(400);
    		response.setData("We're sorry something went wrong");
    	} finally {
    		txn.end();
    	}
    	return response;
    }
    
    public static void updateMerchantPaymentData(SalesOrder orderData) {
    	if (orderData != null) {
    		for (SalesOrderSeller orderSeller : orderData.salesOrderSellers) {
    			Merchant merchant = orderSeller.merchant;
    			merchant.unpaidCustomer = merchant.getUnpaidCustomer() - orderSeller.paymentSeller;
				merchant.unpaidHokeba = merchant.getUnpaidHokeba() + orderSeller.paymentSeller;
                merchant.update();
    		}
    	}
    }
    
    public static void checkOrderComplete(SalesOrder orderData) {
    	if (orderData != null) {
    		boolean finishFlag = true;
    		for (SalesOrderSeller orderSeller : orderData.salesOrderSellers) {
    			if (!SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER.equals(orderSeller.status)) {
    				finishFlag = false;
    				break;
    			}
    		}
    		
    		if (finishFlag) {
    			SalesOrder.updateMerchantPaymentData(orderData);
    			orderData.status = SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER;
    			orderData.update();
    		}
    	}
    }


    public static Long fromRequestRedeem(Member member, MapOrderRedeem map){ //TODO create new order
        SalesOrder model = new SalesOrder();
        model.orderDate = new Date();
        model.orderNumber = generateSOCode();
        model.voucher = model.totalPrice = model.subtotal = model.discount = 0D;
        model.member = member;
        model.shipmentAddress = Address.find.byId(map.getShippingAddress());
        model.billingAddress = Address.find.byId(map.getBillingAddress());
        model.status = ORDER_STATUS_VERIFY;
        model.expiredDate = null;
        model.struct = "";
        model.shipmentType = "";
        model.emailNotif = "";
        model.checkoutType = 1L;
        
        model.save();

        Double subTotal = 0D;
        Double discount = 0D;
        Double priceTotal = 0D;
        Double shipping = 0D;
        List<Long> salesOrderSellers = new ArrayList<>();
        for (MapOrderSeller mos : map.getSellers()){ //TODO per merchant
            SalesOrderSeller sos = new SalesOrderSeller();
            sos.orderDate = model.orderDate;
            Merchant merchant = null;
            Vendor vendor = null;
            boolean flagOwnMerchant = false;
            if (mos.getMerchantId() != null){
                merchant = Merchant.find.byId(mos.getMerchantId());
                flagOwnMerchant = merchant.ownMerchant;
                sos.merchant = merchant;
                if (sos.merchant.id == -1L){
                    sos.orderNumber = "M0-" +model.orderNumber ;
                }else{
                    sos.orderNumber = "M"+sos.merchant.id + "-" +model.orderNumber ;
                }
            }else{
                vendor = Vendor.find.byId(mos.getVendorId());
                sos.vendor = vendor;
                sos.orderNumber = "V"+sos.vendor.id + "-" +model.orderNumber ;
            }
            
            sos.salesOrder = model;
            sos.member = model.member;
            sos.status = model.status;
            sos.shipmentAddress = model.shipmentAddress;
            
            Double courierValue = mos.getCourier().value;
            String[] etdSplit = mos.getCourier().etd.split("-");
            String etdTimeTarget = etdSplit[etdSplit.length-1].replace("HARI", "").trim().replace("+", "");
            Integer courierEstTime = StringUtils.isNumeric(etdTimeTarget) ? Integer.parseInt(etdTimeTarget) : 10 ;
            
            sos.courierCode = mos.getCourier().courierCode.trim();
            sos.courierName = mos.getCourier().courier.trim();
            sos.courierServiceCode = mos.getCourier().serviceCode.trim();
            sos.courierServiceName = mos.getCourier().service.trim();
            
            sos.shipping = courierValue == null || courierValue < 0 ? 0D : courierValue;
            
            sos.discount = 0D;
            sos.subtotal = sos.shipping;
            sos.voucher = sos.totalPrice = 0D;
            
            Date currentDate = new Date();
            sos.sentDate = currentDate;
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.DATE, courierEstTime);
            sos.deliveredDate = cal.getTime();
            sos.paymentStatus = SalesOrderSeller.UNPAID_HOKEBA;
            
            sos.save();

            Double subTotal2 = 0D;
            Double discount2 = 0D;
            Double priceTotal2 = 0D;
            Double weights = 0D;
            Double volumes = 0D;
            Double totalPayments = 0D;
            Map<Product, Integer> items = new HashMap<>();
            for (MapOrderDetail mod : mos.getItems()){ //TODO per items
                SalesOrderDetail detail = new SalesOrderDetail();
                Product product = Product.find.byId(mod.getProductId());
                ProductDetailVariance productVariance = ProductDetailVariance.find.byId(mod.getProductVariantId());
                detail.product = product;
                detail.productVar = productVariance;
                detail.salesOrder = model;
                detail.salesOrderSeller = sos;
                if (mod.getSizeId() != null){
                    Size size = Size.find.byId(mod.getSizeId());
                    detail.sizeName = size.international;
                    detail.fashionSize = size;
                }
                Double discountPersen = 0D;
                Double discountAmount = 0D;
                detail.quantity = mod.getQuantity();
                if (product.getDiscountActive()) {
	                switch (product.discountType){
	                    case 1 :
	                        discountAmount =  product.discount * detail.quantity;
	                        break;
	                    case 2 :
	                        discountPersen = product.discount;
	                        discountAmount = Math.floor((product.discount/100*product.price)) * detail.quantity;
	                        break;
	                }
                }
                detail.discountPersen = discountPersen;
                detail.discountAmount = discountAmount;
                detail.productName = product.name;
                detail.status = sos.status;
                detail.price = product.price;
                detail.priceDiscount = product.priceDisplay;
                detail.totalPrice = detail.quantity * detail.price;
                detail.voucher = detail.tax = 0D;
                detail.taxPrice = detail.totalPrice * (detail.tax/100);
                detail.subTotal = detail.totalPrice + detail.taxPrice - detail.discountAmount;
//                detail.paymentSeller = product.price * detail.quantity; //TODO buat bagi hasil?
                detail.paymentSeller = (flagOwnMerchant || product.buyPrice == null) ? 0D : (product.buyPrice * detail.quantity);


                subTotal += detail.subTotal;
                discount += discountAmount;
                priceTotal += detail.totalPrice;

                subTotal2 += detail.subTotal;
                discount2 += discountAmount;
                priceTotal2 += detail.totalPrice;
                detail.save();

                weights += product.getWeight();
                volumes += product.getVolumes();
                totalPayments += detail.paymentSeller;

                items.put(product, mod.getQuantity());

                productVariance.totalStock = productVariance.totalStock - detail.quantity; //TODO removing product stock
                productVariance.update();
                product.numOfOrder = product.numOfOrder == null ? 1 : (product.numOfOrder + 1); //TODO add order count to product for popularity
                product.update();
            }

            //TODO count shipping cost per seller
            sos.discount = discount2;
            sos.subtotal = subTotal2 + sos.shipping;
            sos.totalPrice = priceTotal2;
            sos.weights = weights;
            sos.volumes = volumes;
            sos.paymentSeller = flagOwnMerchant ? 0D : (totalPayments + sos.shipping);
            sos.update();
            shipping += sos.shipping;

            salesOrderSellers.add(sos.id);

        }

        model.shipping = shipping;
        model.totalPrice = priceTotal;
        model.discount = discount;
        model.subtotal = subTotal + shipping;
        model.paymentType = "REDEEM";
        model.update();
        
        checkLoyaltyRedeem(model.id,map);

        for (Long id : salesOrderSellers){
            SalesOrderSeller sos = SalesOrderSeller.find.byId(id);

            SalesOrderSellerStatus sosStatus = new SalesOrderSellerStatus(sos, new Date(), 1, "Your orders has been created.");
            sosStatus.save();

            Merchant merchant = sos.merchant;
            if (merchant != null && !merchant.isHokeba()){
                merchant.unpaidCustomer = merchant.getUnpaidCustomer() + sos.paymentSeller;
                merchant.update();
            }
        }
        
//        if (map.getPaymentMethod().equals("COD")){
        SalesOrderPayment payment = new SalesOrderPayment();
        payment.salesOrder = model;
        payment.invoiceNo = SalesOrderPayment.generateInvoiceCode();
        payment.confirmAt = new Date();
        payment.debitAccountName = "--";
        payment.debitAccountNumber = "--";
        payment.totalTransfer = model.subtotal;
        payment.imageUrl = "";
        payment.status = SalesOrderPayment.VERIFY;
        payment.comments = "";
        payment.settlement = true;
        payment.paymentType = "Redeem";
        payment.save();
//        }

        return model.id;
    }
    
    private static void checkLoyaltyRedeem(Long id, MapOrderRedeem map) {
    	SalesOrder model = SalesOrder.find.byId(id);
    	long userPoint = 0;
    	//customer must use points
    	//    	if (!map.getLoyalty().equals(0L)) {
    	userPoint = LoyaltyPoint.countPoint(model.member.id);
    	double eligible = 0;

    	for(SalesOrderSeller sos : model.salesOrderSellers) {
    		for(SalesOrderDetail sod : sos.salesOrderDetail){
    			Logger.info("available use: " + sod.product.getEligiblePointUsed());
    			eligible += (sod.product.price - sod.product.discount)*sod.quantity;
    		}
    	}

    	if(userPoint >= eligible) {
    		userPoint = (long)eligible;
    		try {
    			LoyaltyPoint loyaltyPoint = LoyaltyPoint.reducePoint(model.member.id, userPoint, model.id);
    			if (loyaltyPoint != null) {
    				model.loyaltyPoint.add(loyaltyPoint);
    			}
    			model.subtotal = 0D;
    			model.update();
    		} catch (Exception e) {
    			// TODO: handle exception
    			e.printStackTrace();
    		}
    	}
//    	}
		boolean checkAllocate = LoyaltyController.allocateEligiblePointRedeem(model.id,userPoint);//allocate loyalty per sod redeem only
    }
    
    public static Long fromRequestV2(Member member, MapOrder map){ //TODO create new order
        SalesOrder model = new SalesOrder();
        model.orderDate = new Date();
        model.orderNumber = generateSOCode();
        model.voucher = model.totalPrice = model.subtotal = model.discount = 0D;
        model.member = member;
        model.shipmentAddress = Address.find.byId(map.getShippingAddress());
        model.billingAddress = Address.find.byId(map.getBillingAddress());
//        model.bank = Bank.find.byId(map.getBankId());
//        model.status = ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION;
        model.status = ORDER_STATUS_CHECKOUT;
        model.expiredDate = PaymentExpiration.getExpired();
        model.struct = "";
        model.shipmentType = "";
        model.emailNotif = map.getOrderNotes();
        model.checkoutType = 0L;
        model.deviceType = map.getDeviceType();
//        CourierPointLocation cpl = null;
//        if (map.getPickupPoint() != null){
//            cpl = CourierPointLocation.find.byId(map.getPickupPoint());
//        }
//        if (cpl != null){
//            model.courierPointLocation = cpl;
//        }
        model.save();

        Double subTotal = 0D;
        Double discount = 0D;
        Double priceTotal = 0D;
        Double shipping = 0D;
        List<Long> salesOrderSellers = new ArrayList<>();
        for (MapOrderSeller mos : map.getSellers()){ //TODO per merchant
            SalesOrderSeller sos = new SalesOrderSeller();
            sos.orderDate = model.orderDate;
            Merchant merchant = null;
            Vendor vendor = null;
            boolean flagOwnMerchant = false;
            if (mos.getMerchantId() != null){
                merchant = Merchant.find.byId(mos.getMerchantId());
                flagOwnMerchant = merchant.ownMerchant;
                sos.merchant = merchant;
                if (sos.merchant.id == -1L){
                    sos.orderNumber = "M0-" +model.orderNumber ;
                }else{
                    sos.orderNumber = "M"+sos.merchant.id + "-" +model.orderNumber ;
                }
            }else{
                vendor = Vendor.find.byId(mos.getVendorId());
                sos.vendor = vendor;
                sos.orderNumber = "V"+sos.vendor.id + "-" +model.orderNumber ;
            }
//            ShippingCostDetail cs;
//            Courier c;
//            if (map.getPaymentMethod().equals("COD")){
//                c = Courier.find.where().eq("name", "COD").setMaxRows(1).findUnique();
//                cs = ShippingCostDetail.find.where().eq("service", c.services.get(0)).setMaxRows(1).findUnique();
//            }else{
//                cs = ShippingCostDetail.find.byId(mos.getCourierServiceId());
//                cs.getServiceName();
//                c = cs.service.courier;
//            }
            sos.salesOrder = model;
//            sos.courier = c;
//            sos.shippingCostDetail = cs;
            sos.member = model.member;
            sos.status = model.status;
            sos.shipmentAddress = model.shipmentAddress;
//            if (cpl != null){
//                sos.courierPointLocation = cpl;
//            }
            
            Date currentDate = new Date();
            
            if(mos.getShipmentType() == 0L) { // check whether user chose normal shipping or picking up themselves
            	//using normal shipping
            	sos.shipmentType = 0L;
            	
            	Double courierValue = mos.getCourier().value;
                String[] etdSplit = mos.getCourier().etd.split("-");
                String etdTimeTarget = etdSplit[etdSplit.length-1].replace("HARI", "").trim().replace("+", "");
                Integer courierEstTime = StringUtils.isNumeric(etdTimeTarget) ? Integer.parseInt(etdTimeTarget) : 10 ;
                
                sos.courierCode = mos.getCourier().courierCode.trim();
                sos.courierName = mos.getCourier().courier.trim();
                sos.courierServiceCode = mos.getCourier().serviceCode.trim();
                sos.courierServiceName = mos.getCourier().service.trim();
                
                sos.shipping = courierValue == null || courierValue < 0 ? 0D : courierValue;
                
                sos.discount = 0D;
                sos.subtotal = sos.shipping;
                sos.voucher = sos.totalPrice = 0D;
                
                
                sos.sentDate = currentDate;
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                cal.add(Calendar.DATE, courierEstTime);
                sos.deliveredDate = cal.getTime();
            }
            else if(mos.getShipmentType() == 1L){
            	// using pick up point
            	sos.shipmentType = 1L;
            	
            	sos.pickUpPointName = mos.getPickuppoint().name;
            	sos.pickUpPointAddress = mos.getPickuppoint().address;
            	sos.pickUpPointContact = mos.getPickuppoint().contact;
            	sos.pickUpPointDuration = mos.getPickuppoint().duration;
            	sos.pickUpPointLatitude = mos.getPickuppoint().latitude;
            	sos.pickUpPointLongitude = mos.getPickuppoint().longitude;
            	
            	sos.shipping = 0D;
            	sos.discount = 0D;
            	sos.subtotal = sos.shipping;
            	sos.voucher = sos.totalPrice = 0D;

            	  
            }

            sos.paymentStatus = SalesOrderSeller.UNPAID_CUSTOMER;
            
            sos.save();

            Double subTotal2 = 0D;
            Double discount2 = 0D;
            Double priceTotal2 = 0D;
            Double weights = 0D;
            Double volumes = 0D;
            Double totalPayments = 0D;
            Map<Product, Integer> items = new HashMap<>();
            //set bag status to "checkout" ================================================================
            Bag.bagCheckOut(mos.getItems(),member);
            for (MapOrderDetail mod : mos.getItems()){ //TODO per items
                SalesOrderDetail detail = new SalesOrderDetail();
                Product product = Product.find.byId(mod.getProductId());
                ProductDetailVariance productVariance = ProductDetailVariance.find.byId(mod.getProductVariantId());
                detail.product = product;
                detail.productVar = productVariance;
                detail.salesOrder = model;
                detail.salesOrderSeller = sos;
                if (mod.getSizeId() != null){
                    Size size = Size.find.byId(mod.getSizeId());
                    detail.sizeName = size.international;
                    detail.fashionSize = size;
                }
                Double discountPersen = 0D;
                Double discountAmount = 0D;
                detail.quantity = mod.getQuantity();
                ProductPrice productPrice = ProductPrice.find.where()
                		.eq("product_id", product.id)
                		.eq("is_active", true)
                		.le("start_date", currentDate)
                		.ge("end_date", currentDate)
                		.setMaxRows(1).findUnique();
                if (productPrice != null){
                	discountAmount =  (product.price - product.getPriceDisplay()) * detail.quantity;
                }
                else if (product.getDiscountActive()) {
	                switch (product.discountType){
	                    case 1 :
	                        discountAmount =  product.discount * detail.quantity;
	                        break;
	                    case 2 :
	                        discountPersen = product.discount;
	                        discountAmount = Math.floor((product.discount/100*product.price)) * detail.quantity;
	                        break;
	                }
                }
                detail.discountPersen = discountPersen;
                detail.discountAmount = discountAmount;
                detail.productName = product.name;
                detail.status = sos.status;
                detail.price = product.price;
                detail.priceDiscount = product.getPriceDisplay();
                detail.totalPrice = detail.quantity * detail.price;
                detail.voucher = detail.tax = 0D;
                detail.taxPrice = detail.totalPrice * (detail.tax/100);
                detail.subTotal = detail.totalPrice + detail.taxPrice - detail.discountAmount;
//                detail.paymentSeller = product.price * detail.quantity; //TODO buat bagi hasil?
                detail.paymentSeller = (flagOwnMerchant || product.buyPrice == null) ? 0D : (product.buyPrice * detail.quantity);


                subTotal += detail.subTotal;
                discount += discountAmount;
                priceTotal += detail.totalPrice;

                subTotal2 += detail.subTotal;
                discount2 += discountAmount;
                priceTotal2 += detail.totalPrice;
                detail.save();

                weights += product.getWeight();
                volumes += product.getVolumes();
                totalPayments += detail.paymentSeller;

                items.put(product, mod.getQuantity());

                productVariance.totalStock = productVariance.totalStock - detail.quantity; //TODO removing product stock
                productVariance.update();
                product.numOfOrder = product.numOfOrder == null ? 1 : (product.numOfOrder + 1); //TODO add order count to product for popularity
                product.update();
            }

            //TODO count shipping cost per seller
            sos.discount = discount2;
//            if (c.deliveryType.equals(Courier.DELIVERY_TYPE_PICK_UP_POINT)){
//                sos.shipping = ShippingUtil.calculateCost(items, sos.merchant.courierPointLocation, cpl);
//            }else{
//                sos.shipping = cs.calculateCost(weights, volumes);
//            }
//            sos.subtotal = subTotal2 + sos.shipping;
            sos.subtotal = subTotal2 + sos.shipping;
            sos.totalPrice = priceTotal2;
            sos.weights = weights;
            sos.volumes = volumes;
            sos.paymentSeller = flagOwnMerchant ? 0D : (totalPayments + sos.shipping);
            sos.update();
            shipping += sos.shipping;

            salesOrderSellers.add(sos.id);

        }

        model.shipping = shipping;
        model.totalPrice = priceTotal;
        model.discount = discount;
        model.subtotal = subTotal + shipping;
        model.paymentType = map.getPaymentMethod();
        model.update();
        
        HashMap<Integer, List<Voucher>> mapVoucher = new HashMap<>(); //TODO count voucher
        HashMap<Integer, List<VoucherDetail>> mapVoucherDetail = new HashMap<>();
        List<Long> voucherSet = new ArrayList<>();
        if (map.getVouchers() != null){
            for (MapVoucherCode code : map.getVouchers()){
                VoucherDetail voucherDetail = VoucherDetail.findByCode(code.getVoucherCode());
                if (voucherDetail != null){
                    Voucher voucher = voucherDetail.voucher;
                    if(voucher != null && !voucherSet.contains(voucher.id)){
                        List<Voucher> list = mapVoucher.get(voucher.priority);
                        if (list == null){
                            list = new LinkedList<>();
                        }
                        list.add(voucher);
                        mapVoucher.put(voucher.priority, list);

                        List<VoucherDetail> list2 = mapVoucherDetail.get(voucher.priority);
                        if (list2 == null){
                            list2 = new LinkedList<>();
                        }
                        list2.add(voucherDetail);
                        mapVoucherDetail.put(voucher.priority, list2);

                        voucherSet.add(voucher.id);
                    }
                }
            }
        }

        if(mapVoucher.size() > 0){
            int maxPriorityAllow = getMaxPriorityAllow(mapVoucher);
            SalesOrder so = SalesOrder.find.byId(model.id);
            for(int i=1; i<=maxPriorityAllow; i++){
                if(mapVoucher.containsKey(i)){
                    List<Voucher> vouchers = mapVoucher.get(i);
                    List<VoucherDetail> voucherDetails = mapVoucherDetail.get(i);
                    for (int j =0; j< vouchers.size(); j++){
                        entryVoucher(so, vouchers.get(j), voucherDetails.get(j), member);
                    }
                }
            }
            recalculateTotalSO(model.id);
        }
        
        checkLoyalty(model.id,map);

        for (Long id : salesOrderSellers){
            SalesOrderSeller sos = SalesOrderSeller.find.byId(id);

            SalesOrderSellerStatus sosStatus = new SalesOrderSellerStatus(sos, new Date(), 1, "Your orders has been created.");
            sosStatus.save();

            Merchant merchant = sos.merchant;
            if (merchant != null && !merchant.isHokeba()){
                merchant.unpaidCustomer = merchant.getUnpaidCustomer() + sos.paymentSeller;
                merchant.update();
            }
        }

        if (map.getPaymentMethod().equals("COD")){
            SalesOrderPayment payment = new SalesOrderPayment();
            payment.salesOrder = model;
            payment.invoiceNo = SalesOrderPayment.generateInvoiceCode();
            payment.confirmAt = new Date();
            payment.debitAccountName = "--";
            payment.debitAccountNumber = "--";
            payment.totalTransfer = model.subtotal;
            payment.imageUrl = "";
            payment.status = SalesOrderPayment.COD_VERIFY;
            payment.comments = "";
            payment.save();
        }

        return model.id;
    }
}
