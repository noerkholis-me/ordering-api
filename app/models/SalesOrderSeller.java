package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.ApiFilter;
import com.hokeba.api.ApiFilterValue;
import com.hokeba.api.ApiResponse;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.response.*;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import com.hokeba.util.Helper;

import javax.persistence.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import com.avaje.ebean.Expr;

/**
 * Created by hendriksaragih on 4/26/17.
 */
@Entity
public class SalesOrderSeller extends BaseModel {
    private static final long serialVersionUID = 1L;

    public static final Integer UNPAID_CUSTOMER = 0;
    public static final Integer UNPAID_HOKEBA = 1;
    public static final Integer PAID_HOKEBA = 2;

    public static Finder<Long, SalesOrderSeller> find = new Finder<>(Long.class,
            SalesOrderSeller.class);

	private static Date created_at;

	//private static Long id_referral;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    @JsonProperty("order_date")
    public Date orderDate;

    @Column(unique = true)
    @JsonProperty("order_number")
    public String orderNumber;

    @JsonIgnore
    @ManyToOne
    public Merchant merchant;

    @JsonIgnore
    @ManyToOne
    public Vendor vendor;

    @JsonIgnore
    @ManyToOne(cascade = { CascadeType.ALL })
    public SalesOrder salesOrder;

    public Double discount;
    public Double voucher;
    @JsonProperty("voucher_id")
    public Long voucherId;

    public Double subtotal;
    public Double weights;
    public Double volumes;

    @JsonProperty("retur_amount")
    public Double returAmount;

    @JsonProperty("total_price")
    public Double totalPrice;
    
    @JsonProperty("shipment_type")
    public Long shipmentType;

    @ManyToOne
    public Courier courier;

    @JsonProperty("courier_code")
    public String courierCode;
    @JsonProperty("courier_name")
    public String courierName;
    @JsonProperty("courier_service_code")
    public String courierServiceCode;
    @JsonProperty("courier_service_name")
    public String courierServiceName;
    
//    @ManyToOne
//    public PickUpPoint pickUpPoint;
    
    @JsonProperty("pick_up_point_name")
    public String pickUpPointName;
    @JsonProperty("pick_up_point_address")
    public String pickUpPointAddress;
    @JsonProperty("pick_up_point_contact")
    public String pickUpPointContact;
    @JsonProperty("pick_up_point_duration")
    public Long pickUpPointDuration;
    @JsonProperty("pick_up_point_latitude")
    public Double pickUpPointLatitude;
    @JsonProperty("pick_up_point_longitude")
    public Double pickUpPointLongitude;
    

    @JsonIgnore
    @ManyToOne
    public ShippingCostDetail shippingCostDetail;

    public String status;

    @OneToMany(mappedBy = "salesOrderSeller")
    @JsonProperty("items")
    public List<SalesOrderDetail> salesOrderDetail;

    @JsonIgnore
    @ManyToOne
    public Member member;

    @ManyToOne
    @JsonProperty("shipping_address")
    public Address shipmentAddress;

    @ManyToOne
    @JsonProperty("pickup_point")
    public CourierPointLocation courierPointLocation;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    @JsonProperty("sent_date")
    public Date sentDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    @JsonProperty("delivered_date")
    public Date deliveredDate;

    @JsonProperty("tracking_number")
    public String trackingNumber;

    //odoo
    @Column(name = "odoo_id")
    public Integer odooId;

    @Column(name = "vendor_odoo_id")
    public Integer vendorOdooId;

    @Column(name = "invoice_odoo_id")
    public Integer invoiceOdooId;

    @Column(name = "invoice_vendor_odoo_id")
    public Integer invoiceVendorOdooId;

    public Double shipping;

    @Column(name = "payment_status")
    public Integer paymentStatus;

    @JsonProperty("payment_seller")
    public Double paymentSeller;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "payment_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date paymentDate;

    @OneToMany(mappedBy = "salesOrderSeller")
    @JsonProperty("sales_order_status")
    public List<SalesOrderSellerStatus> salesOrderSellerStatuses;

	//private Long id_referral;

    @Transient
    @JsonProperty("items")
    public List<SalesOrderDetail> getItems(){
        return salesOrderDetail;
    }

    @Transient
    @JsonProperty("buyer_id")
    public Long getBuyerId(){
        return member.id;
    }

