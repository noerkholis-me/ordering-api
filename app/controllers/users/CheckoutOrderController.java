package controllers.users;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.response.MapOrderStruct;
import controllers.BaseController;
import dtos.order.OrderTransaction;
import dtos.order.ProductOrderDetail;
import models.Member;
import models.ProductStore;
import models.merchant.ProductMerchant;
import models.transaction.*;
import play.libs.Json;
import play.mvc.Result;
import repository.ProductMerchantRepository;
import repository.ProductStoreRepository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CheckoutOrderController extends BaseController {

    private static BaseResponse response = new BaseResponse();

    private static ObjectMapper objectMapper = new ObjectMapper();

    public Result checkoutOrder() {
        Member member = checkMemberAccessAuthorization();
        if (member != null) {
            JsonNode jsonNode = request().body().asJson();
            Transaction txn = Ebean.beginTransaction();
            try {
                // request order
                OrderTransaction orderRequest = objectMapper.readValue(jsonNode.toString(), OrderTransaction.class);

                // new oders
                Order order = new Order();
                order.setOrderDate(new Date());
                order.setOrderNumber(Order.getOrderNumber());
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
                orderPayment.setPaymentType(orderRequest.getPaymentDetail().getPaymentType());
                orderPayment.setPaymentChannel(orderRequest.getPaymentDetail().getPaymentChannel());

                BigDecimal totalAmount = new BigDecimal(0);
                BigDecimal fee = orderRequest.getPaymentDetail().getPaymentFee()
                        .add(orderRequest.getPaymentDetail().getPlatformFee()
                        .add(orderRequest.getPaymentDetail().getServiceFee())
                        .add(orderRequest.getPaymentDetail().getTaxFee()));
                totalAmount = totalAmount.add(totalPrice.add(fee));
                orderPayment.setTotalAmount(totalAmount);
                orderPayment.save();

                // do initiate payment



                txn.commit();

                response.setBaseResponse(1, offset, 1, success, null);
                return ok(Json.toJson(response));
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
