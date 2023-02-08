package controllers.order;

import com.avaje.ebean.Query;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.Constant;
import controllers.BaseController;
import dtos.order.*;
import repository.BrandMerchantRepository;
import models.Address;
import models.BrandMerchant;
import models.Member;
import models.Merchant;
import models.Store;
import models.appsettings.AppSettings;
import models.internal.FeeSetting;
import models.merchant.FeeSettingMerchant;
import models.merchant.ProductMerchantDetail;
import models.transaction.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.AppSettingRepository;
import repository.FeeSettingMerchantRepository;
import repository.OrderPaymentRepository;
import repository.OrderRepository;
import repository.ProductMerchantDetailRepository;
// TableMerchant
import models.merchant.TableMerchant;
import repository.TableMerchantRepository;

// TableMerchant
import models.pupoint.*;
import repository.pickuppoint.PickUpPointRepository;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileOutputStream;

import service.DownloadOrderReport;

public class OrderMerchantController extends BaseController {

    private final static Logger.ALogger LOGGER = Logger.of(OrderMerchantController.class);
    private static final String PREPARING = "PREPARING";
    private static final String ON_COOKING = "ON COOKING";
    private static final String SERVING = "SERVING";
    private static final String ON_PROCESS = "ON PROCESS";
    private static final String READY_TO_PICKUP = "READY";

    private static BaseResponse response = new BaseResponse();