    @Transient
    @JsonProperty("buyer_name")
    public String getBuyerName(){
        return member.fullName;
    }

    @Transient
    @JsonProperty("buyer_address")
    public String getBuyerAddress(){
        return getShippingAddress();
    }

    @Transient
    @JsonProperty("dispatch_date")
    public String getDispatchDate(){
        return "";
    }

    @Transient
    @JsonProperty("sdate")
    public String getSdate(){
        return CommonFunction.getDate(sentDate);
    }

//    @javax.persistence.Transient
//    @JsonProperty("courier_name")
//    public String getCourierName(){
////    	return shippingCostDetail.getServiceName();
//        return "";
//    }

    @Transient
    @JsonProperty("seller_name")
    public String getSellerName(){
        return merchant != null ? merchant.fullName : vendor.name;
    }
    
    @JsonGetter("merchant_id")
    public Long getMerchantId() {
    	return merchant != null ? merchant.id : null;
    }

    @JsonGetter("order_notes")
    public String getOrderNotes() {
    	return salesOrder != null ? salesOrder.getOrderNotes() : "";
    }
    
    @Transient
    @JsonProperty("edate")
    public String getEdate(){
        return CommonFunction.getDate(deliveredDate);
    }

    @Transient
    @JsonProperty("date")
    public String getDate(){
        return CommonFunction.getDate(orderDate);
    }

    @Transient
    @JsonProperty("total")
    public Double getTotal(){
        return subtotal - getSellerLoyalty();
    }

    @Transient
    @JsonProperty("real_price")
    public Double getRealPrice(){
        return paymentSeller;
    }

    @Transient
    @JsonProperty("commision")
    public Double getCommision(){
        return totalPrice - discount - paymentSeller;
    }

    @Transient
    @JsonProperty("sell_price")
    public Double getSellPrice(){
        return totalPrice;
    }

    @Transient
    @JsonProperty("receive_payment")
    public Double getReceivePayment(){
        return paymentSeller + shipping;
    }

    @Transient
    @JsonProperty("sub_total")
    public Double getSubTotal(){
        return totalPrice;
    }
    
    @Transient
    @JsonProperty("sub_total_with_discount")
    public Double getSubTotalWithDiscount(){
        return totalPrice - discount;
    }

    public Double getReturAmount(){
        return returAmount != null ? returAmount : 0D;
    }

    @Transient
    @JsonProperty("customer_name")
    public String getCustomerName(){
        return member.fullName;
    }

    @Transient
    @JsonProperty("payment_type")
    public String getPaymentType(){
        return salesOrder.getPaymentMethod();
    }

    @Transient
    @JsonProperty("payment_method")
    public String getPaymentMethod(){
        return salesOrder.getPaymentMethod();
    }

    @Transient
    @JsonProperty("payment_status")
    public String getPaymentStatusOrder(){
        return salesOrder.getPaymentStatus();
    }

    @Transient
    @JsonProperty("currency")
    public String getCurrency(){
        return Constant.defaultCurrency;
    }

    @Transient
    @JsonGetter("payment_date_cust")
    public String getPaymentDateCust(){
        return salesOrder.salesOrderPayment!=null?salesOrder.salesOrderPayment.getConfirmAt():"";
    }

    @Transient
    @JsonProperty("seller_loyalty")
    public Long getSellerLoyalty() {
    	long totalLoyaltyUsage = 0;
    	for(SalesOrderDetail sod : salesOrderDetail) {
    		if(sod.loyaltyEligibleUse != null)
    		totalLoyaltyUsage += sod.loyaltyEligibleUse;
    	}
    	return totalLoyaltyUsage;
    }
    
    @Transient
    @JsonProperty("seller_loyalty_earn")
    public Long getSellerLoyaltyEarn() {
    	long totalLoyaltyEarn = 0;
    	for(SalesOrderDetail sod : salesOrderDetail) {
    		if(sod.loyaltyEligibleEarn != null)
    		totalLoyaltyEarn += sod.loyaltyEligibleEarn;
    	}
    	return totalLoyaltyEarn;
    }

