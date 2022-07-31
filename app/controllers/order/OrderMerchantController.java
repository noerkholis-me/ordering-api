package controllers.order;

import com.avaje.ebean.Query;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.Constant;
import controllers.BaseController;
import dtos.order.InvoicePrintResponse;
import dtos.order.OrderDetailAddOnResponse;
import dtos.order.OrderDetailResponse;
import dtos.order.OrderList;
import models.Member;
import models.Merchant;
import models.Store;
import models.appsettings.AppSettings;
import models.internal.FeeSetting;
import models.merchant.FeeSettingMerchant;
import models.transaction.Order;
import models.transaction.OrderDetail;
import models.transaction.OrderDetailAddOn;
import models.transaction.OrderPayment;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.AppSettingRepository;
import repository.FeeSettingMerchantRepository;
import repository.OrderPaymentRepository;
import repository.OrderRepository;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.io.File;
import java.io.FileOutputStream;

import service.DownloadOrderReport;

public class OrderMerchantController extends BaseController {

    private final static Logger.ALogger LOGGER = Logger.of(OrderMerchantController.class);

    private static BaseResponse response = new BaseResponse();

    public static Result getOrderList(Long storeId, int offset, int limit, String statusOrder) throws Exception {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {

                Query<Order> query = null;
                // default query find by merchant id
                query = OrderRepository.findAllOrderByMerchantId(merchant);

                // check store id --> mandatory
                if (storeId != null && storeId != 0L) {
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    query = OrderRepository.findAllOrderByStoreId(storeId);
                }

                Integer totalData = OrderRepository.getTotalData(query, statusOrder);

                List<OrderList> orderLists = new ArrayList<>();
                List<Order> orders = OrderRepository.findAllOrderWithFilter(query, offset, limit, statusOrder);
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
                    if (getOrderPayment.getStatus().equalsIgnoreCase("PAID") || statusOrder == "CANCELED") {
                        // System.out.println(">>>>> Order payment when paid <<<<<");
                        orderRes.setInvoiceNumber(getOrderPayment.getInvoiceNo());
                        orderRes.setOrderNumber(order.getOrderNumber());

                        // get member
                        Member member = null;
                        if (order.getMember() != null) {
                            member = Member.findByIdMember(order.getMember().id);
                        }
                        String customerName = member == null || member.fullName.equalsIgnoreCase("")
                                ? "GENERAL CUSTOMER (" + order.getStore().storeName + ")"
                                : member.fullName;
                        orderRes.setCustomerName(customerName);

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
                        }
                        String customerName = member == null || member.fullName.equalsIgnoreCase("")
                                ? "GENERAL CUSTOMER (" + order.getStore().storeName + ")"
                                : member.fullName;
                        orderRes.setCustomerName(customerName);

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

                Query<Order> query = null;
                // default query find by merchant id
                if (startDate != null) {
                    query = OrderRepository.findAllOrderReportWithFilter(merchant, startDate, endDate);
                } else {
                    query = OrderRepository.findAllOrderByMerchantId(merchant);
                }

                // check store id --> mandatory
                if (storeId != null && storeId != 0L) {
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    query = OrderRepository.findAllOrderByStoreId(storeId);
                }

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
                    if (getOrderPayment.getStatus().equalsIgnoreCase("PAID")
                            || getOrderPayment.getStatus().equalsIgnoreCase("CANCEL")
                            || getOrderPayment.getStatus().equalsIgnoreCase("CANCELED")) {
                        // System.out.println(">>>>> Order payment when paid <<<<<");
                        orderRes.setInvoiceNumber(getOrderPayment.getInvoiceNo());
                        orderRes.setOrderNumber(order.getOrderNumber());

                        // get member
                        Member member = null;
                        if (order.getMember() != null) {
                            member = Member.findByIdMember(order.getMember().id);
                        }
                        String customerName = member == null || member.fullName.equalsIgnoreCase("")
                                ? "GENERAL CUSTOMER (" + order.getStore().storeName + ")"
                                : member.fullName;
                        orderRes.setCustomerName(customerName);

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

                Query<Order> query = null;
                // default query find by merchant id
                if (startDate != null) {
                    query = OrderRepository.findAllOrderReportWithFilter(merchant, startDate, endDate);
                } else {
                    query = OrderRepository.findAllOrderByMerchantId(merchant);
                }

                // check store id --> mandatory
                if (storeId != null && storeId != 0L) {
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    query = OrderRepository.findAllOrderByStoreId(storeId);
                }

                Integer totalData = OrderRepository.getTotalData(query, statusOrder);

                List<OrderList> orderLists = new ArrayList<>();
                List<Order> orders = OrderRepository.findAllOrderWithFilter(query, offset, limit, statusOrder);
                if (orders.isEmpty() || orders.size() == 0) {
                    response.setBaseResponse(totalData, offset, limit, success + " Showing data transaction",
                            orderLists);
                    return ok(Json.toJson(response));
                }

                File file = DownloadOrderReport.downloadOrderReport(orders);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                String filenameOrderReport = "OrderReport-" + simpleDateFormat.format(new Date()).toString() + ".xlsx";
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
                if (appSettings == null) {
                    String sandboxImage = Constant.getInstance().getImageUrl()
                            .concat("/assets/images/logo-sandbox.png");
                    invoicePrintResponse.setImageStoreUrl(sandboxImage);
                }

                invoicePrintResponse.setImageStoreUrl(appSettings.getAppLogo());

                invoicePrintResponse.setStoreName(store.storeName);
                invoicePrintResponse.setStoreAddress(store.storeAddress);
                invoicePrintResponse.setStorePhoneNumber(store.storePhone);

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
                invoicePrintResponse.setOrderDetails(orderDetailResponses);
                invoicePrintResponse.setSubTotal(getOrder.getSubTotal());
                invoicePrintResponse.setTaxPrice(orderPayment.getTaxPrice());

                Optional<FeeSettingMerchant> feeSetting = FeeSettingMerchantRepository
                        .findByLatestFeeSetting(store.getMerchant().id);
                if (!feeSetting.isPresent()) {
                    invoicePrintResponse.setTaxPercentage(feeSetting.get().getTax());
                    invoicePrintResponse.setServicePercentage(feeSetting.get().getService());
                }
                invoicePrintResponse.setTaxPercentage(11D);
                invoicePrintResponse.setServicePercentage(0D);

                invoicePrintResponse.setPaymentFeeOwner(orderPayment.getPaymentFeeOwner());
                invoicePrintResponse.setPaymentFeeCustomer(orderPayment.getPaymentFeeCustomer());
                invoicePrintResponse.setTotal(getOrder.getTotalPrice());
                invoicePrintResponse.setOrderQueue(getOrder.getOrderQueue());
                invoicePrintResponse.setPaymentStatus(orderPayment.getStatus());
                invoicePrintResponse.setReferenceNumber("-");

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

}
