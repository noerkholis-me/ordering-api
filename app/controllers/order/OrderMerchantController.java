package controllers.order;

import com.avaje.ebean.Query;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.order.OrderList;
import models.Member;
import models.Merchant;
import models.Store;
import models.transaction.Order;
import models.transaction.OrderDetail;
import models.transaction.OrderDetailAddOn;
import models.transaction.OrderPayment;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.OrderPaymentRepository;
import repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderMerchantController extends BaseController {

    private final static Logger.ALogger LOGGER = Logger.of(OrderMerchantController.class);

    private static BaseResponse response = new BaseResponse();


    public static Result getOrderList(Long storeId, int offset, int limit, String statusOrder) throws Exception {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {

                Query<Order> query = null;
                // default query find by merchant id
                query = OrderRepository.findAllOrderByMerchantId(merchant.id);

                // check store id --> mandatory
                if (storeId != null && storeId != 0L) {
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    query = OrderRepository.findAllOrderByStoreId(storeId);
                }

                Integer totalData = OrderRepository.getTotalData(query);

                List<OrderList> orderLists = new ArrayList<>();
                List<Order> orders = OrderRepository.findAllOrderWithFilter(query, offset, limit, statusOrder);
                if (orders.isEmpty() || orders.size() == 0) {
                    response.setBaseResponse(totalData, offset, limit, success + " Showing data transaction", orderLists);
                    return ok(Json.toJson(response));
                }

                for (Order order : orders) {
                    OrderList orderRes = new OrderList();
                    // looping order
                    System.out.println(">>>>> order in : " + order.id);
                    Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderId(order.id);
                    if (!orderPayment.isPresent()) {
                        System.out.println(">>>>> order payment does not exists <<<<< ");
                        break;
                    }
                    OrderPayment getOrderPayment = orderPayment.get();
                    if (getOrderPayment.getStatus().equalsIgnoreCase("PAID")) {
                        System.out.println(">>>>> Order payment when paid <<<<<");
                        orderRes.setInvoiceNumber(getOrderPayment.getInvoiceNo());
                        orderRes.setOrderNumber(order.getOrderNumber());

                        // get member
                        Member member = null;
                        if (order.getMember() != null) {
                            member = Member.findByIdMember(order.getMember().id);
                        }
                        String customerName = member == null || member.fullName.equalsIgnoreCase("") ? "GENERAL CUSTOMER" : member.fullName;
                        orderRes.setCustomerName(customerName);

                        // get store
                        Store store = Store.findById(order.getStore().id);
                        String merchantName = store == null ? null : store.getMerchant().name;
                        orderRes.setMerchantName(merchantName);

                        orderRes.setTotalAmount(order.getTotalPrice());
                        orderRes.setOrderType(order.getOrderType());
                        orderRes.setOrderQueue(order.getOrderQueue());
                        orderRes.setStatusOrder(order.getStatus());
                        orderRes.setStatus(null);
                        orderRes.setPaymentType(getOrderPayment.getPaymentType());
                        orderRes.setPaymentChannel(getOrderPayment.getPaymentChannel());
                        orderRes.setTotalAmountPayment(getOrderPayment.getTotalAmount());
                        orderRes.setPaymentDate(getOrderPayment.getPaymentDate());

                        List<OrderDetail> orderDetails = OrderRepository.findOrderDetailByOrderId(order.id);
                        List<OrderList.ProductOrderDetail> productOrderDetails = new ArrayList<>();

                        System.out.println(">>>>> loop order detail <<<<<");
                        for (OrderDetail orderDetail : orderDetails) {
                            System.out.println(">>>>> order detail in : " + orderDetail.id);
                            OrderList.ProductOrderDetail productDetail = new OrderList.ProductOrderDetail();
                            productDetail.setProductId(orderDetail.getProductMerchant().id);
                            productDetail.setProductName(orderDetail.getProductName());
                            productDetail.setProductPrice(orderDetail.getProductPrice());
                            productDetail.setProductQty(orderDetail.getQuantity());
                            productDetail.setNotes(orderDetail.getNotes());

                            System.out.println(">>>>> loop order detail add on <<<<<<");
                            List<OrderList.ProductOrderDetail.ProductOrderDetailAddOn> productDetailAddOns = new ArrayList<>();
                            for (OrderDetailAddOn orderDetailAddOn : orderDetail.getOrderDetailAddOns()) {
                                System.out.println(">>>>> order detail add on in : " + orderDetailAddOn.id);
                                OrderList.ProductOrderDetail.ProductOrderDetailAddOn productAddOn = new OrderList.ProductOrderDetail.ProductOrderDetailAddOn();
                                productAddOn.setProductId(orderDetailAddOn.getProductAddOn().getProductMerchant().id);
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
                    }
                    orderLists.add(orderRes);
                }


                System.out.println(">>>>> Total Data : " + totalData);
                System.out.println(">>>>> Total Data Orders : " + orders.size());
                System.out.println(">>>>> order list : " + orderLists.size());

                response.setBaseResponse(totalData, offset, limit, success + " Showing data list order", orderLists);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                LOGGER.error("Error when getting list data orders");
                ex.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }


}