    public String getStatus(){
        String result = "";
        switch (status){
            case SalesOrder.ORDER_STATUS_VERIFY : result = "Order Verified";break;
            case SalesOrder.ORDER_STATUS_WAITING_PAYMENT_CONFIRMATION : result = "Waiting Payment Confirmation";break;
            case SalesOrder.ORDER_STATUS_EXPIRE_PAYMENT : result = "Expire Payment";break;
            case SalesOrder.ORDER_STATUS_PICKING : result = "Picking";break;
            case SalesOrder.ORDER_STATUS_PACKING : result = "Packing";break;
            case SalesOrder.ORDER_STATUS_ON_DELIVERY : result = "On Delivery";break;
            case SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER : result = "Received By Customer";break;
            case SalesOrder.ORDER_STATUS_CUSTOMER_NOT_AT_THE_ADDRESS_STATE : result = "Customer Not At The Address Statte";break;
            case SalesOrder.ORDER_STATUS_CANCEL : result = "Cancel";break;
            case SalesOrder.ORDER_STATUS_RETURN : result = "Return";break;
            case SalesOrder.ORDER_STATUS_REPLACED : result = "Replaced";break;
            case SalesOrder.ORDER_STATUS_CANCEL_BY_CUSTOMER_SERVICE : result = "Cancel By Customer Service";break;
            default: status = "Invalid Status";
        }

        return result;
    }

    public String getStatusRaw(){
        return status;
    }

    public String getPaymentStatus(){
        String result = "";
        switch (paymentStatus){
            case 0 : result = "Unpaid Customer";break;
            case 1 : result = "Paid Customer";break;
            case 2 : result = "Paid Whizliz";break;
            default: status = "Invalid Status";
        }

        return result;
    }

    public String getSeller(){
        if (merchant != null){
            return merchant.name;
        }else{
            return vendor.name;
        }
    }

    public String getShippings(){
//        if (shippingCostDetail != null){
//            return courier.name + " - " +shippingCostDetail.service.service;
//        }
        return "";
    }

