package controllers.order;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.Constant;
import controllers.BaseController;
import dtos.order.*;
import models.Member;
import models.merchant.ProductMerchantDetail;
import models.Store;
import models.UserMerchant;
import models.appsettings.AppSettings;
import models.merchant.FeeSettingMerchant;
import models.transaction.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.AppSettingRepository;
import repository.FeeSettingMerchantRepository;
import repository.OrderPaymentRepository;
import repository.OrderRepository;
import repository.ProductMerchantDetailRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderPosController extends BaseController {

    private final static Logger.ALogger LOGGER = Logger.of(OrderPosController.class);

    private static BaseResponse response = new BaseResponse();

    public static Result getOrderList(Long storeId, int offset, int limit, String customerName, String orderNumber) {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {

            if (storeId == null || storeId == 0L) {
                response.setBaseResponse(0, 0, 0, "store_id tidak boleh kosong", null);
                return badRequest(Json.toJson(response));
            }

            Store store = Store.findById(storeId);
            if (store == null) {
                response.setBaseResponse(0, 0, 0, "toko tidak ditemukan", null);
                return badRequest(Json.toJson(response));
            }

            Query<Order> orderQuery = OrderRepository.findAllOrderByUserMerchantIdAndStoreId(userMerchant.id, store.id);
            List<Order> orders = OrderRepository.findAllOrderByStatusAndCustomerNameAndOrderNumber(orderQuery, offset, limit, OrderStatus.PENDING.getStatus(), customerName, orderNumber);
            Integer totalData = OrderRepository.getTotalOrder(orderQuery);

            if (orders.isEmpty() || orders == null) {
                response.setBaseResponse(0, 0, 0, "data tidak tersedia", new ArrayList<>());
                return ok(Json.toJson(response));
            }

            List<OrderListPosResponse> orderListPosResponses = new ArrayList<>();
            for (Order order : orders) {
                Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderIdAndStatusAndPaymentChannel(order.id, OrderPayment.PENDING);
                if (orderPayment.isPresent()) {
                    OrderListPosResponse orderResponse = new OrderListPosResponse();
                    Member member = order.getMember();
                    if (member == null) {
                        orderResponse.setCustomerName("-");
                    } else {
                        orderResponse.setCustomerName(member.fullName);
                    }

                    orderResponse.setOrderId(order.id);
                    orderResponse.setOrderNumber(order.getOrderNumber());
                    orderResponse.setOrderDate(order.getOrderDate());

                    orderListPosResponses.add(orderResponse);
                }
                continue;
            }

            response.setBaseResponse(totalData, offset, limit, success + " menampilkan data order", orderListPosResponses);
            return ok(Json.toJson(response));
        }
    }

    public static Result getOrderDetail(String orderNumber) {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {

            Optional<Order> order = OrderRepository.findByOrderNumber(orderNumber);
            if (!order.isPresent()) {
                response.setBaseResponse(0, 0, 0, "data order dengan order_number " + orderNumber + " tidak tersedia", null);
                return badRequest(Json.toJson(response));
            }

            Order getOrder = order.get();

            OrderDetailPosResponse orderResponse = new OrderDetailPosResponse();
            orderResponse.setOrderNumber(getOrder.getOrderNumber());
            orderResponse.setOrderDate(getOrder.getOrderDate());
            Member member = getOrder.getMember();
            if (member == null) {
                orderResponse.setCustomerName("-");
                orderResponse.setCustomerPhone("-");
            } else {
                orderResponse.setCustomerName(member.fullName);
                orderResponse.setCustomerPhone(member.phone);
            }

            List<OrderDetail> orderDetails = getOrder.getOrderDetails();
            List<ProductDetailPosResponse> productDetailPosResponses = new ArrayList<>();
            for (OrderDetail orderDetail : orderDetails) {
                ProductMerchantDetail pMD = ProductMerchantDetailRepository.findByProduct(orderDetail.getProductMerchant());
                ProductDetailPosResponse productDetailPosResponse = new ProductDetailPosResponse();
                productDetailPosResponse.setProductName(orderDetail.getProductName());
                productDetailPosResponse.setProductPrice(orderDetail.getProductPrice());
                productDetailPosResponse.setQty(orderDetail.getQuantity());
                productDetailPosResponse.setImageUrl(pMD.getProductImageMain());

                List<OrderDetailAddOn> orderDetailAddOns = orderDetail.getOrderDetailAddOns();
                List<ProductDetailPosResponse.ProductAddOnPosResponse> productAddOnPosResponses = new ArrayList<>();
                for (OrderDetailAddOn orderDetailAddOn : orderDetailAddOns) {
                    ProductMerchantDetail pMDAddon = ProductMerchantDetailRepository.findByProduct(orderDetailAddOn.getOrderDetail().getProductMerchant());
                    ProductDetailPosResponse.ProductAddOnPosResponse productAddOnPosResponse = new ProductDetailPosResponse.ProductAddOnPosResponse();
                    if (orderDetailAddOn == null) {
                        continue;
                    } else {
                        productAddOnPosResponse.setProductName(orderDetailAddOn.getProductName());
                        productAddOnPosResponse.setProductPrice(orderDetailAddOn.getProductPrice());
                        productAddOnPosResponse.setQty(orderDetailAddOn.getQuantity());
                        productAddOnPosResponse.setImageUrl(pMDAddon.getProductImageMain());
                        productAddOnPosResponses.add(productAddOnPosResponse);
                    }
                }
                productDetailPosResponse.setProductAddOnPosResponses(productAddOnPosResponses);
                productDetailPosResponses.add(productDetailPosResponse);
            }
            orderResponse.setProductDetailPosResponses(productDetailPosResponses);

            OrderPayment orderPayment = getOrder.getOrderPayment();

            orderResponse.setSubTotal(getOrder.getSubTotal());
            orderResponse.setTaxPrice(orderPayment.getTaxPrice());
            orderResponse.setTaxPercentage(orderPayment.getTaxPercentage());
            orderResponse.setPaymentFeeOwner(orderPayment.getPaymentFeeOwner());
            orderResponse.setServicePercentage(orderPayment.getServicePercentage());
            orderResponse.setTotal(getOrder.getTotalPrice());
            orderResponse.setPaymentType(orderPayment.getPaymentChannel());


            response.setBaseResponse(1, 0, 1, success + " menampilkan data order", orderResponse);
            return ok(Json.toJson(response));
        }
    }

    public static Result printInvoicePaid(String orderNumber, String referenceNumber) {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {
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

                invoicePrintResponse.setImageStoreUrl(appSettings.getAppLogo() != null && appSettings.getAppLogo() != "" ? appSettings.getAppLogo() : null);

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
                if (referenceNumber == null || referenceNumber.equalsIgnoreCase("")) {
                    invoicePrintResponse.setReferenceNumber("-");
                } else {
                    invoicePrintResponse.setReferenceNumber(referenceNumber);
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    getOrder.setStatus(Order.NEW_ORDER);
                    getOrder.update();

                    orderPayment.setStatus(OrderPayment.PAID);
                    orderPayment.update();

                    trx.commit();

                    invoicePrintResponse.setPaymentStatus(orderPayment.getStatus());
                    response.setBaseResponse(1, offset, 1, success + " success showing data invoice.",
                            invoicePrintResponse);
                    return ok(Json.toJson(response));
                } catch (Exception ex) {
                    LOGGER.error("Error while update order", ex);
                    ex.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, inputParameter, null);
        return badRequest(Json.toJson(response));
    }

}