    public static Result getOrderList(Long storeId, int offset, int limit, String statusOrder, String filter) throws Exception {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {

                Query<Order> query = null;
                // default query find by merchant id
                if(statusOrder.equalsIgnoreCase("CANCELED")){
                    query = OrderRepository.find.where().eq("store.merchant", merchant).eq("t0.status", statusOrder).order("t0.id desc");
                } else if (statusOrder.equalsIgnoreCase("PENDING")) {
                    query = OrderRepository.find.where()
                    		.or(Expr.ne("t0.device_type", "MINIPOS"), Expr.isNull("t0.user_merchant_id"))
                    		.ne("t0.status", "CANCELLED").ne("t0.status", "CANCELED")
                    		.eq("orderPayment.status", statusOrder).eq("store.merchant", merchant).order("t0.id desc");
                } else {
                    query = OrderRepository.find.where().eq("orderPayment.status", "PAID").eq("store.merchant", merchant).eq("t0.status", statusOrder).order("t0.id desc");
                }
                // check store id --> mandatory
                if (storeId != null && storeId != 0L) {
                    query = null;
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    if(statusOrder.equalsIgnoreCase("CANCELED")){
                        query = OrderRepository.find.where().eq("store", store).eq("t0.status", statusOrder).order("t0.id desc");
                    } else if (statusOrder.equalsIgnoreCase("PENDING")) {
                        query = OrderRepository.find.where().or(Expr.ne("t0.device_type", "MINIPOS"), Expr.eq("t0.user_merchant_id", null)).or(Expr.ne("t0.status", "CANCELLED"), Expr.ne("t0.status", "CANCELED")).eq("orderPayment.status", statusOrder).eq("store", store).order("t0.id desc");
                    } else {
                        query = OrderRepository.find.where().eq("orderPayment.status", "PAID").eq("store", store).eq("t0.status", statusOrder).order("t0.id desc");

                    }
                }

                ExpressionList<Order> exp = query.where();
                exp = exp.disjunction();
                exp = exp.ilike("t0.order_number", "%" + filter + "%");
                exp = exp.ilike("t0.member_name", "%" + filter + "%");
                exp = exp.ilike("member.fullName", "%" + filter + "%");
                exp = exp.ilike("member.firstName", "%" + filter + "%");
                exp = exp.ilike("member.lastName", "%" + filter + "%");
                exp = exp.endJunction();
                query = exp.query();

                List<OrderList> orderLists = new ArrayList<>();
                List<Order> orders = query.findPagingList(limit).getPage(offset).getList();
                Integer totalData = query.findList().size();
                System.out.println(orders.size());
                if (orders.isEmpty() || orders.size() == 0) {
                    response.setBaseResponse(totalData, offset, limit, success + " Showing data order",
                            orderLists);
                    return ok(Json.toJson(response));
                }

                for (Order order : orders) {
                    OrderList orderRes = new OrderList();
                    // looping order
                    Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderId(order.id);
                    if (!orderPayment.isPresent()) {
                        System.out.println(">>>>> order payment does not exists <<<<< ");
                        break;
                    }
                    OrderPayment getOrderPayment = orderPayment.get();
                        // System.out.println(">>>>> Order payment when paid <<<<<");
                        orderRes.setInvoiceNumber(getOrderPayment.getInvoiceNo());
                        orderRes.setOrderNumber(order.getOrderNumber());

                        // get member
                        Member member = null;
                        if (order.getMember() != null) {
                            member = Member.findByIdMember(order.getMember().id);
                            orderRes.setCustomerName(member.fullName != null && member.fullName != "" ? member.fullName : "GENERAL CUSTOMER (" + order.getStore().storeName + ")");
                        } else {
                            String customerName = "GENERAL CUSTOMER (" + order.getStore().storeName + ")";
                            orderRes.setCustomerName(customerName);
                        }
                        orderRes.setCustomerPhone(order.getPhoneNumber());

                        // get store
                        orderRes.setMerchantName(order.getStore().getMerchant().name != null || order.getStore().getMerchant().name != "" ? order.getStore().getMerchant().name : null);

                        orderRes.setTotalAmount(order.getTotalPrice());
                        orderRes.setOrderType(order.getOrderType());
                        orderRes.setOrderQueue(order.getOrderQueue());
                        orderRes.setStatusOrder(order.getStatus());
                        orderRes.setStatus(order.getStatus());
                        orderRes.setPaymentType(getOrderPayment.getPaymentType());
                        orderRes.setPaymentChannel(getOrderPayment.getPaymentChannel());
                        orderRes.setTotalAmountPayment(getOrderPayment.getTotalAmount());
                        orderRes.setPaymentDate(getOrderPayment.getPaymentDate());

                        List<OrderDetail> orderDetails = OrderRepository.findOrderDetailByOrderId(order.id);
                        List<OrderList.ProductOrderDetail> productOrderDetails = new ArrayList<>();

                        // System.out.println(">>>>> loop order detail <<<<<");
                        for (OrderDetail orderDetail : orderDetails) {
                            // System.out.println(">>>>> order detail in : " + orderDetail.id);
                            OrderList.ProductOrderDetail productDetail = new OrderList.ProductOrderDetail();
                            productDetail.setProductId(orderDetail.getProductMerchant().id);
                            productDetail.setProductName(orderDetail.getProductName());
                            ProductMerchantDetail pMD = ProductMerchantDetailRepository.findMainProduct(orderDetail.getProductMerchant());
                            productDetail.setProductImage(pMD == null ? null : pMD.getProductImageMain());
                            productDetail.setProductPrice(orderDetail.getProductPrice());
                            productDetail.setProductQty(orderDetail.getQuantity());
                            productDetail.setNotes(orderDetail.getNotes());

                            // System.out.println(">>>>> loop order detail add on <<<<<<");
                            List<OrderList.ProductOrderDetail.ProductOrderDetailAddOn> productDetailAddOns = new ArrayList<>();
                            for (OrderDetailAddOn orderDetailAddOn : orderDetail.getOrderDetailAddOns()) {
                                // System.out.println(">>>>> order detail add on in : " + orderDetailAddOn.id);
                                OrderList.ProductOrderDetail.ProductOrderDetailAddOn productAddOn = new OrderList.ProductOrderDetail.ProductOrderDetailAddOn();
                                productAddOn.setProductId(orderDetailAddOn.getProductAddOn().getProductAssignId());
                                productAddOn.setProductName(orderDetailAddOn.getProductName());
                                productAddOn.setProductPrice(orderDetailAddOn.getProductPrice());
                                productAddOn.setProductQty(orderDetailAddOn.getQuantity());
                                productAddOn.setNotes(orderDetailAddOn.getNotes());
                                productDetailAddOns.add(productAddOn);
                            }
                            productDetail.setProductAddOn(productDetailAddOns);
                            productOrderDetails.add(productDetail);
                        }
                        orderRes.setProductOrderDetail(productOrderDetails);
                        orderLists.add(orderRes);
                    // }
                    
                }

                // System.out.println(">>>>> Total Data : " + totalData);
                // System.out.println(">>>>> Total Data Orders : " + orders.size());
                // System.out.println(">>>>> order list : " + orderLists.size());

                response.setBaseResponse(totalData, offset, limit, success + " Berhasil menampilkan data order",
                        orderLists);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                LOGGER.error("Error when getting list data orders");
                ex.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result getOrderListUser(Long storeId, int offset, int limit, String statusOrder, String filter) throws Exception {
        try {
            Query<Order> query = null;
            // check store id --> mandatory
            Store store = null;
            if (storeId != null && storeId != 0L) {
                store = Store.findById(storeId);    
            }
            if (store == null) {
                response.setBaseResponse(0, 0, 0, "Store id does not exists", null);
                return badRequest(Json.toJson(response));
            }
            
            if (statusOrder.equalsIgnoreCase("ALL")) {
            	query = OrderRepository.find.where().eq("store", store)
            			.order("t0.id desc");
            } else if (statusOrder.equalsIgnoreCase("CANCELED")) {
                query = OrderRepository.find.where().eq("t0.status", statusOrder).eq("store", store)
                		.order("t0.id desc");
            } else if (statusOrder.equalsIgnoreCase("PENDING")) {
                query = OrderRepository.find.where()
                		.or(Expr.ne("t0.device_type", "MINIPOS"), Expr.isNull("t0.user_merchant_id"))
                		.ne("t0.status", "CANCELLED").ne("t0.status", "CANCELED")
                		.eq("orderPayment.status", statusOrder)
                		.eq("store", store)
                		.order("t0.id desc");
            } else {
                query = OrderRepository.find.where().eq("t0.status", statusOrder).eq("store", store)
                		.eq("orderPayment.status", "PAID")
                		.order("t0.id desc");
            }
            
            ExpressionList<Order> exp = query.where();
            exp.ieq("t0.member_name", filter);
//            exp = exp.disjunction();
//            exp = exp.ilike("t0.order_number", "%" + filter + "%");
//            exp = exp.ilike("t0.member_name", "%" + filter + "%");
//            exp = exp.ilike("member.fullName", "%" + filter + "%");
//            exp = exp.ilike("member.firstName", "%" + filter + "%");
//            exp = exp.ilike("member.lastName", "%" + filter + "%");
//            exp = exp.endJunction();
            query = exp.query();

            List<OrderList> orderLists = new ArrayList<>();
            List<Order> orders = query.findPagingList(limit).getPage(offset).getList();
            Integer totalData = query.findList().size();
            System.out.println(orders.size());
            if (orders.isEmpty() || orders.size() == 0) {
                response.setBaseResponse(totalData, offset, limit, success + " Showing data order",
                        orderLists);
                return ok(Json.toJson(response));
            }

            for (Order order : orders) {
                OrderList orderRes = new OrderList();
                // looping order
                Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderId(order.id);
                if (!orderPayment.isPresent()) {
                    System.out.println(">>>>> order payment does not exists <<<<< ");
                    break;
                }
                OrderPayment getOrderPayment = orderPayment.get();
                // System.out.println(">>>>> Order payment when paid <<<<<");
                orderRes.setInvoiceNumber(getOrderPayment.getInvoiceNo());
                orderRes.setOrderNumber(order.getOrderNumber());

                // get member
                Member member = null;
                if (order.getMember() != null) {
                    member = Member.findByIdMember(order.getMember().id);
                    orderRes.setCustomerName(member.fullName != null && member.fullName != "" ? member.fullName : "GENERAL CUSTOMER (" + order.getStore().storeName + ")");
                } else {
                    String customerName = "GENERAL CUSTOMER (" + order.getStore().storeName + ")";
                    orderRes.setCustomerName(customerName);
                }
                orderRes.setCustomerPhone(order.getPhoneNumber());

                // get store
                orderRes.setMerchantName(order.getStore().getMerchant().name != null || order.getStore().getMerchant().name != "" ? order.getStore().getMerchant().name : null);

                orderRes.setTotalAmount(order.getTotalPrice());
                orderRes.setOrderType(order.getOrderType());
                orderRes.setOrderQueue(order.getOrderQueue());
                orderRes.setStatusOrder(order.getStatus());
                orderRes.setStatus(order.getStatus());
                orderRes.setPaymentType(getOrderPayment.getPaymentType());
                orderRes.setPaymentChannel(getOrderPayment.getPaymentChannel());
                orderRes.setTotalAmountPayment(getOrderPayment.getTotalAmount());
                orderRes.setPaymentDate(getOrderPayment.getPaymentDate());

                List<OrderDetail> orderDetails = OrderRepository.findOrderDetailByOrderId(order.id);
                List<OrderList.ProductOrderDetail> productOrderDetails = new ArrayList<>();

                // System.out.println(">>>>> loop order detail <<<<<");
                for (OrderDetail orderDetail : orderDetails) {
                    // System.out.println(">>>>> order detail in : " + orderDetail.id);
                    OrderList.ProductOrderDetail productDetail = new OrderList.ProductOrderDetail();
                    productDetail.setProductId(orderDetail.getProductMerchant().id);
                    productDetail.setProductName(orderDetail.getProductName());
                    ProductMerchantDetail pMD = ProductMerchantDetailRepository.findMainProduct(orderDetail.getProductMerchant());
                    productDetail.setProductImage(pMD == null ? null : pMD.getProductImageMain());
                    productDetail.setProductPrice(orderDetail.getProductPrice());
                    productDetail.setProductQty(orderDetail.getQuantity());
                    productDetail.setNotes(orderDetail.getNotes());

                    // System.out.println(">>>>> loop order detail add on <<<<<<");
                    List<OrderList.ProductOrderDetail.ProductOrderDetailAddOn> productDetailAddOns = new ArrayList<>();
                    for (OrderDetailAddOn orderDetailAddOn : orderDetail.getOrderDetailAddOns()) {
                        // System.out.println(">>>>> order detail add on in : " + orderDetailAddOn.id);
                        OrderList.ProductOrderDetail.ProductOrderDetailAddOn productAddOn = new OrderList.ProductOrderDetail.ProductOrderDetailAddOn();
                        productAddOn.setProductId(orderDetailAddOn.getProductAddOn().getProductAssignId());
                        productAddOn.setProductName(orderDetailAddOn.getProductName());
                        productAddOn.setProductPrice(orderDetailAddOn.getProductPrice());
                        productAddOn.setProductQty(orderDetailAddOn.getQuantity());
                        productAddOn.setNotes(orderDetailAddOn.getNotes());
                        productDetailAddOns.add(productAddOn);
                    }
                    productDetail.setProductAddOn(productDetailAddOns);
                    productOrderDetails.add(productDetail);
                }
                orderRes.setProductOrderDetail(productOrderDetails);
                orderLists.add(orderRes);
            
            }

            // System.out.println(">>>>> Total Data : " + totalData);
            // System.out.println(">>>>> Total Data Orders : " + orders.size());
            // System.out.println(">>>>> order list : " + orderLists.size());

            response.setBaseResponse(totalData, offset, limit, success + " Berhasil menampilkan data order",
                    orderLists);
            return ok(Json.toJson(response));
        } catch (Exception ex) {
            LOGGER.error("Error when getting list data orders");
            ex.printStackTrace();
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result checkStatusOrderNumber(int offset, int limit, String statusOrder, String email,
            String phoneNumber, String storeCode) throws Exception {
        if (email != null && email != "" || phoneNumber != null && phoneNumber != "") {
            Member memberUser = Member.findDataCustomer(email, phoneNumber);

            if (memberUser != null) {
                try {

                    Query<Order> query = null;
                    // default query find by merchant id
                    Store store = null;
                    store = Store.findByStoreCode(storeCode);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "Store tidak ditemukan", null);
                        return notFound(Json.toJson(response));
                    }
                    query = OrderRepository.find.where().eq("t0.user_id", memberUser.id).eq("t0.store_id", store.id)
                            .order("t0.created_at desc");

                    // check store id --> mandatory
                    // if (storeId != null && storeId != 0L) {
                    // if (store == null) {
                    // response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                    // return badRequest(Json.toJson(response));
                    // }
                    // query = OrderRepository.findAllOrderByStoreId(storeId);
                    // }

                    Integer totalData = OrderRepository.getTotalData(query, statusOrder);

                    List<OrderList> orderLists = new ArrayList<>();
                    List<Order> orders = OrderRepository.findAllOrderWithFilter(query, offset, limit, statusOrder);
                    if (orders.isEmpty() || orders.size() == 0) {
                        response.setBaseResponse(totalData, offset, limit, success + " Showing data transaction",
                                orderLists);
                        return ok(Json.toJson(response));
                    }

                    for (Order order : orders) {
                        OrderList orderRes = new OrderList();
                        // looping order
                        // System.out.println(">>>>> order in : " + order.id);
                        Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderId(order.id);
                        if (!orderPayment.isPresent()) {
                            System.out.println(">>>>> order payment does not exists <<<<< ");
                            break;
                        }
                        OrderPayment getOrderPayment = orderPayment.get();
                        // System.out.println(">>>>> Order payment when paid <<<<<");
                        orderRes.setInvoiceNumber(getOrderPayment.getInvoiceNo());
                        orderRes.setOrderNumber(order.getOrderNumber());

                        // get member
                        Member member = null;
                        if (order.getMember() != null) {
                            member = Member.findByIdMember(order.getMember().id);
                            orderRes.setCustomerName(member.fullName != null && member.fullName != "" ? member.fullName : "GENERAL CUSTOMER (" + order.getStore().storeName + ")");
                        } else {
                            String customerName = "GENERAL CUSTOMER (" + order.getStore().storeName + ")";
                            orderRes.setCustomerName(customerName);
                        }

                        // get store
                        store = Store.findById(order.getStore().id);
                        String merchantName = store == null ? null : store.getMerchant().name;
                        orderRes.setMerchantName(merchantName);

                        orderRes.setTotalAmount(order.getTotalPrice());
                        orderRes.setOrderType(order.getOrderType());
                        orderRes.setOrderQueue(order.getOrderQueue());
                        orderRes.setStatusOrder(order.getStatus());
                        orderRes.setStatus(order.getStatus());
                        orderRes.setPaymentType(getOrderPayment.getPaymentType());
                        orderRes.setPaymentChannel(getOrderPayment.getPaymentChannel());
                        orderRes.setTotalAmountPayment(getOrderPayment.getTotalAmount());
                        orderRes.setPaymentDate(getOrderPayment.getPaymentDate());

                        List<OrderDetail> orderDetails = OrderRepository.findOrderDetailByOrderId(order.id);
                        List<OrderList.ProductOrderDetail> productOrderDetails = new ArrayList<>();

                        // System.out.println(">>>>> loop order detail <<<<<");
                        for (OrderDetail orderDetail : orderDetails) {
                            // System.out.println(">>>>> order detail in : " + orderDetail.id);
                            OrderList.ProductOrderDetail productDetail = new OrderList.ProductOrderDetail();
                            productDetail.setProductId(orderDetail.getProductMerchant().id);
                            productDetail.setProductName(orderDetail.getProductName());
                            productDetail.setProductPrice(orderDetail.getProductPrice());
                            productDetail.setProductQty(orderDetail.getQuantity());
                            productDetail.setNotes(orderDetail.getNotes());

                            // System.out.println(">>>>> loop order detail add on <<<<<<");
                            List<OrderList.ProductOrderDetail.ProductOrderDetailAddOn> productDetailAddOns = new ArrayList<>();
                            for (OrderDetailAddOn orderDetailAddOn : orderDetail.getOrderDetailAddOns()) {
                                // System.out.println(">>>>> order detail add on in : " + orderDetailAddOn.id);
                                OrderList.ProductOrderDetail.ProductOrderDetailAddOn productAddOn = new OrderList.ProductOrderDetail.ProductOrderDetailAddOn();
                                productAddOn.setProductId(orderDetailAddOn.getProductAddOn().getProductAssignId());
                                productAddOn.setProductName(orderDetailAddOn.getProductName());
                                productAddOn.setProductPrice(orderDetailAddOn.getProductPrice());
                                productAddOn.setProductQty(orderDetailAddOn.getQuantity());
                                productAddOn.setNotes(orderDetailAddOn.getNotes());
                                productDetailAddOns.add(productAddOn);
                            }
                            productDetail.setProductAddOn(productDetailAddOns);
                            productOrderDetails.add(productDetail);
                        }
                        orderRes.setProductOrderDetail(productOrderDetails);
                        orderLists.add(orderRes);
                    }

                    // System.out.println(">>>>> Total Data : " + totalData);
                    // System.out.println(">>>>> Total Data Orders : " + orders.size());
                    // System.out.println(">>>>> order list : " + orderLists.size());

                    response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan data order", orderLists);
                    return ok(Json.toJson(response));
                } catch (Exception ex) {
                    LOGGER.error("Error when getting list data orders");
                    ex.printStackTrace();
                }
            }
            response.setBaseResponse(0, 0, 0, "Email atau Nomor Telepon tidak ditemukan", null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, "Email atau Nomor Telepon dibutuhkan", null);
        return badRequest(Json.toJson(response));
    }

    public static Result orderReportMerchant(String startDate, String endDate, int offset, int limit,
            String statusOrder, Long storeId) throws Exception {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Calendar calStartDate = Calendar.getInstance();
                Calendar calEndDate = Calendar.getInstance();

                if(startDate != null && !startDate.equals("")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    calStartDate.setTime(sdf.parse(startDate + " 00:00:00"));
                    calEndDate.setTime(sdf.parse(endDate + " 23:59:59"));
                }

                Query<Order> query = null;
                if(statusOrder != null && statusOrder != ""){
                    if(startDate != null && startDate != ""){
                        query = OrderRepository.find.where().raw("t0.order_date between '" + calStartDate.getTime() + "' and '" + calEndDate.getTime() + "'").ne("orderPayment.status", "PENDING").eq("store.merchant", merchant).eq("t0.status", statusOrder).order("t0.id desc");
                    } else {
                        query = OrderRepository.find.where().ne("orderPayment.status", "PENDING").eq("store.merchant", merchant).eq("t0.status", statusOrder).order("t0.id desc");
                    }
                } else {
                    if(startDate != null && startDate != ""){
                        query = OrderRepository.find.where().raw("t0.order_date between '" + calStartDate.getTime() + "' and '" + calEndDate.getTime() + "'").ne("orderPayment.status", "PENDING").eq("store.merchant", merchant).order("t0.id desc");
                    } else {
                        query = OrderRepository.find.where().ne("orderPayment.status", "PENDING").eq("store.merchant", merchant).order("t0.id desc");
                    }

                }
                // check store id --> mandatory
                if (storeId != null && storeId != 0L) {
                    query = null;
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    
                    if(statusOrder != null && statusOrder != ""){
                        if(startDate != null && startDate != ""){
                            query = OrderRepository.find.where().raw("t0.order_date between '" + calStartDate.getTime() + "' and '" + calEndDate.getTime() + "'").ne("orderPayment.status", "PENDING").eq("store", store).eq("t0.status", statusOrder).order("t0.id desc");
                        } else {
                            query = OrderRepository.find.where().ne("orderPayment.status", "PENDING").eq("store", store).eq("t0.status", statusOrder).order("t0.id desc");
                        } 
                    } else {
                        if(startDate != null && startDate != ""){
                            query = OrderRepository.find.where().raw("t0.order_date between '" + calStartDate.getTime() + "' and '" + calEndDate.getTime() + "'").ne("orderPayment.status", "PENDING").eq("store", store).order("t0.id desc");
                        } else {
                            query = OrderRepository.find.where().ne("orderPayment.status", "PENDING").eq("store", store).order("t0.id desc");
                        }
                    }
                }


                List<OrderList> orderLists = new ArrayList<>();
                List<Order> orders = query.findPagingList(limit).getPage(offset).getList();
                Integer totalData = query.findList().size();
                System.out.println(orders.size());
                if (orders.isEmpty() || orders.size() == 0) {
                    response.setBaseResponse(totalData, offset, limit, success + " Showing data order",
                            orderLists);
                    return ok(Json.toJson(response));
                }

                for (Order order : orders) {
                    OrderList orderRes = new OrderList();
                    // looping order
                    // System.out.println(">>>>> order in : " + order.id);
                    Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderId(order.id);
                    if (!orderPayment.isPresent()) {
                        System.out.println(">>>>> order payment does not exists <<<<< ");
                        break;
                    }
                    OrderPayment getOrderPayment = orderPayment.get();
                        System.out.println(">>>>> Order payment when paid <<<<<");
                        orderRes.setInvoiceNumber(getOrderPayment.getInvoiceNo());
                        orderRes.setOrderNumber(order.getOrderNumber());

                        // get member
                        Member member = null;
                        if (order.getMember() != null) {
                            member = Member.findByIdMember(order.getMember().id);
                            orderRes.setCustomerName(member.fullName != null && member.fullName != "" ? member.fullName : "GENERAL CUSTOMER (" + order.getStore().storeName + ")");
                        } else {
                            String customerName = "GENERAL CUSTOMER (" + order.getStore().storeName + ")";
                            orderRes.setCustomerName(customerName);
                        }

                        // get store
                        Store store = Store.findById(order.getStore().id);
                        String merchantName = store == null ? null : store.getMerchant().name;
                        orderRes.setMerchantName(merchantName);

                        orderRes.setTotalAmount(order.getTotalPrice());
                        orderRes.setOrderType(order.getOrderType());
                        orderRes.setOrderQueue(order.getOrderQueue());
                        orderRes.setStatusOrder(order.getStatus());
                        orderRes.setStatus(order.getStatus());
                        orderRes.setPaymentType(getOrderPayment.getPaymentType());
                        orderRes.setPaymentChannel(getOrderPayment.getPaymentChannel());
                        orderRes.setTotalAmountPayment(getOrderPayment.getTotalAmount());
                        orderRes.setPaymentDate(getOrderPayment.getPaymentDate());
                        orderRes.setShipperOrderId(order.getShipperOrderId());

                        List<OrderDetail> orderDetails = OrderRepository.findOrderDetailByOrderId(order.id);
                        List<OrderList.ProductOrderDetail> productOrderDetails = new ArrayList<>();

                        // System.out.println(">>>>> loop order detail <<<<<");
                        for (OrderDetail orderDetail : orderDetails) {
                            OrderList.ProductOrderDetail productDetail = new OrderList.ProductOrderDetail();
                            productDetail.setProductId(orderDetail.getProductMerchant().id);
                            productDetail.setProductName(orderDetail.getProductName());
                            productDetail.setProductPrice(orderDetail.getProductPrice());
                            productDetail.setProductQty(orderDetail.getQuantity());
                            productDetail.setNotes(orderDetail.getNotes());

                            // System.out.println(">>>>> loop order detail add on <<<<<<");
                            List<OrderList.ProductOrderDetail.ProductOrderDetailAddOn> productDetailAddOns = new ArrayList<>();
                            for (OrderDetailAddOn orderDetailAddOn : orderDetail.getOrderDetailAddOns()) {
                                OrderList.ProductOrderDetail.ProductOrderDetailAddOn productAddOn = new OrderList.ProductOrderDetail.ProductOrderDetailAddOn();
                                productAddOn.setProductId(orderDetailAddOn.getProductAddOn().getProductAssignId());
                                productAddOn.setProductName(orderDetailAddOn.getProductName());
                                productAddOn.setProductPrice(orderDetailAddOn.getProductPrice());
                                productAddOn.setProductQty(orderDetailAddOn.getQuantity());
                                productAddOn.setNotes(orderDetailAddOn.getNotes());
                                productDetailAddOns.add(productAddOn);
                            }
                            productDetail.setProductAddOn(productDetailAddOns);
                            productOrderDetails.add(productDetail);
                        }
                        orderRes.setProductOrderDetail(productOrderDetails);
                        orderLists.add(orderRes);
                    }

                response.setBaseResponse(totalData, offset, limit, " Berhasil menampilkan data order report",
                        orderLists);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                LOGGER.error("Error when getting list data orders");
                ex.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result downloadTransaction(String startDate, String endDate, int offset, int limit,
            String statusOrder, Long storeId) throws Exception {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Calendar calStartDate = Calendar.getInstance();
                Calendar calEndDate = Calendar.getInstance();

                if(startDate != null && !startDate.equals("")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    calStartDate.setTime(sdf.parse(startDate + " 00:00:00"));
                    calEndDate.setTime(sdf.parse(endDate + " 23:59:59"));
                }

                Query<Order> query = null;
                // default query find by merchant id
                if(statusOrder != null && statusOrder != ""){
                    if(startDate != null && startDate != ""){
                        query = OrderRepository.find.where().ne("orderPayment.status", "PENDING").eq("store.merchant", merchant).eq("t0.status", statusOrder).order("t0.id desc");
                    } else {
                        query = OrderRepository.find.where().raw("t0.order_date between '" + calStartDate.getTime() + "' and '" + calEndDate.getTime() + "'").ne("orderPayment.status", "PENDING").eq("store.merchant", merchant).eq("t0.status", statusOrder).order("t0.id desc");
                    }
                } else {
                    if(startDate != null && startDate != ""){
                        query = OrderRepository.find.where().raw("t0.order_date between '" + calStartDate.getTime() + "' and '" + calEndDate.getTime() + "'").ne("orderPayment.status", "PENDING").eq("store.merchant", merchant).order("t0.id desc");
                    } else {
                        query = OrderRepository.find.where().ne("orderPayment.status", "PENDING").eq("store.merchant", merchant).order("t0.id desc");
                    }

                }
                // check store id --> mandatory
                String storeName = "";
                if (storeId != null && storeId != 0L) {
                    query = null;
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    storeName = store.storeName;
                    
                    if(statusOrder != null && statusOrder != ""){
                        if(startDate != null && startDate != ""){
                            query = OrderRepository.find.where().raw("t0.order_date between '" + calStartDate.getTime() + "' and '" + calEndDate.getTime() + "'").ne("orderPayment.status", "PENDING").eq("store", store).eq("t0.status", statusOrder).order("t0.id desc");
                        } else {
                            query = OrderRepository.find.where().ne("orderPayment.status", "PENDING").eq("store", store).eq("t0.status", statusOrder).order("t0.id desc");
                        } 
                    } else {
                        if(startDate != null && startDate != ""){
                            query = OrderRepository.find.where().raw("t0.order_date between '" + calStartDate.getTime() + "' and '" + calEndDate.getTime() + "'").ne("orderPayment.status", "PENDING").eq("store", store).order("t0.id desc");
                        } else {
                            query = OrderRepository.find.where().ne("orderPayment.status", "PENDING").eq("store", store).order("t0.id desc");
                        }
                    }
                }


                List<OrderList> orderLists = new ArrayList<>();
                List<Order> orders = query.findPagingList(limit).getPage(offset).getList();
                Integer totalData = query.findList().size();
                System.out.println(orders.size());
                if (orders.isEmpty() || orders.size() == 0) {
                    response.setBaseResponse(totalData, offset, limit, success + " Showing data order",
                            orderLists);
                    return ok(Json.toJson(response));
                }

                File file = DownloadOrderReport.downloadOrderReport(orders);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateOrderReport = "";
                if(startDate != null && !startDate.equals("")) {
                    dateOrderReport = startDate + "_" + endDate;
                }else {
                    dateOrderReport = simpleDateFormat.format(new Date());
                }
                String filenameOrderReport = "LaporanOrder-" + storeName.replaceAll(" ", "_") + "-" + merchant.name + "-" + dateOrderReport + ".xlsx";
                response().setContentType("application/vnd.ms-excel");
                response().setHeader("Content-disposition", "attachment; filename=" + filenameOrderReport);
                return ok(file);
            } catch (Exception ex) {
                ex.printStackTrace();
                LOGGER.error("Error while download order report ", ex);
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result printInvoice(String orderNumber) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                if (orderNumber.equalsIgnoreCase("") || orderNumber == null) {
                    response.setBaseResponse(0, 0, 0, "order number cannot be null or empty.", null);
                    return badRequest(Json.toJson(response));
                }
                Optional<Order> order = OrderRepository.findByOrderNumber(orderNumber);
                if (!order.isPresent()) {
                    response.setBaseResponse(0, 0, 0, "order number does not exists.", null);
                    return badRequest(Json.toJson(response));
                }
                Order getOrder = order.get();

                Store store = getOrder.getStore();

                InvoicePrintResponse invoicePrintResponse = new InvoicePrintResponse();

                AppSettings appSettings = AppSettingRepository.findByMerchantId(store.getMerchant().id);
                String sandboxImage = Constant.getInstance().getImageUrl().concat("/assets/images/logo-sandbox.png");
                if (appSettings == null) {
                    invoicePrintResponse.setImageStoreUrl(sandboxImage != null && sandboxImage != "" ? sandboxImage: "");
                } else if (appSettings != null && appSettings.getAppLogo() != null && appSettings.getAppLogo() != "") {
                    invoicePrintResponse.setImageStoreUrl(appSettings.getAppLogo() != null && appSettings.getAppLogo() != "" ? appSettings.getAppLogo() : sandboxImage);
                } else {
                    invoicePrintResponse.setImageStoreUrl(sandboxImage != null && sandboxImage != "" ? sandboxImage: "");
                }

                invoicePrintResponse.setStoreName(store.storeName);
                invoicePrintResponse.setStoreAddress(store.storeAddress);
                invoicePrintResponse.setStorePhoneNumber(store.storePhone);

                invoicePrintResponse.setDestinationAddress(getOrder.getDestinationAddress() == null ? "-" : getOrder.getDestinationAddress());
                
                OrderPayment orderPayment = getOrder.getOrderPayment();
                invoicePrintResponse.setInvoiceNumber(orderPayment.getInvoiceNo());
                invoicePrintResponse.setOrderNumber(getOrder.getOrderNumber());
                invoicePrintResponse.setOrderType(getOrder.getOrderType());
                invoicePrintResponse.setOrderDate(getOrder.getOrderDate());
                invoicePrintResponse.setOrderTime(getOrder.getOrderDate());

                List<OrderDetail> orderDetails = getOrder.getOrderDetails();
                List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
                for (OrderDetail orderDetail : orderDetails) {
                    OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
                    orderDetailResponse.setProductName(orderDetail.getProductName());
                    orderDetailResponse.setQty(orderDetail.getQuantity());
                    orderDetailResponse.setTotal(orderDetail.getSubTotal());
                    orderDetailResponse.setNoSku(orderDetail.getProductMerchant().getNoSKU());
                    List<OrderDetailAddOnResponse> orderDetailAddOns = new ArrayList<>();
                    List<OrderDetailAddOn> orderDetailAddOnList = orderDetail.getOrderDetailAddOns();
                    for (OrderDetailAddOn orderDetailAddOn : orderDetailAddOnList) {
                        OrderDetailAddOnResponse orderDetailAddOnResponse = new OrderDetailAddOnResponse();
                        orderDetailAddOnResponse.setProductName(orderDetailAddOn.getProductName());
                        orderDetailAddOnResponse.setNoSku(orderDetailAddOn.getProductAddOn().getProductMerchant().getNoSKU());
                        orderDetailAddOns.add(orderDetailAddOnResponse);
                    }
                    orderDetailResponse.setOrderDetailAddOns(orderDetailAddOns);
                    orderDetailResponses.add(orderDetailResponse);
                }
                TableMerchant tmerch = TableMerchantRepository.find.where().eq("t0.id", getOrder.table_id).findUnique();
                if(tmerch != null){
                    invoicePrintResponse.setTableMerchant(tmerch);
                } else {
                    invoicePrintResponse.setTableMerchant(null);
                }
                PickUpPointMerchant pupMerchant = PickUpPointRepository.find.where().eq("t0.id", getOrder.pickup_point_id).findUnique();
                if(pupMerchant != null){
                    invoicePrintResponse.setPickupPoint(pupMerchant);
                } else {
                    invoicePrintResponse.setPickupPoint(null);
                }
                invoicePrintResponse.setOrderDetails(orderDetailResponses);
                invoicePrintResponse.setSubTotal(getOrder.getSubTotal());
                invoicePrintResponse.setTaxPrice(orderPayment.getTaxPrice());
                invoicePrintResponse.setTaxPercentage(orderPayment.getTaxPercentage());
                invoicePrintResponse.setServicePercentage(orderPayment.getServicePercentage());
                invoicePrintResponse.setServiceFee(orderPayment.getServicePrice());

                invoicePrintResponse.setPaymentFeeType(orderPayment.getPaymentFeeType());
                invoicePrintResponse.setPaymentFeeOwner(orderPayment.getPaymentFeeOwner());
                invoicePrintResponse.setPaymentFeeCustomer(orderPayment.getPaymentFeeCustomer());
                invoicePrintResponse.setTotal(getOrder.getTotalPrice());
                invoicePrintResponse.setOrderQueue(getOrder.getOrderQueue());
                invoicePrintResponse.setPaymentStatus(orderPayment.getStatus());
                invoicePrintResponse.setReferenceNumber("-");
                
                Member memberTarget = getOrder.getMember();
                if (memberTarget != null) {
                    invoicePrintResponse.setCustomerName(memberTarget.fullName != null ? memberTarget.fullName : memberTarget.firstName + " " + memberTarget.lastName);
                    Address shipping = Address.getPrimaryAddress(memberTarget.id, Address.SHIPPING_ADDRESS);
                    invoicePrintResponse.setCustomerShippingAddress(shipping != null ? shipping.address : "-");
                    Address billing = Address.getPrimaryAddress(memberTarget.id, Address.BILLING_ADDRESS);
                    invoicePrintResponse.setCustomerBillingAddress(billing != null ? billing.address : "-");
                    invoicePrintResponse.setCustomerPhone(getOrder.getPhoneNumber());
                } else {
                	invoicePrintResponse.setCustomerName("GENERAL CUSTOMER " + getOrder.getStore().storeName);
                	invoicePrintResponse.setCustomerShippingAddress("-");
                	invoicePrintResponse.setCustomerBillingAddress("-");
                	invoicePrintResponse.setCustomerPhone(getOrder.getPhoneNumber());
                }

                if (getOrder.getUserMerchant() != null) {
                    invoicePrintResponse.setCashierName(getOrder.getUserMerchant().getFullName() != null ? getOrder.getUserMerchant().getFullName() : getOrder.getUserMerchant().getFirstName() + " " + getOrder.getUserMerchant().getLastName());
                } else {
                    invoicePrintResponse.setCashierName("Admin");
                }

                String orderNumberEncode = Base64.getEncoder().encodeToString(getOrder.getOrderNumber().getBytes(StandardCharsets.UTF_8));
                String webhookOrderDetailUrl = Constant.getInstance().getWebhookOrderDetailUrl();
                String orderDetailUrl = webhookOrderDetailUrl.replace("{orderNumber}", orderNumberEncode);

                invoicePrintResponse.setOrderQrCode(orderDetailUrl);
                invoicePrintResponse.setShipperOrderId(getOrder.getShipperOrderId());

                response.setBaseResponse(1, offset, limit, success + " success showing data invoice.",
                        invoicePrintResponse);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return unauthorized(Json.toJson(response));
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }

    public static Result listQueueOrder(Long storeId, int offset, int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            if (storeId == null || storeId == 0L) {
                response.setBaseResponse(0, 0, 0, "store id tidak boleh null atau kosong", null);
                return badRequest(Json.toJson(response));
            }
            Query<Order> orderQuery = OrderRepository.findAllOrderByStoreIdNow(storeId);
            List<Order> orders = OrderRepository.findOrdersQueue(orderQuery, offset, limit);
            List<OrderQueueResponse> orderQueueResponses = new ArrayList<>();
            OrderQueueResponse.OrderDetail orderQueueResponsesDetail = new OrderQueueResponse.OrderDetail();
            for (Order order : orders) {
                OrderQueueResponse orderQueueResponse = new OrderQueueResponse();
                orderQueueResponse.setOrderQueue(order.getOrderQueue());
                orderQueueResponse.setTransactionNo(order.getOrderNumber());
                orderQueueResponse.setCustomerName(order.getMemberName() != null ? order.getMemberName() : "GENERAL CUSTOMER");
                List<OrderQueueResponse.OrderDetail> orderDetailListResponses = new ArrayList<>();
                List<OrderDetail> orderDetailList = order.getOrderDetails();
                for (OrderDetail oDetails : orderDetailList) {
                    OrderQueueResponse.OrderDetail orderDetailListResponse = new OrderQueueResponse.OrderDetail();
                    orderDetailListResponse.setProductName(oDetails.getProductName());
                    orderDetailListResponse.setQty(oDetails.getQuantity());
                    orderDetailListResponses.add(orderDetailListResponse);
                    List<OrderQueueResponse.OrderDetail.OrderDetailAddOn> orderDetailAddOnListResponses = new ArrayList<>();
                    List<OrderDetailAddOn> orderDetailAddOnList = oDetails.getOrderDetailAddOns();
                    for (OrderDetailAddOn oDetailAddOn: orderDetailAddOnList) {
                        OrderQueueResponse.OrderDetail.OrderDetailAddOn orderDetailAddOnListResponse = new OrderQueueResponse.OrderDetail.OrderDetailAddOn();
                        orderDetailAddOnListResponse.setProductName(oDetails.getProductName());
                        orderDetailAddOnListResponse.setQty(oDetails.getQuantity());
                        orderDetailAddOnListResponses.add(orderDetailAddOnListResponse);
                    }
                    orderDetailListResponse.setOrderDetailAddOn(orderDetailAddOnListResponses);
                }
                orderQueueResponse.setOrderDetails(orderDetailListResponses);
                orderQueueResponse.setOrderHour(order.getOrderDate());
                OrderStatus orderStatus = OrderStatus.convertToOrderStatus(order.getStatus());
                orderQueueResponse.setStatus(convertOrderStatus(orderStatus));
                orderQueueResponses.add(orderQueueResponse);
            }
            response.setBaseResponse(orders.size(), 0, 5, success + " menampilkan list queue order", orderQueueResponses);
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }

    public static Result orderListCustomer(Long memberId, Long storeId, int offset, int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            if (memberId == null || memberId == 0L) {
                response.setBaseResponse(0, 0, 0, "customer id tidak boleh null atau kosong", null);
                return badRequest(Json.toJson(response));
            }
            if (storeId == null || storeId == 0L) {
                response.setBaseResponse(0, 0, 0, "store id tidak boleh null atau kosong", null);
                return badRequest(Json.toJson(response));
            }
            Query<Order> orderQuery = OrderRepository.findAllOrderByMemberIdAndStoreId(memberId, storeId);
            List<Order> orders = OrderRepository.findOrdersCustomer(orderQuery, offset, limit);
            List<Order> ordersTotal = OrderRepository.findOrdersCustomerTotal(orderQuery);
            List<OrderCustomerResponse> orderCustomerResponses = new ArrayList<>();
            for (Order order : orders) {
                OrderCustomerResponse orderCustomerResponse = new OrderCustomerResponse();
                orderCustomerResponse.setOrderNumber(order.getOrderNumber());
                orderCustomerResponse.setStoreName(order.getStore().storeName);
                orderCustomerResponse.setOrderDate(order.getOrderDate());
                orderCustomerResponse.setTotalPrice(order.getTotalPrice());
                orderCustomerResponse.setOrderStatus(order.getStatus());
                orderCustomerResponse.setPaymentStatus(order.getOrderPayment().getStatus());
                orderCustomerResponses.add(orderCustomerResponse);
            }

            response.setBaseResponse(ordersTotal.size(), offset, limit, success, orderCustomerResponses);
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }

    private static String convertOrderStatus(OrderStatus orderStatus) {
        String status = null;
        switch (orderStatus) {
            case NEW_ORDER:
                status = PREPARING;
                break;
            case PROCESS:
                status = ON_PROCESS;
                break;
            case READY_TO_PICKUP:
                status = READY_TO_PICKUP;
                break;
            case DELIVERY:
                status = SERVING;
                break;
            default:
                status = PREPARING;
                break;
        }
        return status;
    }

    public static Result statusOrderMQ(String orderNumber) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                if (orderNumber.equalsIgnoreCase("") || orderNumber == null) {
                    response.setBaseResponse(0, 0, 0, "order number cannot be null or empty.", null);
                    return badRequest(Json.toJson(response));
                }
                Optional<Order> order = OrderRepository.findByOrderNumber(orderNumber);
                if (!order.isPresent()) {
                    response.setBaseResponse(0, 0, 0, "order number does not exists.", null);
                    return badRequest(Json.toJson(response));
                }
                Order getOrder = order.get();

                PaymentInformationResponse paymentInformation = new PaymentInformationResponse();

                Store store = getOrder.getStore();
                paymentInformation.setStoreAddress(store == null ? "-" : store.storeAddress);
                paymentInformation.setStorePhone(store == null ? "-" : store.storePhone);
                
                OrderPayment orderPayment = getOrder.getOrderPayment();
                paymentInformation.setInvoiceNumber(orderPayment.getInvoiceNo());
                paymentInformation.setOrderNumber(getOrder.getOrderNumber());
                paymentInformation.setOrderType(getOrder.getOrderType());
                if(getOrder.getStatus().equalsIgnoreCase("PENDING") || getOrder.getStatus().equalsIgnoreCase("COMPLETE") || getOrder.getStatus().equalsIgnoreCase("NEW_ORDER")) {
                    paymentInformation.setOrderStatus(getOrder.getStatus());
                } else {
                    OrderStatus orderStatus = OrderStatus.convertToOrderStatus(getOrder.getStatus());
                    paymentInformation.setOrderStatus(convertOrderStatus(orderStatus));
                }
                paymentInformation.setOrderDate(getOrder.getOrderDate());
                paymentInformation.setOrderTime(getOrder.getOrderDate());

                List<OrderDetail> orderDetails = getOrder.getOrderDetails();

                List<BrandMerchant> brandMerchantData = BrandMerchantRepository.find.where().eq("merchant", getOrder.getStore().getMerchant()).eq("t0.is_active", Boolean.TRUE).eq("t0.is_deleted", Boolean.FALSE).findList();
                List<PaymentInformationResponse.BrandData> brandDatas = new ArrayList<>();
                for(BrandMerchant bMerchant: brandMerchantData) {
                    List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
                    List<OrderDetail> orderDataDetailByBrand = OrderDetail.find.where().eq("order", getOrder).eq("productMerchant.brandMerchant", bMerchant).findList();
                    if(orderDataDetailByBrand.size() != 0){
                        PaymentInformationResponse.BrandData brandData = new PaymentInformationResponse.BrandData();
                        brandData.setBrandName(bMerchant.getBrandName());
                        for (OrderDetail orderDetail : orderDataDetailByBrand) {
                            OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
                            orderDetailResponse.setProductName(orderDetail.getProductName());
                            orderDetailResponse.setQty(orderDetail.getQuantity());
                            orderDetailResponse.setTotal(orderDetail.getSubTotal());
                            List<OrderDetailAddOnResponse> orderDetailAddOns = new ArrayList<>();
                            List<OrderDetailAddOn> orderDetailAddOnList = orderDetail.getOrderDetailAddOns();
                            for (OrderDetailAddOn orderDetailAddOn : orderDetailAddOnList) {
                                OrderDetailAddOnResponse orderDetailAddOnResponse = new OrderDetailAddOnResponse();
                                orderDetailAddOnResponse.setProductName(orderDetailAddOn.getProductName());
                                orderDetailAddOns.add(orderDetailAddOnResponse);
                            }
                            orderDetailResponse.setOrderDetailAddOns(orderDetailAddOns);
                            orderDetailResponses.add(orderDetailResponse);
                        }
                        brandData.setOrderDetails(orderDetailResponses);
                        brandDatas.add(brandData);
                    }
                }

                paymentInformation.setBrandData(brandDatas);
                paymentInformation.setSubTotal(getOrder.getSubTotal());
                paymentInformation.setTaxPrice(orderPayment.getTaxPrice());
                paymentInformation.setTaxPercentage(orderPayment.getTaxPercentage());
                paymentInformation.setServicePercentage(orderPayment.getServicePercentage());
                paymentInformation.setServiceFee(orderPayment.getServicePrice());

                paymentInformation.setPaymentFeeType(orderPayment.getPaymentFeeType());
                paymentInformation.setPaymentFeeOwner(orderPayment.getPaymentFeeOwner());
                paymentInformation.setPaymentFeeCustomer(orderPayment.getPaymentFeeCustomer());
                paymentInformation.setTotal(getOrder.getTotalPrice());
                paymentInformation.setOrderQueue(getOrder.getOrderQueue());
                paymentInformation.setPaymentStatus(orderPayment.getStatus());
                if (getOrder.getMember() != null) {
                    paymentInformation.setCustomerName(getOrder.getMember().fullName != null ? getOrder.getMember().fullName : getOrder.getMember().firstName + " " + getOrder.getMember().lastName);
                } else {
                    paymentInformation.setCustomerName("GENERAL CUSTOMER " + getOrder.getStore().storeName);
                }

                String messageStatus = "";
                switch(getOrder.getStatus()) {
                    case "PENDING":
                        messageStatus = "Orderan Sedang Menunggu Pembayaran";
                        break;
                    case "NEW_ORDER":
                        messageStatus = "Orderan Anda Terkonfirmasi";
                        break;
                    case "PROCESS":
                        messageStatus = "Orderan Anda Sedang Dimasak";
                        break;
                    case "READY_TO_PICKUP":
                        messageStatus = "Orderan Anda Siap Diambil!";
                        break;
                    case "DELIVERY":
                        messageStatus = "Orderan Anda Sedang Dikirimkan";
                        break;
                    case "CLOSED":
                        messageStatus = "Orderan Anda Selesai";
                        break;
                    case "COMPLETE":
                        messageStatus = "Orderan Anda Selesai";
                        break;
                    case "CANCELED":
                        messageStatus = "Orderan Anda Dibatalkan";
                        break;
                    default:
                        messageStatus = "Orderan Anda Menunggu Pembayaran";
                    }

                response.setBaseResponse(1, offset, limit, messageStatus,
                        paymentInformation);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return unauthorized(Json.toJson(response));
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }

}
