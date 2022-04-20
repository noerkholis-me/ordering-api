package controllers.users;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.api.BaseResponse;
import com.hokeba.http.response.global.ServiceResponse;
import controllers.BaseController;
import dtos.order.OrderTransaction;
import dtos.order.OrderTransactionResponse;
import dtos.order.PaymentDetailResponse;
import dtos.order.ProductOrderDetail;
import dtos.payment.InitiatePaymentRequest;
import dtos.payment.InitiatePaymentResponse;
import dtos.payment.PaymentRequest;
import models.Member;
import models.ProductStore;
import models.transaction.*;
import org.json.JSONObject;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.OrderRepository;
import repository.ProductStoreRepository;
import service.PaymentService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CheckoutOrderController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(CheckoutOrderController.class);

    private static BaseResponse response = new BaseResponse();

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Result checkoutOrder() {
        Member member = checkMemberAccessAuthorization();
        if (member != null) {
            JsonNode jsonNode = request().body().asJson();
            Transaction txn = Ebean.beginTransaction();
            try {
                // request order
                OrderTransaction orderRequest = objectMapper.readValue(jsonNode.toString(), OrderTransaction.class);

                // new oders
                Order order = new Order();
                String orderNumber = Order.generateOrderNumber();
                order.setOrderDate(new Date());
                order.setOrderNumber(orderNumber);
                order.setOrderType(orderRequest.getOrderType());
                order.setStatus(OrderStatus.NEW_ORDER.getStatus());
                order.setMember(member);

                order.save();

                List<ProductOrderDetail> productOrderDetails = orderRequest.getProductOrderDetail();
                StringBuilder message = new StringBuilder();
                List<OrderDetail> orderDetails = new ArrayList<>();
                for (ProductOrderDetail productOrderDetail : productOrderDetails) {
                    Optional<ProductStore> productStore = ProductStoreRepository.findById(productOrderDetail.getProductId());
                    if (!productStore.isPresent()) {
                        message.append("product id ").append(productOrderDetail.getProductId()).append(" not found");
                    }
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setProductStore(productStore.get());
                    orderDetail.setProductName(productStore.get().getProductMerchant().getProductName());
                    orderDetail.setProductPrice(productStore.get().getFinalPrice());
                    orderDetail.setQuantity(productOrderDetail.getProductQty());
                    orderDetail.setOrder(order);
                    orderDetails.add(orderDetail);
                }
                // validate product
                if (!message.toString().isEmpty()) {
                    response.setBaseResponse(0, 0, 0, message.toString(), null);
                    return badRequest(Json.toJson(response));
                }
                BigDecimal subTotal = new BigDecimal(0);
                BigDecimal totalPrice = new BigDecimal(0);
                for (OrderDetail orderDetail : orderDetails) {
                    subTotal = subTotal.add(orderDetail.getProductPrice().multiply(new BigDecimal(orderDetail.getQuantity())));
                    totalPrice = subTotal;
                    orderDetail.save();
                }

                order.setSubTotal(subTotal);
                order.setTotalPrice(totalPrice);
                order.update();

                OrderPayment orderPayment = new OrderPayment();
                orderPayment.setOrder(order);
                orderPayment.setInvoiceNo(OrderPayment.generateInvoiceCode());
                orderPayment.setStatus(PaymentStatus.UNPAID.getStatus());
                orderPayment.setPaymentType(orderRequest.getPaymentDetailResponse().getPaymentType());
                orderPayment.setPaymentChannel(orderRequest.getPaymentDetailResponse().getPaymentChannel());

                BigDecimal totalAmount = new BigDecimal(0);
                BigDecimal fee = orderRequest.getPaymentDetailResponse().getPaymentFee()
                        .add(orderRequest.getPaymentDetailResponse().getPlatformFee()
                        .add(orderRequest.getPaymentDetailResponse().getServiceFee())
                        .add(orderRequest.getPaymentDetailResponse().getTaxFee()));
                totalAmount = totalAmount.add(totalPrice.add(fee));
                orderPayment.setTotalAmount(totalAmount);
                orderPayment.save();

                // do initiate payment
                InitiatePaymentRequest request = new InitiatePaymentRequest();
                request.setOrderNumber(orderNumber);
                request.setDeviceType(orderRequest.getDeviceType());
                PaymentDetailResponse paymentDetailResponse = PaymentDetailResponse.builder()
                        .paymentType(orderRequest.getPaymentDetailResponse().getPaymentType())
                        .paymentChannel(orderRequest.getPaymentDetailResponse().getPaymentChannel())
                        .platformFee(orderRequest.getPaymentDetailResponse().getPlatformFee())
                        .paymentFee(orderRequest.getPaymentDetailResponse().getPaymentFee())
                        .taxFee(orderRequest.getPaymentDetailResponse().getTaxFee())
                        .totalAmount(totalAmount)
                        .build();
                request.setPaymentDetailResponse(paymentDetailResponse);
                request.setProductOrderDetails(productOrderDetails);
                request.setMerchantName(orderRequest.getMerchantName());

                ServiceResponse serviceResponse = PaymentService.getInstance().initiatePayment(request);

                if (serviceResponse.getCode() == 408) {
                    txn.rollback();
                    ObjectNode result = Json.newObject();
                    result.put("error_messages", Json.toJson(new String[]{"Request timeout, please try again later"}));
                    response.setBaseResponse(1, offset, 1, timeOut, result);
                    return badRequest(Json.toJson(response));
                } else {
                    // update payment status
                    order.setStatus(OrderStatus.PROCESS.getStatus());
                    order.update();

                    orderPayment.setStatus(PaymentStatus.PENDING.getStatus());
                    orderPayment.update();

                    String object = objectMapper.writeValueAsString(serviceResponse.getData());
                    JSONObject jsonObject = new JSONObject(object);
                    String initiate = jsonObject.getJSONObject("data").toString();
                    InitiatePaymentResponse initiatePaymentResponse = objectMapper.readValue(initiate, InitiatePaymentResponse.class);


                    PaymentDetail payDetail = new PaymentDetail();
                    payDetail.setOrderNumber(order.getOrderNumber());
                    payDetail.setPaymentChannel(orderRequest.getPaymentDetailResponse().getPaymentChannel());
                    payDetail.setCreationTime(initiatePaymentResponse.getCreationTime());
                    payDetail.setStatus(initiatePaymentResponse.getStatus());
                    payDetail.setQrCode(initiatePaymentResponse.getQrString());
                    payDetail.setReferenceId(initiatePaymentResponse.getReferenceId());
                    payDetail.setTotalAmount(initiatePaymentResponse.getTotalAmount());
                    payDetail.setOrderPayment(orderPayment);
                    payDetail.save();

                    txn.commit();

                    OrderTransactionResponse orderTransactionResponse = new OrderTransactionResponse();
                    orderTransactionResponse.setOrderNumber(order.getOrderNumber());
                    orderTransactionResponse.setInvoiceNumber(order.getOrderPayment().getInvoiceNo());
                    orderTransactionResponse.setQrString(initiatePaymentResponse.getQrString());
                    orderTransactionResponse.setTotalAmount(initiatePaymentResponse.getTotalAmount());
                    orderTransactionResponse.setMerchantName(initiatePaymentResponse.getMerchantName());

                    response.setBaseResponse(1, offset, 1, success, orderTransactionResponse);
                    return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result payment() {
        Member member = checkMemberAccessAuthorization();
        if (member != null) {
            JsonNode jsonNode = request().body().asJson();
            Transaction txn = Ebean.beginTransaction();
            try {
                PaymentRequest paymentRequest = objectMapper.readValue(jsonNode.toString(), PaymentRequest.class);
                Optional<Order> order = OrderRepository.findByOrderNumber(paymentRequest.getOrderNumber());
                if (!order.isPresent()) {
                    response.setBaseResponse(0, 0, 0, inputParameter, null);
                    return badRequest(Json.toJson(response));
                }
                ServiceResponse serviceResponse = PaymentService.getInstance().doPayment(paymentRequest);

                if (serviceResponse.getCode() == 408) {
                    txn.rollback();
                    ObjectNode result = Json.newObject();
                    result.put("error_messages", Json.toJson(new String[]{"Request timeout, please try again later"}));
                    response.setBaseResponse(1, offset, 1, timeOut, result);
                    return badRequest(Json.toJson(response));
                } else {
                    txn.commit();

                    response.setBaseResponse(1, offset, 1, success, serviceResponse.getData());
                    return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }


        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }


}