    public String getTrackingNumber(){
        if (trackingNumber != null){
            return trackingNumber;
        }
        return "";
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

    public String getSentDate(){
        if (sentDate != null){
            return "Sent Date : "+ CommonFunction.getDate(sentDate);
        }
        return "";
    }

    public static Integer getOrderByStatus(Long merchant, List<String> status, Date sdate, Date edate){
        return SalesOrderSeller.find.where()
                .eq("merchant_id", merchant)
                .in("status", status)
                .ge("order_date", sdate)
                .le("order_date", edate)
                .findRowCount();
    }

    public static Integer getOrderByStatus(Long merchant, List<String> status){
        return SalesOrderSeller.find.where()
                .eq("merchant_id", merchant)
                .in("status", status)
                .findRowCount();
    }
    
    public List<MapOrderSellerVoucherDetail> fetchVoucherInfo() {
    	Map<Long, Double> mapValue = new LinkedHashMap<>();
    	Map<Long, String> mapName = new LinkedHashMap<>();
    	
    	//voucher discount (dengan asumsi 1 order hanya dapat menggunakan 1 voucher discount)
    	for (SalesOrderDetail detail : salesOrderDetail) {
			for (VoucherDetail voucherAtDetail : detail.voucherDetails) {
				if (mapName.containsKey(voucherAtDetail.id)) {
					Double currentValue = mapValue.get(voucherAtDetail.id);
					currentValue += detail.voucher;
					break;
				} else {
					Voucher mainVoucher = voucherAtDetail.voucher;
					mapName.put(voucherAtDetail.id, mainVoucher.masking);
					mapValue.put(voucherAtDetail.id, detail.voucher);
					break;
				}
			}
		}
    	
    	//voucher shipping
    	if (this.voucherId != null) {
    		VoucherDetail voucherShipping = VoucherDetail.find.byId(this.voucherId);
    		if (voucherShipping != null) {
    			Voucher mainVoucher = voucherShipping.voucher;
				mapName.put(voucherShipping.id, "<Shipping> " + mainVoucher.name);
				mapValue.put(voucherShipping.id, this.shipping);
    		}
    	}
    	
    	//process result
    	List<MapOrderSellerVoucherDetail> result = new ArrayList<>();
    	for (Long voucherId : mapValue.keySet()) {
			result.add(new MapOrderSellerVoucherDetail(mapName.get(voucherId), mapValue.get(voucherId)));
		}
    	return result;
    }

    public static MapOrderSummary getOrderSummary(Long merchant, Date sdate, Date edate){
        Integer newOrder = getOrderByStatus(merchant, SalesOrder.ORDER_NEW, sdate, edate);
        Integer orderProcessed = getOrderByStatus(merchant, SalesOrder.ORDER_PROCESSED, sdate, edate);
        Integer orderCompleted = getOrderByStatus(merchant, SalesOrder.ORDER_COMPLETED, sdate, edate);
        Integer orderReturn = getOrderByStatus(merchant, SalesOrder.ORDER_RETURN, sdate, edate);

        return new MapOrderSummary(newOrder, orderProcessed, orderReturn, orderCompleted);
    }

    public static ArrayList<MapOrderSummary> getOrderSummaryList(Long merchant, Date sdate, Date edate){
    	ArrayList<MapOrderSummary> result = new ArrayList<MapOrderSummary>();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        DateFormat df2 = new SimpleDateFormat("dd-MM-yy");
    	Date currentSdate = sdate;
    	Date currentEdate = Helper.getDate(df.format(sdate) + " 23:59:59");
    	do {
            Integer newOrder = getOrderByStatus(merchant, SalesOrder.ORDER_NEW, currentSdate, currentEdate);
            Integer orderProcessed = getOrderByStatus(merchant, SalesOrder.ORDER_PROCESSED, currentSdate, currentEdate);
            Integer orderCompleted = getOrderByStatus(merchant, SalesOrder.ORDER_COMPLETED, currentSdate, currentEdate);
            Integer orderReturn = getOrderByStatus(merchant, SalesOrder.ORDER_RETURN, currentSdate, currentEdate);
            result.add(new MapOrderSummary(newOrder, orderProcessed, orderReturn, orderCompleted, df2.format(currentSdate)));
    		currentSdate = Helper.addDate(currentSdate,Calendar.DATE, 1);
    		currentEdate = Helper.addDate(currentEdate,Calendar.DATE, 1);
		} while (!currentEdate.equals(edate));

        return result;
    }

    @Transient
    @JsonProperty("order_status")
    public MapOrderUserStatus getOrderStatus(){
        boolean isProcessing = false;
        boolean isShipped = false;
        boolean isCompleted = false;
        MapOrderUserStatusDetail processingDetail = new MapOrderUserStatusDetail();
        MapOrderUserStatusDetail shippedDetail = new MapOrderUserStatusDetail();
        MapOrderUserStatusDetail completedDetail = new MapOrderUserStatusDetail();

        List<String> ORDER_PROCESSED = Arrays.asList(SalesOrder.ORDER_STATUS_VERIFY, SalesOrder.ORDER_STATUS_PICKING, SalesOrder.ORDER_STATUS_PACKING);
        List<String> ORDER_SHIPPED = Arrays.asList(SalesOrder.ORDER_STATUS_ON_DELIVERY,SalesOrder.ORDER_STATUS_CUSTOMER_NOT_AT_THE_ADDRESS_STATE);
        List<String> ORDER_COMPLETED = Arrays.asList(SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER,SalesOrder.ORDER_STATUS_RETURN,SalesOrder.ORDER_STATUS_REPLACED);
        List<String> ORDER_CANCEL = Arrays.asList(SalesOrder.ORDER_STATUS_CANCEL,SalesOrder.ORDER_STATUS_CANCEL_BY_CUSTOMER_SERVICE);
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
        processingDetail.setStatus(false);
        shippedDetail.setStatus(false);
        completedDetail.setStatus(false);
        if(isProcessing){
            processingDetail.setStatus(true);
            SalesOrderSellerStatus soss = SalesOrderSellerStatus.find.where()
                    .eq("salesOrderSeller.id", id)
                    .ilike("description","Orders are being processed in our warehouse.")
                    .setMaxRows(1).findUnique();
            if(soss != null){
                processingDetail.setLastDetailStatus("Packing");
                processingDetail.setLastStatusDate(CommonFunction.getDateTime(soss.getDate()));
            }
        }
        if(isShipped){
            shippedDetail.setStatus(true);
            SalesOrderSellerStatus soss = SalesOrderSellerStatus.find.where()
                    .eq("salesOrderSeller.id", id)
                    .ilike("description","The orders was sent%")
                    .setMaxRows(1).findUnique();
            if(soss != null){
                shippedDetail.setLastDetailStatus("On Delivery");
                shippedDetail.setLastStatusDate(CommonFunction.getDateTime(soss.getDate()));
            }
        }
        if(isCompleted){
            completedDetail.setStatus(true);
            SalesOrderSellerStatus soss = SalesOrderSellerStatus.find.where()
                    .eq("salesOrderSeller.id", id)
                    .ilike("description","Your orders has arrived at the destination. Thank you for shopping at Whizliz")
                    .setMaxRows(1).findUnique();
            if(soss != null){
                completedDetail.setLastDetailStatus("Receive By Customer");
                completedDetail.setLastStatusDate(CommonFunction.getDateTime(soss.getDate()));
            }
        }
        MapOrderUserStatus orderStatus = new MapOrderUserStatus(isProcessing, isShipped, isCompleted, processingDetail, shippedDetail, completedDetail);
        return orderStatus;
    }

    public static <T> BaseResponse<T> getDataMerchant(Query<T> reqQuery, String type, String sort, String filter, int offset, int limit)
            throws IOException {
        Query<T> query = reqQuery;

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        }

        ExpressionList<T> exp = query.where();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


        exp = exp.conjunction();
        exp = exp.ilike("t0.order_number", filter + "%");
        switch (type){
            case "new" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("t0.status", "in", new ApiFilterValue[]{new ApiFilterValue(SalesOrder.ORDER_STATUS_VERIFY)}));
                break;
            case "paid" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("t0.status", "in", new ApiFilterValue[]{new ApiFilterValue(SalesOrder.ORDER_STATUS_PACKING)}));
                break;
            case "packed" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("t0.status", "in", new ApiFilterValue[]{new ApiFilterValue(SalesOrder.ORDER_STATUS_ON_DELIVERY)}));
                break;
            case "delivered" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("t0.status", "in", new ApiFilterValue[]{new ApiFilterValue(SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER)}));
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
        response.setData(new ObjectMapper().convertValue(resData, MapOrderMerchantList[].class));
        response.setMeta(total, offset, limit);
        response.setMessage("Success");

        return response;
    }

    public static Integer getOrderSeller(Long merchant, String date){
        Date sdate = Helper.getDateYmd(date);
        List<String> ORDER_COMPLETED = Arrays.asList(SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER, SalesOrder.ORDER_STATUS_RETURN, SalesOrder.ORDER_STATUS_REPLACED);
        return SalesOrderSeller.find.where()
                .eq("merchant_id", merchant)
                .in("status", ORDER_COMPLETED)
                .ge("order_date", sdate)
                .le("order_date", sdate)
                .findRowCount();
    }

    public static List<MapSellerData> getOrderSellers(Long merchant){
        List<MapSellerData> data = new LinkedList<>();
//        String edate = Helper.nowFormat();
//        String sdate = Helper.addDate(edate, -7);
//        while (!Objects.equals(sdate, edate)){
//            data.add(new MapSellerData(sdate, getOrderSeller(merchant, sdate)));
//            sdate = Helper.addDate(sdate, 1);
//        }
        
        Date currentDate = Helper.getCurrentDate();
        Date startDate = Helper.addDate(currentDate, Calendar.DATE, -7);
        int totalDayLoop = 7;
        int count = 0;
        while (count < totalDayLoop) {
        	data.add(new MapSellerData(Helper.parseDateToString(startDate, "yyyyMMdd"), fetchOrderSeller(merchant, startDate)));
        	startDate = Helper.addDate(startDate, Calendar.DATE, 1);
        	count++;
        }
        return data;
    }
    
    public static Integer fetchOrderSeller(Long merchant, Date date){
    	Date sdate = Helper.fetchStartOfDate(date);
        Date edate = Helper.fetchEndOfDate(date);
        List<String> ORDER_COMPLETED = Arrays.asList(SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER, SalesOrder.ORDER_STATUS_RETURN, SalesOrder.ORDER_STATUS_REPLACED);
        return SalesOrderSeller.find.where()
                .eq("merchant_id", merchant)
                .in("status", ORDER_COMPLETED)
                .ge("order_date", sdate)
                .le("order_date", edate)
                .findRowCount();
    }
    
    public static void finishAndGetPoint(SalesOrderSeller orderData) {
    	//========start adding point to each order seller per item========//
		Long pointsToEarn = 0L;
		Long pointsToEarnReferral = 0L;
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.YEAR, 1); // to get previous year add -1
		Date nextYear = cal.getTime();
//		for(SalesOrderSeller sos : orderData.salesOrderSellers ) {
					
		System.out.println("before looping data");
			for(SalesOrderDetail sod : orderData.salesOrderDetail) {
				System.out.println("looping data");
				if(sod.loyaltyEligibleEarn != null) {
					System.out.println("if member point<>0");
					pointsToEarn += sod.loyaltyEligibleEarn;
					
					// pengambilan parameter [x] transaksi
					ConfigSettings set_ref_max = ConfigSettings.find.where().eq("module", "loyaltysetting_referral").setMaxRows(1).findUnique();
					String ref_max_string = set_ref_max.value;
					
					int ref_max=Integer.parseInt(ref_max_string);
					
					// cek jumlah transaksi sukses
					int count_order  = SalesOrder.find.where().eq("member_id", sod.salesOrder.member.id).raw("(status = 'OV' or status = 'PA' or status = 'OD' or status = 'RC')").findRowCount();
					
					System.out.println("Order Delivered : "+ count_order+" Max loyalty referral: "+ref_max);
					
					List<SalesOrder> listOrder  = SalesOrder.find.where().eq("member_id", sod.salesOrder.member.id)
							.raw("(status = 'OV' or status = 'PA' or status = 'OD' or status = 'RC' )").orderBy("id").setMaxRows(1).findList();
					
					System.out.println(listOrder);
			        
					for (final SalesOrder salesorder : listOrder) {
			        	long id_salesorder = salesorder.id;
			        	
			        	System.out.println("order sales id : "+orderData.salesOrder.id);
			        	System.out.println("id_salesorder : " +id_salesorder);
			        	
			        	//System.out.println("id_salesorder : "+id_salesorder+"--"+"order sales id : "+orderData.salesOrder.id);
			        	
			        	// penambahan point referral
			        	if (id_salesorder == orderData.salesOrder.id)
			        	{
			        		
			        		System.out.println("add referral point , member id : "+sod.salesOrder.member.id);						
							
			        		System.out.println("if error member_referral check");
							
							long id_referral=0;
							
							MemberReferral listReferral = MemberReferral.find.where().eq("member_id", sod.salesOrder.member.id).setMaxRows(1).findUnique();
							
							if (listReferral != null)
							{
								id_referral = listReferral.referral.id;
								
								System.out.println("referral ID : "+ id_referral);
							}
							
							System.out.println("referral ID : "+ id_referral);
							
							// pengecekan jika ada referral.
							if (id_referral > 1)
							{
								pointsToEarnReferral += sod.loyaltyEligibleEarnReferral;
							}
			        	}
			        }
			        
			        //int i = 1;
					
					// penambahan point referral
//					if (count_order < ref_max)
//					{
//						System.out.println("add referral point , member id : "+sod.salesOrder.member.id);
//						
//						System.out.println("if error member_referral check");
//						
//						long id_referral=0;
//						
//						MemberReferral listReferral = MemberReferral.find.where().eq("member_id", sod.salesOrder.member.id).setMaxRows(1).findUnique();
//						if (listReferral != null)
//						{
//							id_referral = listReferral.referral.id;
//						}
//						
//						System.out.println("referral ID : "+ id_referral);
//						
//						// pengecekan jika ada referral.
//						if (id_referral > 1)
//						{
//							pointsToEarnReferral += sod.loyaltyEligibleEarnReferral;
//						}
//						
//					}
					 
					
				}
			}
//		}
		if(!pointsToEarn.equals(0L)) {
			try {
				LoyaltyPoint.addPoint(orderData.member.id, pointsToEarn, orderData.id, nextYear, "Gained point from " + orderData.orderNumber);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(!pointsToEarnReferral.equals(0L)) {
			try {
				MemberReferral listReferral = MemberReferral.find.where().eq("member_id", orderData.member.id).setMaxRows(1).findUnique();
				long id_referral = listReferral.referral.id;
				LoyaltyPoint.addPointReferral(id_referral, pointsToEarnReferral, orderData.id, nextYear, "Gained point referral from " + orderData.orderNumber);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        //========end of adding point to each order seller per item========//
    }

}
